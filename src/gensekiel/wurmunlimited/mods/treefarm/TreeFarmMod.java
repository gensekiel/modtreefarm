package gensekiel.wurmunlimited.mods.treefarm;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.CtPrimitiveType;
import javassist.bytecode.Descriptor;

public class TreeFarmMod implements
	WurmServerMod,
	Initable,
	PreInitable,
	Configurable,
	ServerStartedListener
{
//======================================================================
	private static String wrapper_code = ""
	+ "public static void wrap_checkForTreeGrowth("
	+ "   int tile, int tilex, int tiley, byte type, byte aData)"
	+ "{"
	+ "   logger.log(Level.INFO, \"Injected method entered.\");"
	+ "   boolean pollingSurface_old = pollingSurface;"
	+ "   MeshIO currentMesh_old = currentMesh;"
	+ "   pollingSurface = true;"
	+ "   currentMesh = Server.surfaceMesh;"
	+ ""
	+ "   checkForTreeGrowth(tile, tilex, tiley, type, aData);"
	+ ""
	+ "   pollingSurface = pollingSurface_old;"
	+ "   currentMesh = currentMesh_old;"
	+ "   logger.log(Level.INFO, \"Injected method left.\");"
	+ "}";
//======================================================================	
	private static Logger logger = Logger.getLogger(TreeFarmMod.class.getName());
	private static WateringAction wateringaction = new WateringAction();
	private static FertilizingAction fertilizingaction = new FertilizingAction();
	private static boolean allowGrow = true;
	private static boolean allowFertilize = true;
//======================================================================
	@Override
	public void onServerStarted()
	{
		wateringaction.registerAction();
		fertilizingaction.registerAction();
		
		if(allowGrow) ModActions.registerAction(wateringaction);
		if(allowFertilize) ModActions.registerAction(fertilizingaction);
		
		boolean debug = false;
		if(debug){
			WateringAction wa2 = new WateringAction("WaterEX");
			FertilizingAction fa2 = new FertilizingAction("FertilizeEX");
	
			wa2.setCost(0);
			wa2.setTime(0);
			wa2.setItem(0);
	
			fa2.setCost(0);
			fa2.setTime(0);
			fa2.setItem(0);
	
			wa2.registerAction();
			fa2.registerAction();
	
			ModActions.registerAction(wa2);
			ModActions.registerAction(fa2);
		}
		
		logger.log(Level.INFO, "Actions registered.");
	}
//======================================================================
	@Override
	public void init()
	{
		if(GrowTask.getUseOriginalGrowthFunction()){
			try{
				ClassPool pool = ClassPool.getDefault();
				pool.importPackage("java.util.logging");
				pool.importPackage("com.wurmonline.mesh");
				pool.importPackage("com.wurmonline.server");
				CtClass ctclass = pool.get("com.wurmonline.server.zones.TilePoller");
				CtMethod wrapper_method = CtNewMethod.make(wrapper_code, ctclass);
				ctclass.addMethod(wrapper_method);
				logger.log(Level.INFO, "Wrapper method injected.");
			}catch(Exception e){
				logger.log(Level.WARNING, "Wrapper injection failed. Falling back to builtin growth function. Exception: " + e);
				GrowTask.setUseOriginalGrowthFunction(false);
			}
		}
		
		// We need the server paths to be set properly before initializing
		// the TreeTilePoller.
		// TODO Should be okay to do that in preinit.
		try{
			HookManager.getInstance().registerHook("com.wurmonline.server.zones.CropTilePoller", "initializeFields", Descriptor.ofMethod(CtPrimitiveType.voidType, null), new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler() {
						@Override
						public Object invoke(Object object, Method method, Object[] args) throws Throwable {
							TreeTilePoller.initialize();
							return method.invoke(object, args);
						}
					};
				}
			});
		}catch(Exception e){
			logger.log(Level.WARNING, "Initializing hook failed. Tree list will not be loaded. Exception: " + e);
		}

		try{
			HookManager.getInstance().registerHook("com.wurmonline.server.zones.CropTilePoller", "pollCropTiles", Descriptor.ofMethod(CtPrimitiveType.voidType, null), new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler() {
						@Override
						public Object invoke(Object object, Method method, Object[] args) throws Throwable {
							TreeTilePoller.pollTreeTiles();
							return method.invoke(object, args);
						}
					};
				}
			});
		}catch(Exception e){
			logger.log(Level.SEVERE, "Polling hook failed. TreeTilePoller will not run. Exception: " + e);
		}
		
		try{
			// Don't hook shutDown directly as it raises an exception on
			// purpose and that may make it look like this mod is somehow
			// responsible.
//			HookManager.getInstance().registerHook("com.wurmonline.server.Server", "shutDown", Descriptor.ofMethod(CtPrimitiveType.voidType, null), new InvocationHandlerFactory(){
			HookManager.getInstance().registerHook("com.wurmonline.server.zones.Zones", "saveAllZones", Descriptor.ofMethod(CtPrimitiveType.voidType, null), new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler() {
						@Override
						public Object invoke(Object object, Method method, Object[] args) throws Throwable {
							TreeTilePoller.dumpTreeList();
							return method.invoke(object, args);
						}
					};
				}
			});
		}catch(Exception e){
			logger.log(Level.WARNING, "Tree list hook failed. Tree list will not be saved. Exception: " + e);
		}
	}
//======================================================================
	@Override
	public void preInit()
	{
		ModActions.init();
	}
//======================================================================
	private Integer getOption(String option, Integer default_value, Properties properties){ return Integer.valueOf(properties.getProperty(option, Integer.toString(default_value))); }
	private Double getOption(String option, Double default_value, Properties properties){ return Double.valueOf(properties.getProperty(option, Double.toString(default_value))); }
	private Long getOption(String option, Long default_value, Properties properties){ return Long.valueOf(properties.getProperty(option, Long.toString(default_value))); }
	private Boolean getOption(String option, Boolean default_value, Properties properties){ return Boolean.valueOf(properties.getProperty(option, Boolean.toString(default_value))); }
	private Byte getOption(String option, Byte default_value, Properties properties){ return Byte.valueOf(properties.getProperty(option, Byte.toString(default_value))); }
//======================================================================
	@Override
	public void configure(Properties properties)
	{
		allowGrow = getOption("AllowGrow", allowGrow, properties);
		allowFertilize = getOption("AllowFertilize", allowFertilize, properties);

		AbstractAction.setAllowTrees(getOption("AllowTrees", AbstractAction.getAllowTrees(), properties));
		AbstractAction.setAllowBushes(getOption("AllowBushes", AbstractAction.getAllowBushes(), properties));
		
		wateringaction.setCost(getOption("WateringCost", wateringaction.getCost(), properties));
		wateringaction.setTime(getOption("WateringTime", wateringaction.getTime(), properties));
		wateringaction.setItem(getOption("WateringItem", wateringaction.getItem(), properties));

		fertilizingaction.setCost(getOption("FertilizingCost", fertilizingaction.getCost(), properties));
		fertilizingaction.setTime(getOption("FertilizingTime", fertilizingaction.getTime(), properties));
		fertilizingaction.setItem(getOption("FertilizingItem", fertilizingaction.getItem(), properties));

		AbstractAction.setCostSkillMultiplier(getOption("CostSkillMultiplier", AbstractAction.getCostSkillMultiplier(), properties));
		AbstractAction.setCostAgeMultiplier(getOption("CostAgeMultiplier", AbstractAction.getCostAgeMultiplier(), properties));
		AbstractAction.setTimeSkillMultiplier(getOption("TimeSkillMultiplier", AbstractAction.getTimeSkillMultiplier(), properties));
		AbstractAction.setGrowthTimeQualityMultiplier(getOption("GrowthTimeQualityMultiplier", AbstractAction.getGrowthTimeQualityMultiplier(), properties));
		AbstractAction.setGrowthTimeSkillMultiplier(getOption("GrowthTimeSkillMultiplier", AbstractAction.getGrowthTimeSkillMultiplier(), properties));
		AbstractAction.setGainSkill(getOption("GainSkill", AbstractAction.getGainSkill(), properties));

		AbstractAction.setCheckIfPolled(getOption("CheckIfPolled", AbstractAction.getCheckIfPolled(), properties));
		AbstractAction.setCheckConditions(getOption("CheckConditions", AbstractAction.getCheckConditions(), properties));

		TreeTilePoller.setPollInterval(getOption("PollInterval", TreeTilePoller.getPollInterval(), properties));
		TreeTilePoller.setPreserveTreeList(getOption("PreserveTreeList", TreeTilePoller.getPreserveTreeList(), properties));

		GrowTask.setAgeLimit(getOption("AgeLimit", GrowTask.getAgeLimit(), properties));
		GrowTask.setKeepGrowing(getOption("KeepGrowing", GrowTask.getKeepGrowing(), properties));
		GrowTask.setCheckForWUPoll(getOption("CheckForWUPoll", GrowTask.getCheckForWUPoll(), properties));
		GrowTask.setUseOriginalGrowthFunction(getOption("UseOriginalGrowthFunction", GrowTask.getUseOriginalGrowthFunction(), properties));
		
		TreeTile.setBaseGrowthTime(getOption("BaseGrowthTime", TreeTile.getBaseGrowthTime(), properties));

		TreeTile.setGrowthModifierTree    (getOption("GrowthModifierTree",     TreeTile.getGrowthModifierTree(),     properties));
		TreeTile.setGrowthModifierBush    (getOption("GrowthModifierBush",     TreeTile.getGrowthModifierBush(),     properties));
		
		GrowTask .setGrowthModifier(getOption("GrowthModifierGrow",  GrowTask .getGrowthModifier(), properties));
		FruitTask.setGrowthModifier(getOption("GrowthModifierFruit", FruitTask.getGrowthModifier(), properties));

		TreeTile.setGrowthModifierBirch   (getOption("GrowthModifierBirch",    TreeTile.getGrowthModifierBirch(),    properties));
		TreeTile.setGrowthModifierPine    (getOption("GrowthModifierPine",     TreeTile.getGrowthModifierPine(),     properties));
		TreeTile.setGrowthModifierOak     (getOption("GrowthModifierOak",      TreeTile.getGrowthModifierOak(),      properties));
		TreeTile.setGrowthModifierCedar   (getOption("GrowthModifierCedar",    TreeTile.getGrowthModifierCedar(),    properties));
		TreeTile.setGrowthModifierWillow  (getOption("GrowthModifierWillow",   TreeTile.getGrowthModifierWillow(),   properties));
		TreeTile.setGrowthModifierMaple   (getOption("GrowthModifierMaple",    TreeTile.getGrowthModifierMaple(),    properties));
		TreeTile.setGrowthModifierApple   (getOption("GrowthModifierApple",    TreeTile.getGrowthModifierApple(),    properties));
		TreeTile.setGrowthModifierLemon   (getOption("GrowthModifierLemon",    TreeTile.getGrowthModifierLemon(),    properties));
		TreeTile.setGrowthModifierOlive   (getOption("GrowthModifierOlive",    TreeTile.getGrowthModifierOlive(),    properties));
		TreeTile.setGrowthModifierCherry  (getOption("GrowthModifierCherry",   TreeTile.getGrowthModifierCherry(),   properties));
		TreeTile.setGrowthModifierChestnut(getOption("GrowthModifierChestnut", TreeTile.getGrowthModifierChestnut(), properties));
		TreeTile.setGrowthModifierWalnut  (getOption("GrowthModifierWalnut",   TreeTile.getGrowthModifierWalnut(),   properties));
		TreeTile.setGrowthModifierFir     (getOption("GrowthModifierFir",      TreeTile.getGrowthModifierFir(),      properties));
		TreeTile.setGrowthModifierLinden  (getOption("GrowthModifierLinden",   TreeTile.getGrowthModifierLinden(),   properties));

		TreeTile.setGrowthModifierNormal   (getOption("GrowthModifierNormal",    TreeTile.getGrowthModifierNormal(),    properties));
		TreeTile.setGrowthModifierEnchanted(getOption("GrowthModifierEnchanted", TreeTile.getGrowthModifierEnchanted(), properties));
		TreeTile.setGrowthModifierMycelium (getOption("GrowthModifierMycelium",  TreeTile.getGrowthModifierMycelium(),  properties));
		
		TreeTile.setGrowthModifierCamellia(getOption("GrowthModifierCamellia", TreeTile.getGrowthModifierCamellia(), properties));
		TreeTile.setGrowthModifierGrape   (getOption("GrowthModifierGrape",    TreeTile.getGrowthModifierGrape(),    properties));
		TreeTile.setGrowthModifierLavender(getOption("GrowthModifierLavender", TreeTile.getGrowthModifierLavender(), properties));
		TreeTile.setGrowthModifierOleander(getOption("GrowthModifierOleander", TreeTile.getGrowthModifierOleander(), properties));
		TreeTile.setGrowthModifierRose    (getOption("GrowthModifierRose",     TreeTile.getGrowthModifierRose(),     properties));
		TreeTile.setGrowthModifierThorn   (getOption("GrowthModifierThorn",    TreeTile.getGrowthModifierThorn(),    properties));
		
		TreeTile.setGrowthModifierAge( 0, getOption("GrowthModifierAge0",  TreeTile.getGrowthModifierAge( 0), properties));
		TreeTile.setGrowthModifierAge( 1, getOption("GrowthModifierAge1",  TreeTile.getGrowthModifierAge( 1), properties));
		TreeTile.setGrowthModifierAge( 2, getOption("GrowthModifierAge2",  TreeTile.getGrowthModifierAge( 2), properties));
		TreeTile.setGrowthModifierAge( 3, getOption("GrowthModifierAge3",  TreeTile.getGrowthModifierAge( 3), properties));
		TreeTile.setGrowthModifierAge( 4, getOption("GrowthModifierAge4",  TreeTile.getGrowthModifierAge( 4), properties));
		TreeTile.setGrowthModifierAge( 5, getOption("GrowthModifierAge5",  TreeTile.getGrowthModifierAge( 5), properties));
		TreeTile.setGrowthModifierAge( 6, getOption("GrowthModifierAge6",  TreeTile.getGrowthModifierAge( 6), properties));
		TreeTile.setGrowthModifierAge( 7, getOption("GrowthModifierAge7",  TreeTile.getGrowthModifierAge( 7), properties));
		TreeTile.setGrowthModifierAge( 8, getOption("GrowthModifierAge8",  TreeTile.getGrowthModifierAge( 8), properties));
		TreeTile.setGrowthModifierAge( 9, getOption("GrowthModifierAge9",  TreeTile.getGrowthModifierAge( 9), properties));
		TreeTile.setGrowthModifierAge(10, getOption("GrowthModifierAge10", TreeTile.getGrowthModifierAge(10), properties));
		TreeTile.setGrowthModifierAge(11, getOption("GrowthModifierAge11", TreeTile.getGrowthModifierAge(11), properties));
		TreeTile.setGrowthModifierAge(12, getOption("GrowthModifierAge12", TreeTile.getGrowthModifierAge(12), properties));
		TreeTile.setGrowthModifierAge(13, getOption("GrowthModifierAge13", TreeTile.getGrowthModifierAge(13), properties));
		TreeTile.setGrowthModifierAge(14, getOption("GrowthModifierAge14", TreeTile.getGrowthModifierAge(14), properties));
	}
//======================================================================
}

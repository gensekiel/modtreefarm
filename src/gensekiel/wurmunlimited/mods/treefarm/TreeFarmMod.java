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
		// the TaskPoller.
		// TODO Should be okay to do that in preinit.
		try{
			HookManager.getInstance().registerHook("com.wurmonline.server.zones.CropTilePoller", "initializeFields", Descriptor.ofMethod(CtPrimitiveType.voidType, null), new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler() {
						@Override
						public Object invoke(Object object, Method method, Object[] args) throws Throwable {
							TaskPoller.initialize();
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
							TaskPoller.poll();
							return method.invoke(object, args);
						}
					};
				}
			});
		}catch(Exception e){
			logger.log(Level.SEVERE, "Polling hook failed. TaskPoller will not run. Exception: " + e);
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
							TaskPoller.dumpTreeList();
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

		TaskPoller.setPollInterval(getOption("PollInterval", TaskPoller.getPollInterval(), properties));
		TaskPoller.setPreserveList(getOption("PreserveList", TaskPoller.getPreserveList(), properties));

		GrowTask.setAgeLimit(getOption("AgeLimit", GrowTask.getAgeLimit(), properties));
		GrowTask.setKeepGrowing(getOption("KeepGrowing", GrowTask.getKeepGrowing(), properties));
		GrowTask.setCheckForWUPoll(getOption("CheckForWUPoll", GrowTask.getCheckForWUPoll(), properties));
		GrowTask.setUseOriginalGrowthFunction(getOption("UseOriginalGrowthFunction", GrowTask.getUseOriginalGrowthFunction(), properties));
		
		AbstractTask.setBaseGrowthTime(getOption("BaseGrowthTime", AbstractTask.getBaseGrowthTime(), properties));
		
		GrowTask .setGrowthModifier(getOption("GrowthModifierGrow",  GrowTask .getGrowthModifier(), properties));
		FruitTask.setGrowthModifier(getOption("GrowthModifierFruit", FruitTask.getGrowthModifier(), properties));

		TreeTileTask.setGrowthModifierTree(getOption("GrowthModifierTree", TreeTileTask.getGrowthModifierTree(), properties));
		TreeTileTask.setGrowthModifierBush(getOption("GrowthModifierBush", TreeTileTask.getGrowthModifierBush(), properties));

		TreeTileTask.setGrowthModifierBirch   (getOption("GrowthModifierBirch",    TreeTileTask.getGrowthModifierBirch(),    properties));
		TreeTileTask.setGrowthModifierPine    (getOption("GrowthModifierPine",     TreeTileTask.getGrowthModifierPine(),     properties));
		TreeTileTask.setGrowthModifierOak     (getOption("GrowthModifierOak",      TreeTileTask.getGrowthModifierOak(),      properties));
		TreeTileTask.setGrowthModifierCedar   (getOption("GrowthModifierCedar",    TreeTileTask.getGrowthModifierCedar(),    properties));
		TreeTileTask.setGrowthModifierWillow  (getOption("GrowthModifierWillow",   TreeTileTask.getGrowthModifierWillow(),   properties));
		TreeTileTask.setGrowthModifierMaple   (getOption("GrowthModifierMaple",    TreeTileTask.getGrowthModifierMaple(),    properties));
		TreeTileTask.setGrowthModifierApple   (getOption("GrowthModifierApple",    TreeTileTask.getGrowthModifierApple(),    properties));
		TreeTileTask.setGrowthModifierLemon   (getOption("GrowthModifierLemon",    TreeTileTask.getGrowthModifierLemon(),    properties));
		TreeTileTask.setGrowthModifierOlive   (getOption("GrowthModifierOlive",    TreeTileTask.getGrowthModifierOlive(),    properties));
		TreeTileTask.setGrowthModifierCherry  (getOption("GrowthModifierCherry",   TreeTileTask.getGrowthModifierCherry(),   properties));
		TreeTileTask.setGrowthModifierChestnut(getOption("GrowthModifierChestnut", TreeTileTask.getGrowthModifierChestnut(), properties));
		TreeTileTask.setGrowthModifierWalnut  (getOption("GrowthModifierWalnut",   TreeTileTask.getGrowthModifierWalnut(),   properties));
		TreeTileTask.setGrowthModifierFir     (getOption("GrowthModifierFir",      TreeTileTask.getGrowthModifierFir(),      properties));
		TreeTileTask.setGrowthModifierLinden  (getOption("GrowthModifierLinden",   TreeTileTask.getGrowthModifierLinden(),   properties));

		TreeTileTask.setGrowthModifierNormal   (getOption("GrowthModifierNormal",    TreeTileTask.getGrowthModifierNormal(),    properties));
		TreeTileTask.setGrowthModifierEnchanted(getOption("GrowthModifierEnchanted", TreeTileTask.getGrowthModifierEnchanted(), properties));
		TreeTileTask.setGrowthModifierMycelium (getOption("GrowthModifierMycelium",  TreeTileTask.getGrowthModifierMycelium(),  properties));
		
		TreeTileTask.setGrowthModifierCamellia(getOption("GrowthModifierCamellia", TreeTileTask.getGrowthModifierCamellia(), properties));
		TreeTileTask.setGrowthModifierGrape   (getOption("GrowthModifierGrape",    TreeTileTask.getGrowthModifierGrape(),    properties));
		TreeTileTask.setGrowthModifierLavender(getOption("GrowthModifierLavender", TreeTileTask.getGrowthModifierLavender(), properties));
		TreeTileTask.setGrowthModifierOleander(getOption("GrowthModifierOleander", TreeTileTask.getGrowthModifierOleander(), properties));
		TreeTileTask.setGrowthModifierRose    (getOption("GrowthModifierRose",     TreeTileTask.getGrowthModifierRose(),     properties));
		TreeTileTask.setGrowthModifierThorn   (getOption("GrowthModifierThorn",    TreeTileTask.getGrowthModifierThorn(),    properties));
		
		for(int i = 0; i < 15; i++){
			TreeTileTask.setGrowthModifierAge( i, getOption("GrowthModifierAge" + i,  TreeTileTask.getGrowthModifierAge(i), properties));
		}
	}
//======================================================================
}

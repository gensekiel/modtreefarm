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
	
	private static Logger logger = Logger.getLogger(TreeFarmMod.class.getName());
//======================================================================
	@Override
	public void onServerStarted()
	{
		ModActions.registerAction(new WateringAction());
		logger.log(Level.INFO, "Watering action registered.");
	}
//======================================================================
	@Override
	public void init()
	{
		if(TreeTilePoller.getUseOriginalGrowthFunction()){
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
				TreeTilePoller.setUseOriginalGrowthFunction(false);
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
			logger.log(Level.WARNING, "Initializing hook failed. Tree list not loaded. Exception: " + e);
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
	@Override
	public void configure(Properties properties)
	{
		WateringAction.setWateringCost(Integer.valueOf(properties.getProperty("WateringCost", Integer.toString(WateringAction.getWateringCost()))));
		WateringAction.setWateringTime(Integer.valueOf(properties.getProperty("WateringTime", Integer.toString(WateringAction.getWateringTime()))));
		WateringAction.setWateringItem(Integer.valueOf(properties.getProperty("WateringItem", Integer.toString(WateringAction.getWateringItem()))));
		WateringAction.setWateringCheck(Boolean.valueOf(properties.getProperty("WateringCheck", Boolean.toString(WateringAction.getWateringCheck()))));
		WateringAction.setTreeAgeCheck(Boolean.valueOf(properties.getProperty("TreeAgeCheck", Boolean.toString(WateringAction.getTreeAgeCheck()))));

		TreeTilePoller.setPollInterval(Long.valueOf(properties.getProperty("PollInterval", Long.toString(TreeTilePoller.getPollInterval()))));
		TreeTilePoller.setAgeLimit(Byte.valueOf(properties.getProperty("AgeLimit", Byte.toString(TreeTilePoller.getAgeLimit()))));
		TreeTilePoller.setKeepGrowing(Boolean.valueOf(properties.getProperty("KeepGrowing", Boolean.toString(TreeTilePoller.getKeepGrowing()))));
		TreeTilePoller.setUseOriginalGrowthFunction(Boolean.valueOf(properties.getProperty("UseOriginalGrowthFunction", Boolean.toString(TreeTilePoller.getUseOriginalGrowthFunction()))));
		TreeTilePoller.setPreserveTreeList(Boolean.valueOf(properties.getProperty("PreserveTreeList", Boolean.toString(TreeTilePoller.getPreserveTreeList()))));
		TreeTilePoller.setCheckForWUPoll(Boolean.valueOf(properties.getProperty("CheckForWUPoll", Boolean.toString(TreeTilePoller.getCheckForWUPoll()))));
		
		TreeTile.setBaseGrowthTime(Long.valueOf(properties.getProperty("BaseGrowthTime", Long.toString(TreeTile.getBaseGrowthTime()))));
		
		TreeTile.setGrowthModifierBirch(Double.valueOf(properties.getProperty("GrowthModifierBirch", Double.toString(TreeTile.getGrowthModifierBirch()))));
		TreeTile.setGrowthModifierPine(Double.valueOf(properties.getProperty("GrowthModifierPine", Double.toString(TreeTile.getGrowthModifierPine()))));
		TreeTile.setGrowthModifierOak(Double.valueOf(properties.getProperty("GrowthModifierOak", Double.toString(TreeTile.getGrowthModifierOak()))));
		TreeTile.setGrowthModifierCedar(Double.valueOf(properties.getProperty("GrowthModifierCedar", Double.toString(TreeTile.getGrowthModifierCedar()))));
		TreeTile.setGrowthModifierWillow(Double.valueOf(properties.getProperty("GrowthModifierWillow", Double.toString(TreeTile.getGrowthModifierWillow()))));
		TreeTile.setGrowthModifierMaple(Double.valueOf(properties.getProperty("GrowthModifierMaple", Double.toString(TreeTile.getGrowthModifierMaple()))));
		TreeTile.setGrowthModifierApple(Double.valueOf(properties.getProperty("GrowthModifierApple", Double.toString(TreeTile.getGrowthModifierApple()))));
		TreeTile.setGrowthModifierLemon(Double.valueOf(properties.getProperty("GrowthModifierLemon", Double.toString(TreeTile.getGrowthModifierLemon()))));
		TreeTile.setGrowthModifierOlive(Double.valueOf(properties.getProperty("GrowthModifierOlive", Double.toString(TreeTile.getGrowthModifierOlive()))));
		TreeTile.setGrowthModifierCherry(Double.valueOf(properties.getProperty("GrowthModifierCherry", Double.toString(TreeTile.getGrowthModifierCherry()))));
		TreeTile.setGrowthModifierChestnut(Double.valueOf(properties.getProperty("GrowthModifierChestnut", Double.toString(TreeTile.getGrowthModifierChestnut()))));
		TreeTile.setGrowthModifierWalnut(Double.valueOf(properties.getProperty("GrowthModifierWalnut", Double.toString(TreeTile.getGrowthModifierWalnut()))));
		TreeTile.setGrowthModifierFir(Double.valueOf(properties.getProperty("GrowthModifierFir", Double.toString(TreeTile.getGrowthModifierFir()))));
		TreeTile.setGrowthModifierLinden(Double.valueOf(properties.getProperty("GrowthModifierLinden", Double.toString(TreeTile.getGrowthModifierLinden()))));

		TreeTile.setGrowthModifierNormal(Double.valueOf(properties.getProperty("GrowthModifierNormal", Double.toString(TreeTile.getGrowthModifierNormal()))));
		TreeTile.setGrowthModifierEnchanted(Double.valueOf(properties.getProperty("GrowthModifierEnchanted", Double.toString(TreeTile.getGrowthModifierEnchanted()))));
		TreeTile.setGrowthModifierMycelium(Double.valueOf(properties.getProperty("GrowthModifierMycelium", Double.toString(TreeTile.getGrowthModifierMycelium()))));

		TreeTile.setGrowthModifierAge( 0, Double.valueOf(properties.getProperty("GrowthModifierAge0",  Double.toString(TreeTile.getGrowthModifierAge( 0)))));
		TreeTile.setGrowthModifierAge( 1, Double.valueOf(properties.getProperty("GrowthModifierAge1",  Double.toString(TreeTile.getGrowthModifierAge( 1)))));
		TreeTile.setGrowthModifierAge( 2, Double.valueOf(properties.getProperty("GrowthModifierAge2",  Double.toString(TreeTile.getGrowthModifierAge( 2)))));
		TreeTile.setGrowthModifierAge( 3, Double.valueOf(properties.getProperty("GrowthModifierAge3",  Double.toString(TreeTile.getGrowthModifierAge( 3)))));
		TreeTile.setGrowthModifierAge( 4, Double.valueOf(properties.getProperty("GrowthModifierAge4",  Double.toString(TreeTile.getGrowthModifierAge( 4)))));
		TreeTile.setGrowthModifierAge( 5, Double.valueOf(properties.getProperty("GrowthModifierAge5",  Double.toString(TreeTile.getGrowthModifierAge( 5)))));
		TreeTile.setGrowthModifierAge( 6, Double.valueOf(properties.getProperty("GrowthModifierAge6",  Double.toString(TreeTile.getGrowthModifierAge( 6)))));
		TreeTile.setGrowthModifierAge( 7, Double.valueOf(properties.getProperty("GrowthModifierAge7",  Double.toString(TreeTile.getGrowthModifierAge( 7)))));
		TreeTile.setGrowthModifierAge( 8, Double.valueOf(properties.getProperty("GrowthModifierAge8",  Double.toString(TreeTile.getGrowthModifierAge( 8)))));
		TreeTile.setGrowthModifierAge( 9, Double.valueOf(properties.getProperty("GrowthModifierAge9",  Double.toString(TreeTile.getGrowthModifierAge( 9)))));
		TreeTile.setGrowthModifierAge(10, Double.valueOf(properties.getProperty("GrowthModifierAge10", Double.toString(TreeTile.getGrowthModifierAge(10)))));
		TreeTile.setGrowthModifierAge(11, Double.valueOf(properties.getProperty("GrowthModifierAge11", Double.toString(TreeTile.getGrowthModifierAge(11)))));
		TreeTile.setGrowthModifierAge(12, Double.valueOf(properties.getProperty("GrowthModifierAge12", Double.toString(TreeTile.getGrowthModifierAge(12)))));
		TreeTile.setGrowthModifierAge(13, Double.valueOf(properties.getProperty("GrowthModifierAge13", Double.toString(TreeTile.getGrowthModifierAge(13)))));
		TreeTile.setGrowthModifierAge(14, Double.valueOf(properties.getProperty("GrowthModifierAge14", Double.toString(TreeTile.getGrowthModifierAge(14)))));
	}
//======================================================================
}

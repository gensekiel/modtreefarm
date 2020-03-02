package gensekiel.wurmunlimited.mods.treefarm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.CtPrimitiveType;
import javassist.bytecode.Descriptor;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;

import com.wurmonline.server.structures.Fence;

public class Hooks
{
	private static Logger logger = Logger.getLogger(Hooks.class.getName());
	public static boolean preventVanillaFarmGrowth = true;
//======================================================================
	private static String tree_wrapper_code = ""
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
	public static void injectTreeGrowthWrapper()
	{
		try{
			ClassPool pool = ClassPool.getDefault();
			pool.importPackage("java.util.logging");
			pool.importPackage("com.wurmonline.mesh");
			pool.importPackage("com.wurmonline.server");
			CtClass ctclass = pool.get("com.wurmonline.server.zones.TilePoller");
			CtMethod wrapper_method = CtNewMethod.make(tree_wrapper_code, ctclass);
			ctclass.addMethod(wrapper_method);
			logger.log(Level.INFO, "Tree growth wrapper method injected.");
		}catch(Exception e){
			logger.log(Level.WARNING, "Tree growth wrapper injection failed. Falling back to builtin growth function. Exception: " + e);
			TreeGrowTask.setUseOriginalGrowthFunction(false);
		}
	}
//======================================================================
	private static String grass_wrapper_code = ""
	+ "public static void wrap_checkForGrassGrowth("
	+ "   int tile, int tilex, int tiley, byte type, byte aData, boolean andflowers)"
	+ "{"
	+ "   logger.log(Level.INFO, \"Injected method entered.\");"
	+ "   boolean pollingSurface_old = pollingSurface;"
	+ "   MeshIO currentMesh_old = currentMesh;"
	+ "   pollingSurface = true;"
	+ "   currentMesh = Server.surfaceMesh;"
	+ ""
	+ "   checkForGrassGrowth(tile, tilex, tiley, type, aData, andflowers);"
	+ ""
	+ "   pollingSurface = pollingSurface_old;"
	+ "   currentMesh = currentMesh_old;"
	+ "   logger.log(Level.INFO, \"Injected method left.\");"
	+ "}";
//======================================================================
	public static void injectGrassGrowthWrapper()
	{
		try{
			ClassPool pool = ClassPool.getDefault();
			pool.importPackage("java.util.logging");
			pool.importPackage("com.wurmonline.mesh");
			pool.importPackage("com.wurmonline.server");
			CtClass ctclass = pool.get("com.wurmonline.server.zones.TilePoller");
			CtMethod wrapper_method = CtNewMethod.make(grass_wrapper_code, ctclass);
			ctclass.addMethod(wrapper_method);
			logger.log(Level.INFO, "Grass growth wrapper method injected.");
		}catch(Exception e){
			logger.log(Level.WARNING, "Grass growth wrapper injection failed. Falling back to builtin growth function. Exception: " + e);
			GrassGrowTask.setUseOriginalGrowthFunction(false);
		}
	}
//======================================================================
	private static String treegrass_wrapper_code = ""
	+ "public static void wrap_checkForTreeGrassGrowth("
	+ "   int tile, int tilex, int tiley, byte type, byte aData)"
	+ "{"
	+ "   logger.log(Level.INFO, \"Injected method entered.\");"
	+ "   boolean pollingSurface_old = pollingSurface;"
	+ "   MeshIO currentMesh_old = currentMesh;"
	+ "   pollingSurface = true;"
	+ "   currentMesh = Server.surfaceMesh;"
	+ ""
	+ "   checkForGrassGrowth(tile, tilex, tiley, type, aData, andflowers);"
	+ ""
	+ "   pollingSurface = pollingSurface_old;"
	+ "   currentMesh = currentMesh_old;"
	+ "   logger.log(Level.INFO, \"Injected method left.\");"
	+ "}";
//======================================================================
	public static void injectTreeGrassGrowthWrapper()
	{
		try{
			ClassPool pool = ClassPool.getDefault();
			pool.importPackage("java.util.logging");
			pool.importPackage("com.wurmonline.mesh");
			pool.importPackage("com.wurmonline.server");
			CtClass ctclass = pool.get("com.wurmonline.server.zones.TilePoller");
			CtMethod wrapper_method = CtNewMethod.make(treegrass_wrapper_code, ctclass);
			ctclass.addMethod(wrapper_method);
			logger.log(Level.INFO, "Tree grass growth wrapper method injected.");
		}catch(Exception e){
			logger.log(Level.WARNING, "Tree grass growth wrapper injection failed. Falling back to builtin growth function. Exception: " + e);
			TreeGrassTask.setUseOriginalGrowthFunction(false);
		}
	}
//======================================================================
	private static String seed_wrapper_code = ""
	+ "public static void wrap_checkForSeedGrowth("
	+ "   int tile, int tilex, int tiley)"
	+ "{"
	+ "   logger.log(Level.INFO, \"Injected method entered.\");"
	+ "   boolean pollingSurface_old = pollingSurface;"
	+ "   MeshIO currentMesh_old = currentMesh;"
	+ "   pollingSurface = true;"
	+ "   currentMesh = Server.surfaceMesh;"
	+ ""
	+ "   checkForSeedGrowth(tile, tilex, tiley);"
	+ ""
	+ "   pollingSurface = pollingSurface_old;"
	+ "   currentMesh = currentMesh_old;"
	+ "   logger.log(Level.INFO, \"Injected method left.\");"
	+ "}";
//======================================================================
	public static void injectSeedGrowthWrapper()
	{
		try{
			ClassPool pool = ClassPool.getDefault();
			pool.importPackage("java.util.logging");
			pool.importPackage("com.wurmonline.mesh");
			pool.importPackage("com.wurmonline.server");
			CtClass ctclass = pool.get("com.wurmonline.server.zones.TilePoller");
			CtMethod wrapper_method = CtNewMethod.make(seed_wrapper_code, ctclass);
			ctclass.addMethod(wrapper_method);
			logger.log(Level.INFO, "Seed growth wrapper method injected.");
		}catch(Exception e){
			logger.log(Level.WARNING, "Seed growth wrapper injection failed. Falling back to builtin growth function. Exception: " + e);
			ForageBotanizeTask.setUseOriginalGrowthFunction(false);
		}
	}
//======================================================================
//	private static String farm_wrapper_code = ""
//	+ "public static void wrap_checkForFarmGrowth("
//	+ "   int tile, int tilex, int tiley, byte type, byte aData, boolean onSurface)"
//	+ "{"
//	+ "   logger.log(Level.INFO, \"Injected method entered.\");"
//	+ "   boolean pollingSurface_old = pollingSurface;"
//	+ "   MeshIO currentMesh_old = currentMesh;"
//	+ "   pollingSurface = onSurface;"
//	+ "   if(onSurface) currentMesh = Server.surfaceMesh;"
//	+ "   else currentMesh = Server.caveMesh;"
//	+ ""
//	+ "   checkForFarmGrowth(tile, tilex, tiley, type, aData);"
//	+ ""
//	+ "   pollingSurface = pollingSurface_old;"
//	+ "   currentMesh = currentMesh_old;"
//	+ "   logger.log(Level.INFO, \"Injected method left.\");"
//	+ "}";
	private static String farm_wrapper_code = ""
	+ "public static void wrap_checkForFarmGrowth("
	+ "   int tile, int tilex, int tiley, byte type, byte aData)"
	+ "{"
	+ "   logger.log(Level.INFO, \"Injected method entered.\");"
	+ "   boolean pollingSurface_old = pollingSurface;"
	+ "   MeshIO currentMesh_old = currentMesh;"
	+ "   pollingSurface = true;"
	+ "   currentMesh = Server.surfaceMesh;"
	+ ""
	+ "   checkForFarmGrowth(tile, tilex, tiley, type, aData);"
	+ ""
	+ "   pollingSurface = pollingSurface_old;"
	+ "   currentMesh = currentMesh_old;"
	+ "   logger.log(Level.INFO, \"Injected method left.\");"
	+ "}";
//======================================================================
	public static void injectFarmGrowthWrapper()
	{
		try{
			ClassPool pool = ClassPool.getDefault();
			pool.importPackage("java.util.logging");
			pool.importPackage("com.wurmonline.mesh");
			pool.importPackage("com.wurmonline.server");
			CtClass ctclass = pool.get("com.wurmonline.server.zones.TilePoller");
			CtMethod wrapper_method = CtNewMethod.make(farm_wrapper_code, ctclass);
			ctclass.addMethod(wrapper_method);
			logger.log(Level.INFO, "Farm growth wrapper method injected.");
		}catch(Exception e){
			logger.log(Level.WARNING, "Farm growth wrapper injection failed. Falling back to builtin growth function. Exception: " + e);
			FarmGrowTask.setUseOriginalGrowthFunction(false);
		}
	}
//======================================================================
	public static void registerListLoadingHook()
	{
		// We need the server paths to be set properly before initializing
		// the TaskPoller.
		// Should be okay to do that in preinit, too.
		try{
			HookManager.getInstance().registerHook("com.wurmonline.server.zones.CropTilePoller", "initializeFields", Descriptor.ofMethod(CtPrimitiveType.voidType, null), new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler(){
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
	}
//======================================================================
	public static void registerPollingHook()
	{
		try{
			HookManager.getInstance().registerHook("com.wurmonline.server.zones.CropTilePoller", "pollCropTiles", Descriptor.ofMethod(CtPrimitiveType.voidType, null), new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler(){
						@Override
						public Object invoke(Object object, Method method, Object[] args) throws Throwable {
							TaskPoller.poll();
							if(!preventVanillaFarmGrowth){
								return method.invoke(object, args);
							}else{
								return null;
							}
						}
					};
				}
			});
		}catch(Exception e){
			logger.log(Level.SEVERE, "Polling hook failed. TaskPoller will not run. Exception: " + e);
		}
	}
//======================================================================
	public static void registerListSavingHook()
	{
		try{
			// Don't hook shutDown directly as it raises an exception on
			// purpose and that may make it look like this mod is somehow
			// responsible.
//			HookManager.getInstance().registerHook("com.wurmonline.server.Server", "shutDown", Descriptor.ofMethod(CtPrimitiveType.voidType, null), new InvocationHandlerFactory(){
			HookManager.getInstance().registerHook("com.wurmonline.server.zones.Zones", "saveAllZones", Descriptor.ofMethod(CtPrimitiveType.voidType, null), new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler(){
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
	public static void registerTreeProtectionHook()
	{
		try{
			String desc = Descriptor.ofMethod(CtPrimitiveType.voidType, new CtClass[]{
				CtPrimitiveType.intType, CtPrimitiveType.intType, CtPrimitiveType.intType,
				CtPrimitiveType.byteType, CtPrimitiveType.byteType});

			HookManager.getInstance().registerHook("com.wurmonline.server.zones.TilePoller", "checkForTreeGrowth", desc, new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler(){
						@Override
						public Object invoke(Object object, Method method, Object[] args) throws Throwable {
							AbstractTask task = TaskPoller.containsTaskFor(TileTask.getTaskKey((int)args[1], (int)args[2]));
							if(task != null){
								logger.log(Level.WARNING, "Server poll prevented: checkForTreeGrowth");
								return null;
							}
							return method.invoke(object, args);
						}
					};
				}
			});
		}catch(Exception e){
			logger.log(Level.WARNING, "Tree protection hook failed. Exception: " + e);
		}
	}
//======================================================================
	public static void registerGrassProtectionHook()
	{
		try{
			String desc = Descriptor.ofMethod(CtPrimitiveType.booleanType, new CtClass[]{
				CtPrimitiveType.intType, CtPrimitiveType.intType, CtPrimitiveType.intType,
				CtPrimitiveType.byteType, CtPrimitiveType.byteType, CtPrimitiveType.booleanType});

			HookManager.getInstance().registerHook("com.wurmonline.server.zones.TilePoller", "checkForGrassGrowth", desc, new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler(){
						@Override
						public Object invoke(Object object, Method method, Object[] args) throws Throwable {
							AbstractTask task = TaskPoller.containsTaskFor(TileTask.getTaskKey((int)args[1], (int)args[2]));
							if(task != null){
								logger.log(Level.WARNING, "Server poll prevented: checkForGrassGrowth");
								return false;
							}
							return method.invoke(object, args);
						}
					};
				}
			});
		}catch(Exception e){
			logger.log(Level.WARNING, "Grass protection hook failed. Exception: " + e);
		}
	}
//======================================================================
	public static void registerTreeGrassProtectionHook()
	{
		try{
			String desc = Descriptor.ofMethod(CtPrimitiveType.booleanType, new CtClass[]{
				CtPrimitiveType.intType, CtPrimitiveType.intType, CtPrimitiveType.intType,
				CtPrimitiveType.byteType, CtPrimitiveType.byteType});

			HookManager.getInstance().registerHook("com.wurmonline.server.zones.TilePoller", "checkForTreeGrassGrowth", desc, new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler(){
						@Override
						public Object invoke(Object object, Method method, Object[] args) throws Throwable {
							AbstractTask task = TaskPoller.containsTaskFor(TileTask.getTaskKey((int)args[1], (int)args[2]));
							if(task != null){
								logger.log(Level.WARNING, "Server poll prevented: checkForTreeGrassGrowth");
								return false;
							}
							return method.invoke(object, args);
						}
					};
				}
			});
		}catch(Exception e){
			logger.log(Level.WARNING, "Tree grass protection hook failed. Exception: " + e);
		}
	}
//======================================================================
	public static void registerSeedProtectionHook()
	{
		try{
			String desc = Descriptor.ofMethod(CtPrimitiveType.booleanType, new CtClass[]{
				CtPrimitiveType.intType, CtPrimitiveType.intType, CtPrimitiveType.intType});

			HookManager.getInstance().registerHook("com.wurmonline.server.zones.TilePoller", "checkForSeedGrowth", desc, new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler(){
						@Override
						public Object invoke(Object object, Method method, Object[] args) throws Throwable {
							AbstractTask task = TaskPoller.containsTaskFor(TileTask.getTaskKey((int)args[1], (int)args[2]));
							if(task != null){
								logger.log(Level.WARNING, "Server poll prevented: checkForSeedGrowth");
								return false;
							}
							return method.invoke(object, args);
						}
					};
				}
			});
		}catch(Exception e){
			logger.log(Level.WARNING, "Seed protection hook failed. Exception: " + e);
		}
	}
//======================================================================
	public static void registerHedgeProtectionHook()
	{
		try{
			String desc = Descriptor.ofMethod(CtPrimitiveType.voidType, new CtClass[]{CtPrimitiveType.longType});

			HookManager.getInstance().registerHook("com.wurmonline.server.structures.Fence", "poll", desc, new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler(){
						@Override
						public Object invoke(Object object, Method method, Object[] args) throws Throwable {
							AbstractTask task = TaskPoller.containsTaskFor(((Fence)object).getId());
							if(task != null){
								logger.log(Level.WARNING, "Server poll prevented: Fence.poll");
								return null;
							}
							return method.invoke(object, args);
						}
					};
				}
			});
		}catch(Exception e){
			logger.log(Level.WARNING, "Hedge protection hook failed. Exception: " + e);
		}
	}
//======================================================================
	public static void registerFarmProtectionHook()
	{
		try{
			String desc = Descriptor.ofMethod(CtPrimitiveType.booleanType, new CtClass[]{
				CtPrimitiveType.intType, CtPrimitiveType.intType, CtPrimitiveType.intType,
				CtPrimitiveType.byteType, CtPrimitiveType.byteType});

			HookManager.getInstance().registerHook("com.wurmonline.server.zones.TilePoller", "checkForFarmGrowth", desc, new InvocationHandlerFactory(){
				@Override
				public InvocationHandler createInvocationHandler(){
					return new InvocationHandler(){
						@Override
						public Object invoke(Object object, Method method, Object[] args) throws Throwable {
							AbstractTask task = TaskPoller.containsTaskFor(TileTask.getTaskKey((int)args[1], (int)args[2]));
							if(task != null){
								logger.log(Level.WARNING, "Server poll prevented: checkForFarmGrowth");
								return false;
							}
							return method.invoke(object, args);
						}
					};
				}
			});
		}catch(Exception e){
			logger.log(Level.WARNING, "Farm protection hook failed. Exception: " + e);
		}
	}
//======================================================================
}

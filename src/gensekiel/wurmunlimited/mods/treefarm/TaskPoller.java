package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.ServerDirInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TaskPoller
{
//======================================================================
	private static final Map<Long, AbstractTask> tiles = new HashMap<Long, AbstractTask>();
	private static Logger logger = Logger.getLogger(TaskPoller.class.getName());
	private static String fileName = "modTreeFarm.mtf";
	private static volatile long lastPolled = System.currentTimeMillis();
	public static long getLastPolled(){ return lastPolled; }
//======================================================================
	private static long pollInterval = 300000;
	private static boolean preserveList = true;
	private static boolean protectTasks = false;
	private static boolean ignoreNextMatch = false;
//======================================================================
	public static void setPollInterval(long time){ pollInterval = time; }
	public static void setPreserveList(boolean b){ preserveList = b; }
	public static void setProtectTasks(boolean b){ protectTasks = b; }
//======================================================================
	public static long getPollInterval(){ return pollInterval; }
	public static boolean getPreserveList(){ return preserveList; }
	public static boolean getProtectTasks(){ return protectTasks; }
//======================================================================
	public static void ignoreNextMatch(){ ignoreNextMatch = true; }
//======================================================================
	public static void addTask(AbstractTask task)
	{
		synchronized(tiles){
			tiles.put(task.getTaskKey(), task);
		}
	}
//======================================================================
	public static void initialize()
	{
		loadTreeList();
	}
//======================================================================
	public static AbstractTask containsTaskFor(long key)
	{
		if(ignoreNextMatch){
			ignoreNextMatch = false;
			return null;
		}
		AbstractTask tt = null;
		synchronized(tiles){
			tt = tiles.get(key);
		}
		return tt;
	}
//======================================================================
	// Heavily inspired by the CropTilePoller.
	public static void poll()
	{
		long now = System.currentTimeMillis();
		if(now - lastPolled < pollInterval) return;

		List<AbstractTask> toRemove = new ArrayList<AbstractTask>();
		
		synchronized(tiles){
			lastPolled = System.currentTimeMillis();
			
			for(AbstractTask task : tiles.values()){
				// Check task relevant stuff
				if(task.performCheck()){
					toRemove.add(task);
					continue;
				}
				
				// Growth time not yet reached
				if(now - task.getTimeStamp() < task.getTaskTime()) continue;

				// Perform task
				if(task.performTask())
					toRemove.add(task);
			}
			
			for(AbstractTask t : toRemove){
				if(CoolDownTask.getCoolDownMultiplier() > 0.0 && !(t instanceof CoolDownTask))
					tiles.put(t.getTaskKey(), new CoolDownTask(t.getTaskKey(), t.getTaskTime()));
				else
					tiles.remove(t.getTaskKey());
			}
		}
	}
//======================================================================
	public static void dumpTreeList()
	{
		if(!preserveList) return;
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try{
			fos = new FileOutputStream(ServerDirInfo.getFileDBPath() + fileName);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(System.currentTimeMillis());
			oos.writeObject(tiles.size());
			
			synchronized(tiles){
				for(AbstractTask task : tiles.values()) oos.writeObject(task);
			}
			
			logger.log(Level.INFO, "Saved " + tiles.size() + " tasks.");
		}catch(Exception e){
			logger.log(Level.WARNING, "Failed saving task list: " + e);
		}finally{
			try{
				if(oos != null) oos.close();
				if(fos != null) fos.close();
			}catch(IOException ioe){}
		}
	}
//======================================================================
	public static void loadTreeList()
	{
		if(!preserveList) return;
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		try{
			fis = new FileInputStream(ServerDirInfo.getFileDBPath() + fileName);
			ois = new ObjectInputStream(fis);

			long time = (long)ois.readObject();
			long diff = System.currentTimeMillis() - time;
			if(diff < 0) throw new Exception("Invalid timestamp.");

			int length = (int)ois.readObject();
			
			synchronized(tiles){
				for(int i = 0; i < length; i++){
					AbstractTask task = (AbstractTask)ois.readObject();
					task.setTimeStamp(task.getTimeStamp() + diff);
					tiles.put(task.getTaskKey(), task);
				}
			}
			
			logger.log(Level.INFO, "" + tiles.size() + " tasks loaded.");
		}catch(Exception e){
			logger.log(Level.WARNING, "Failed loading task list: " + e);
			logger.log(Level.INFO, "That's okay if it is the first time using that option.");
		}finally{
			try{
				if(ois != null) ois.close();
				if(fis != null) fis.close();
			}catch(IOException ioe){}
		}
	}
//======================================================================
}

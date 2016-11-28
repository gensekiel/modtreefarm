package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
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

public class TreeTilePoller
{
//======================================================================
	private static final Map<Long, TreeTile> tiles = new HashMap<Long, TreeTile>();
	private static volatile long lastPolledTiles = System.currentTimeMillis();
	private static Logger logger = Logger.getLogger(TreeTilePoller.class.getName());
	private static String fileName = "modTreeFarm.mtf";
//======================================================================
	private static long pollInterval = 300000;
	private static boolean preserveTreeList = true;
//======================================================================
	public static void setPollInterval(long time){ pollInterval = time; }
	public static void setPreserveTreeList(boolean b){ preserveTreeList = b; }
//======================================================================
	public static long getPollInterval(){ return pollInterval; }
	public static boolean getPreserveTreeList(){ return preserveTreeList; }
//======================================================================
	private static long getTileKey(int x, int y)
	{
		return Long.valueOf(Tiles.getTileId(x, y, 0, true));
	}
//======================================================================
	public static void addTreeTile(int rawtile, int x, int y, AbstractTask task, double multiplier)
	{
		synchronized(tiles){
			TreeTile ttile = new TreeTile(rawtile, x, y, task, multiplier);
			tiles.put(getTileKey(x, y), ttile);
		}
	}
//======================================================================
	public static void initialize()
	{
		loadTreeList();
	}
//======================================================================
	public static AbstractTask containsTileAt(int x, int y)
	{
		TreeTile tt = null;
		synchronized(tiles){
			tt = tiles.get(getTileKey(x, y));
		}
		if(tt != null) return tt.getTask();
		return null;
	}
//======================================================================
	// Heavily inspired by the CropTilePoller.
	public static void pollTreeTiles()
	{
		long now = System.currentTimeMillis();
		if(now - lastPolledTiles < pollInterval) return;

		List<TreeTile> toRemove = new ArrayList<TreeTile>();
		
		synchronized(tiles){
			lastPolledTiles = System.currentTimeMillis();
			
			for(TreeTile treetile : tiles.values()){
				int rawtile = Server.surfaceMesh.getTile(treetile.getX(), treetile.getY());
				Tiles.Tile tile = TreeTile.getTile(rawtile);
				
				// Generic check
				if(tile == null){
					toRemove.add(treetile);
					continue;
				}
				
				// Check task relevant stuff
				if(treetile.getTask().performCheck(treetile, rawtile)){
					toRemove.add(treetile);
					continue;
				}
				
				// Growth time not yet reached
				if(now - treetile.getTimeStamp() < treetile.getGrowthTime()) continue;

				// Perform task
				if(treetile.getTask().performTask(treetile))
					toRemove.add(treetile);
			}
			
			for(TreeTile t : toRemove){
				tiles.remove(getTileKey(t.getX(), t.getY()));
			}
		}
	}
//======================================================================
	public static void dumpTreeList()
	{
		if(!preserveTreeList) return;
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try{
			fos = new FileOutputStream(ServerDirInfo.getFileDBPath() + fileName);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(System.currentTimeMillis());
			oos.writeObject(tiles.size());
			
			synchronized(tiles){
				for(TreeTile treetile : tiles.values()) oos.writeObject(treetile);
			}
			
			logger.log(Level.INFO, "Saved " + tiles.size() + " tree tiles.");
		}catch(Exception e){
			logger.log(Level.WARNING, "Failed saving tree list: " + e);
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
		if(!preserveTreeList) return;
		
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
					TreeTile treetile = (TreeTile)ois.readObject();
					treetile.setTimeStamp(treetile.getTimeStamp() + diff);
					tiles.put(getTileKey(treetile.getX(), treetile.getY()), treetile);
				}
			}
			
			logger.log(Level.INFO, "" + tiles.size() + " tree tiles loaded.");
		}catch(Exception e){
			logger.log(Level.WARNING, "Failed loading tree list: " + e);
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

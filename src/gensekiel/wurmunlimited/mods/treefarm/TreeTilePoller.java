package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.ServerDirInfo;
import com.wurmonline.server.zones.TilePoller;
import com.wurmonline.mesh.TreeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Method;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TreeTilePoller
{
	private static final Map<Long, TreeTile> tiles = new HashMap<Long, TreeTile>();
	private static volatile long lastPolledTiles = System.currentTimeMillis();
	private static Logger logger = Logger.getLogger(TreeTilePoller.class.getName());
	private static String fileName = "modTreeFarm.mtf";
	private static byte ageLimit = 15;
	private static long pollInterval = 300000;
	private static boolean keepGrowing = false;
	private static boolean useOriginalGrowthFunction = false;
	private static boolean preserveTreeList = false;
	private static boolean checkForWUPoll = false;
//======================================================================
	public static void setPollInterval(long time){ pollInterval = time; }
	public static void setAgeLimit(byte b){ ageLimit = b; }
	public static void setKeepGrowing(boolean b){ keepGrowing = b; }
	public static void setUseOriginalGrowthFunction(boolean b){ useOriginalGrowthFunction = b; }
	public static void setPreserveTreeList(boolean b){ preserveTreeList = b; }
	public static void setCheckForWUPoll(boolean b){ checkForWUPoll = b; }
//======================================================================
	public static long getPollInterval(){ return pollInterval; }
	public static byte getAgeLimit(){ return ageLimit; }
	public static boolean getKeepGrowing(){ return keepGrowing; }
	public static boolean getUseOriginalGrowthFunction(){ return useOriginalGrowthFunction; }
	public static boolean getPreserveTreeList(){ return preserveTreeList; }
	public static boolean getCheckForWUPoll(){ return checkForWUPoll; }
//======================================================================
	private static long getTreeTileKey(int x, int y)
	{
		return Long.valueOf(Tiles.getTileId(x, y, 0, true));
	}
//======================================================================
	public static void addTreeTile(int tileData, int x, int y)
	{
		synchronized(tiles){
			TreeTile tTile = new TreeTile(tileData, x, y);
			tiles.put(getTreeTileKey(x, y), tTile);
		}
	}
//======================================================================
	public static void initialize()
	{
		loadTreeList();
	}
//======================================================================
	public static boolean containsTileAt(int x, int y)
	{
		synchronized(tiles){
			return tiles.containsKey(getTreeTileKey(x, y));
		}
	}
//======================================================================
	private static boolean checkTile(int tile_stored, int tile_found)
	{
		if(checkForWUPoll){
			// In the upper 16 bytes the tile integer encodes tile type
			// and data, data being age (4 bit), fruit (1 bit),
			// center (1 bit) and grass height (2 bit).
			// Only compare type and age.
			if(keepGrowing) // ignore age
				return ( (tile_stored & 0xFF000000) != (tile_found & 0xFF000000) );
			else
				return ( (tile_stored & 0xFFF00000) != (tile_found & 0xFFF00000) );
		}
		return false;
	}
//======================================================================
	// Heavily inspired by the CropTilePoller.
	public static void pollTreeTiles()
	{
		long now = System.currentTimeMillis();
		if(now - lastPolledTiles < pollInterval) return;

		List<TreeTile> toRemove = new ArrayList<TreeTile>();
		synchronized(tiles){
//			logger.log(Level.INFO, "TREE_POLLER: polling.");
			lastPolledTiles = System.currentTimeMillis();
			for(TreeTile treetile : tiles.values()){
				int tile = Server.surfaceMesh.getTile(treetile.getX(), treetile.getY());
				byte type = Tiles.decodeType(tile);
				byte data = Tiles.decodeData(tile);
				byte age = getAgeFromData(data);
				Tiles.Tile tileEnum = Tiles.getTile(type);
				
				if(    (tileEnum == null)
				    || !tileEnum.isTree()
				    || checkTile(treetile.getTile(), tile)
				    || age >= ageLimit) // Maximum age reached.
				{
					toRemove.add(treetile);
					continue;
				}
				
				// Growth time not reached yet.
				if(now - treetile.getTimeStamp() < TreeTile.getBaseGrowthTime()) continue;
					
				if(useOriginalGrowthFunction)
					callTreeGrowthWrapper(tile, treetile.getX(), treetile.getY(), type, data);
				else
					forceTreeGrowth(tile, treetile.getX(), treetile.getY(), type, data);
				if(!keepGrowing) toRemove.add(treetile);
			}
			for(TreeTile t : toRemove){
				tiles.remove(getTreeTileKey(t.getX(), t.getY()));
			}
		}
	}
//======================================================================
	public static byte getAgeFromData(byte data)
	{
		return (byte)(data >> 4 & 0xF);
	}
//======================================================================
	private static void callTreeGrowthWrapper(int tile, int tilex, int tiley, byte type, byte data)
	{
		try{
			Method method = TilePoller.class.getMethod("wrap_checkForTreeGrowth", int.class, int.class, int.class, byte.class, byte.class);
			method.invoke(null, tile, tilex, tiley, type, data);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}
//======================================================================
	private static void forceTreeGrowth(int tile, int tilex, int tiley, byte type, byte data)
	{
		Server.setWorldResource(tilex, tiley, 0);
		byte age = getAgeFromData(data);
		byte new_data = (byte)(((age+1) << 4) + (data & 0xF) & 0xFF);
		// It seems that originally the tree type was stored in the lower
		// nibble of the data byte. The new storage scheme stores the
		// grass height and whether the tree is a fruit tree and/or is
		// centered there. It seems that this function here is intended
		// for translating old tiles to the new format.
		// byte new_type = convertToNewType(Tiles.getTile(type), new_data);
		
		// Manual conversion as the above method is private and I don't
		// want to use an injected method just for that.
		Tiles.Tile ttile = Tiles.getTile(type);
		TreeData.TreeType tt = ttile.getTreeType(data);
		byte new_type = type;
		if(ttile.isNormalTree()) new_type = tt.asNormalTree();
		if(ttile.isMyceliumTree()) new_type = tt.asMyceliumTree();
		if(ttile.isEnchantedTree()) new_type = tt.asEnchantedTree();
		
		Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), new_type, new_data));
		Server.modifyFlagsByTileType(tilex, tiley, new_type);
		Players.getInstance().sendChangedTile(tilex, tiley, true, false);
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
					tiles.put(getTreeTileKey(treetile.getX(), treetile.getY()), treetile);
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

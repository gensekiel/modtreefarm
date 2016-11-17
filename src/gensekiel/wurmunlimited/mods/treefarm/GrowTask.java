package gensekiel.wurmunlimited.mods.treefarm;

import java.lang.reflect.Method;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.TilePoller;

public class GrowTask extends AbstractTask
{
	private static final long serialVersionUID = 2L;
//======================================================================
	private static byte ageLimit = 15;
	private static boolean keepGrowing = false;
	private static boolean useOriginalGrowthFunction = false;
//======================================================================
	public static void setAgeLimit(byte b){ ageLimit = b; }
	public static void setKeepGrowing(boolean b){ keepGrowing = b; }
	public static void setUseOriginalGrowthFunction(boolean b){ useOriginalGrowthFunction = b; }
//======================================================================
	public static byte getAgeLimit(){ return ageLimit; }
	public static boolean getKeepGrowing(){ return keepGrowing; }
	public static boolean getUseOriginalGrowthFunction(){ return useOriginalGrowthFunction; }
//======================================================================
	@Override
	public String getDescription()
	{
		return "The tree has already been watered.";
	}
//======================================================================
	public static boolean checkTileType(int tile)
	{
		return Tiles.getTile(Tiles.decodeType(tile)).isTree();
	}
//======================================================================
	@Override
	public boolean performCheck(TreeTile treetile, int rawtile)
	{
		// Implies checkTileType(), if that method was used by the action
		// that created the task.
		if(checkForWUPoll){
			if(keepGrowing) // check type, ignore age
				if( (treetile.getTile() & 0xFF000000) != (rawtile & 0xFF000000) ) 
					return true;
			else // check type and age
				if( (treetile.getTile() & 0xFFF00000) != (rawtile & 0xFFF00000) )
					return true;
		}
		
		byte age = TreeTile.getAge(Tiles.decodeData(rawtile));
		if(age >= ageLimit) return true;
		
		return false;
	}
//======================================================================
	@Override
	public boolean performTask(TreeTile treetile)
	{
		if(useOriginalGrowthFunction)
			callTreeGrowthWrapper(treetile.getTile(), treetile.getX(), treetile.getY(), treetile.getType(), treetile.getData());
		else
			forceTreeGrowth(treetile.getTile(), treetile.getX(), treetile.getY(), treetile.getType(), treetile.getData());
		
		if(!keepGrowing) return true;
		return false;
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
		byte age = TreeTile.getAge(data);
		byte new_data = (byte)(((age+1) << 4) + (data & 0xF) & 0xFF);
		// It seems that originally the tree type was stored in the lower
		// nibble of the data byte. The new storage scheme stores the
		// grass height and whether the tree is a fruit tree and/or is
		// centered there. It seems that this function here is intended
		// for translating old tiles to the new format.
		// byte new_type = convertToNewType(Tiles.getTile(type), new_data);
		
		// Manual conversion as the above method is private and I don't
		// want to use an injected method just for that.
		byte new_type = TreeTile.convertTile(type, data);
		
		Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), new_type, new_data));
		Server.modifyFlagsByTileType(tilex, tiley, new_type);
		Players.getInstance().sendChangedTile(tilex, tiley, true, false);
	}
//======================================================================
}

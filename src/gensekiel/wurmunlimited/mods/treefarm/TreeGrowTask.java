package gensekiel.wurmunlimited.mods.treefarm;

import java.lang.reflect.Method;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.TilePoller;

public class TreeGrowTask extends TreeTileTask
{
	private static final long serialVersionUID = 5L;
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//----------------------------------------------------------------------
	protected static boolean useOriginalGrowthFunction = false;
	public static void setUseOriginalGrowthFunction(boolean b){ useOriginalGrowthFunction = b; }
	public static boolean getUseOriginalGrowthFunction(){ return useOriginalGrowthFunction; }
//----------------------------------------------------------------------
	protected static byte ageLimit = 1;
	public static void setAgeLimit(byte b){ ageLimit = b; }
	public static byte getAgeLimit(){ return ageLimit; }
//======================================================================
	public TreeGrowTask(int rawtile, int tilex, int tiley, double multiplier, double chance, double rnd, boolean onSurface)
	{
		super(rawtile, tilex, tiley, onSurface);
		tasktime *= growthMultiplier * multiplier;
		fail_chance *= chance;
		random_factor *= rnd;
	}
//======================================================================
	@Override
	public String getDescription()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);
		return "This " + getTileName(rawtile) + " has been watered recently.";
	}
//======================================================================
	public static boolean checkTileType(int rawtile)
	{
		return (    getTile(rawtile).isTree()
		         || getTile(rawtile).isBush() );
	}
//======================================================================
	@Override
	public boolean performCheck()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);
		Tiles.Tile ttile = getTile(rawtile);

		// Generic check
		if(ttile == null) return true;

		if(!checkTileType(rawtile)) return true;

		if(!TileTask.compareTileTypes(tile, rawtile)) return true;

		byte age = getAge(getData(rawtile));

		if(checkForWUPoll && age >= ageLimit){ // Check age
			if((tile & 0x00F00000) != (rawtile & 0x00F00000))
				return true;
		}

		if(getAge(getData(tile)) < ageLimit && age == ageLimit) return true;

		if(age >= getMaxAge()) return true;

		return false;
	}
//======================================================================
	@Override
	public boolean performTask()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);

		if(useOriginalGrowthFunction)
			callTreeGrowthWrapper(rawtile, x, y, getType(rawtile), getData(rawtile));
		else
			forceTreeGrowth(rawtile, x, y, getType(rawtile), getData(rawtile));

		if(getAge(getData(rawtile)) + 1 < ageLimit){
			return false;
		}
		return true;
	}
//======================================================================
	private static void callTreeGrowthWrapper(int rawtile, int tilex, int tiley, byte type, byte data)
	{
		// If the protection is active, calling the growth function
		// will also check for tasks and prevent the execution on
		// tracked objects --> allow execution once.
		if(TaskPoller.getProtectTasks()) TaskPoller.ignoreNextMatch();
		try{
			Method method = TilePoller.class.getMethod("wrap_checkForTreeGrowth", int.class, int.class, int.class, byte.class, byte.class);
			method.invoke(null, rawtile, tilex, tiley, type, data);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}
//======================================================================
	private static void forceTreeGrowth(int rawtile, int tilex, int tiley, byte type, byte data)
	{
		Server.setWorldResource(tilex, tiley, 0);
		byte age = getAge(data);
		byte new_data = (byte)((((age+1) & 0xF) << 4) | (data & 0xF));
		// It seems that originally the tree type was stored in the lower
		// nibble of the data byte. The new storage scheme stores the
		// grass height and whether the tree is a fruit tree and/or is
		// centered there. It seems that this function here is intended
		// for translating old tiles to the new format.
		// byte new_type = convertToNewType(Tiles.getTile(type), new_data);

		// Manual conversion as the above method is private and I don't
		// want to use an injected method just for that.
		byte new_type = convertTile(type, data);

		Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(rawtile), new_type, new_data));
		Server.modifyFlagsByTileType(tilex, tiley, new_type);
		Players.getInstance().sendChangedTile(tilex, tiley, true, false);
	}
//======================================================================
}

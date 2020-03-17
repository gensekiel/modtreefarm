package gensekiel.wurmunlimited.mods.treefarm;

import java.lang.reflect.Method;

import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.TilePoller;

public class GrassGrowTask extends GrassTileTask
{
	private static final long serialVersionUID = 5L;
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//----------------------------------------------------------------------
	private static double[] GrowthMultiplierAge = {1.0, 1.1, 1.2};
	public static void setGrowthMultiplierAge(int age, double d){ GrowthMultiplierAge[age] = d; }
	public static double getGrowthMultiplierAge(int age){ return GrowthMultiplierAge[age]; }
//----------------------------------------------------------------------
	protected static boolean useOriginalGrowthFunction = false;
	public static void setUseOriginalGrowthFunction(boolean b){ useOriginalGrowthFunction = b; }
	public static boolean getUseOriginalGrowthFunction(){ return useOriginalGrowthFunction; }
//----------------------------------------------------------------------
	protected static byte ageLimit = 1;
	public static void setAgeLimit(byte b){ ageLimit = b; }
	public static byte getAgeLimit(){ return ageLimit; }
//======================================================================
	public GrassGrowTask(int rawtile, int tilex, int tiley, double multiplier, double chance, double rnd, boolean onSurface)
	{
		super(rawtile, tilex, tiley, onSurface);

		byte tage = getGrowthStage();
		if(tage < 3) tasktime *= GrowthMultiplierAge[tage];

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
		return getTile(rawtile).isGrass();
	}
//======================================================================
	public byte getGrowthStage()
	{
		return getGrowthStage(getData());
	}
//======================================================================
	public static byte getGrowthStage(byte data)
	{
		return GrassData.GrowthStage.decodeTileData(data).getCode();
	}
//======================================================================
	public boolean canGrow()
	{
		GrassData.GrowthStage gs = GrassData.GrowthStage.decodeTileData(Tiles.decodeData(tile));
		if(gs.isMax()) return false;
		return true;
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

		byte age = getGrowthStage(getData(rawtile));

		if(checkForWUPoll && age >= ageLimit){ // Check age
			if((tile & 0x00C00000) != (rawtile & 0x00C00000))
				return true;
		}

		if(getGrowthStage(getData(tile)) < ageLimit && age == ageLimit) return true;

		if(!canGrow()) return true;

		return false;
	}
//======================================================================
	@Override
	public boolean performTask()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);

		if(useOriginalGrowthFunction)
			callGrassGrowthWrapper(rawtile, x, y, getType(rawtile), getData(rawtile));
		else
			forceGrassGrowth(rawtile, x, y, getType(rawtile), getData(rawtile));

		if(getGrowthStage(getData(rawtile)) + 1 < ageLimit){
			return false;
		}
		return true;
	}
//======================================================================
	private static void callGrassGrowthWrapper(int rawtile, int tilex, int tiley, byte type, byte data)
	{
		// If the protection is active, calling the growth function
		// will also check for tasks and prevent the execution on
		// tracked objects --> allow execution once.
		if(TaskPoller.getProtectTasks()) TaskPoller.ignoreNextMatch();
		try{
			Method method = TilePoller.class.getMethod("wrap_checkForGrassGrowth", int.class, int.class, int.class, byte.class, byte.class, boolean.class);
			method.invoke(null, rawtile, tilex, tiley, type, data, false);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}
//======================================================================
	private static void forceGrassGrowth(int rawtile, int tilex, int tiley, byte type, byte data)
	{
		GrassData.GrowthStage gs = GrassData.GrowthStage.decodeTileData(data);
		GrassData.FlowerType ft = GrassData.FlowerType.decodeTileData(data);
		gs = gs.getNextStage();
		byte newdata = GrassData.encodeGrassTileData(gs, ft);
		Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(rawtile), type, newdata));
		Server.modifyFlagsByTileType(tilex, tiley, type);
		Players.getInstance().sendChangedTile(tilex, tiley, true, false);
	}
//======================================================================
}

package gensekiel.wurmunlimited.mods.treefarm;

import java.lang.reflect.Method;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.TilePoller;

public class TreeGrassTask extends GrassTileTask
{
	private static final long serialVersionUID = 3L;
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
//======================================================================
	public TreeGrassTask(int rawtile, int tilex, int tiley, double multiplier)
	{
		super(rawtile, tilex, tiley, multiplier);

		byte tage = getGrowthStage();
		if(tage < 3) tasktime *= GrowthMultiplierAge[tage];

		tasktime *= growthMultiplier;
	}
//======================================================================
	public byte getGrowthStage()
	{
		return getGrowthStage(getData());
	}
//======================================================================
	public static byte getGrowthStage(byte data)
	{
		return GrassData.GrowthTreeStage.decodeTileData(data).getCode();
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

		if(checkForWUPoll){
			if(!keepGrowing && (tile & 0x00030000) != (rawtile & 0x00030000))
				return true;
		}
		
//		byte age = getGrowthStage(Tiles.decodeData(rawtile));
//		if(age >= 3) return true;
		
		GrassData.GrowthTreeStage grassheight = GrassData.GrowthTreeStage.decodeTileData(Tiles.decodeData(rawtile));
		if(grassheight.isMax()) return true;
		
		return false;
	}
//======================================================================
	@Override
	public boolean performTask()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);

		if(useOriginalGrowthFunction)
			callTreeGrassGrowthWrapper(rawtile, x, y, getType(), getData());
		else
			forceTreeGrassGrowth(rawtile, x, y, getType(), getData());
		
		if(keepGrowing) return false;
		return true;
	}
//======================================================================
	@Override
	public String getDescription()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);
		return "This " + getTileName(rawtile) + "'s ground has been watered recently.";
	}
//======================================================================
	private static void callTreeGrassGrowthWrapper(int rawtile, int tilex, int tiley, byte type, byte data)
	{
		// If the protection is active, calling the growth function
		// will also check for tasks and prevent the execution on
		// tracked objects --> allow execution once.
		if(TaskPoller.getProtectTasks()) TaskPoller.ignoreNextMatch();
		try{
			Method method = TilePoller.class.getMethod("wrap_checkForTreeGrassGrowth", int.class, int.class, int.class, byte.class, byte.class);
			method.invoke(null, rawtile, tilex, tiley, type, data);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}
//======================================================================
	private static void forceTreeGrassGrowth(int rawtile, int tilex, int tiley, byte type, byte data)
	{
		GrassData.GrowthTreeStage grassheight = GrassData.GrowthTreeStage.decodeTileData(data);
		grassheight = grassheight.getNextStage();
		byte new_data = Tiles.encodeTreeData(FoliageAge.getFoliageAge(data), TreeData.hasFruit(data), TreeData.isCentre(data), grassheight);
		Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(rawtile), type, new_data));
		Server.modifyFlagsByTileType(tilex, tiley, type);
		Players.getInstance().sendChangedTile(tilex, tiley, true, false);
	}
}

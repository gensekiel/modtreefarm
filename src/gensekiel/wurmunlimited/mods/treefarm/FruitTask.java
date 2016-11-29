package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;

public class FruitTask extends TreeTileTask
{
	private static final long serialVersionUID = 3L;
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//======================================================================
	public FruitTask(int rawtile, int tilex, int tiley, double multiplier)
	{
		super(rawtile, tilex, tiley, multiplier);
		tasktime *= growthMultiplier;
	}
//======================================================================
	@Override
	public String getDescription()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);
		return "This " + getTileName(rawtile) + " has already been fertilized.";
	}
//======================================================================
	public static boolean checkTileType(int rawtile)
	{
		Tiles.Tile tt = getTile(rawtile);
		return (     tt.canBearFruit() // Currently implies tree
		         || (tt.isBush() && !tt.isThorn(Tiles.decodeData(rawtile))));
	}
//======================================================================
	@Override
	public boolean performCheck()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);

		if(!checkTileType(rawtile)) return true;
		
		if(getTile(rawtile).isMycelium()) return true;
		
		if(checkForWUPoll){ // Check type and fruit state, ignore rest
			if( (tile & 0xFF080000) != (rawtile & 0xFF080000) )
				return true;
		}

		byte age = getAge(Tiles.decodeData(rawtile));
		if(   age <= FoliageAge.YOUNG_FOUR.getAgeId()
		   || age >= FoliageAge.OVERAGED.getAgeId()) return true;

		return false;
	}
//======================================================================
	@Override
	public boolean performTask()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);
		return plantSeed(rawtile, x, y, getType(), getData());
	}
//======================================================================
	private static boolean plantSeed(int rawtile, int tilex, int tiley, byte type, byte data)
	{
		Server.setWorldResource(tilex, tiley, 0);

		// The server checks the calendar and batch-sets all trees/bushes
		// to be harvestable. The calendar seems to have its own thread,
		// and the status seems to be checked every 125 milliseconds.
		byte new_data = (byte)(data | 0x8);
		byte new_type = convertTile(type, data);
		
		if(type != new_type) Server.modifyFlagsByTileType(tilex, tiley, new_type);
		Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(rawtile), new_type, new_data));
		Players.getInstance().sendChangedTile(tilex, tiley, true, false);
		
		return true;
	}
//======================================================================
}

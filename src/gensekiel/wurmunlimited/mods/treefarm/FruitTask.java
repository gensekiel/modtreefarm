package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;

public class FruitTask extends AbstractTask
{
	private static final long serialVersionUID = 2L;
//======================================================================
	private static double growthMultiplier = 1.0;
//======================================================================
	public static void setGrowthModifier(double d){ growthMultiplier = d; }
	public static double getGrowthModifier(){ return growthMultiplier; }
//======================================================================
	public double getGrowthMultiplier(){ return growthMultiplier; }
//======================================================================
	@Override
	public String getDescription(int rawtile)
	{
		return "This " + TreeTile.getTileName(rawtile) + " has already been fertilized.";
	}
//======================================================================
	public static boolean checkTileType(int rawtile)
	{
		Tiles.Tile tt = TreeTile.getTile(rawtile);
		return (     tt.canBearFruit() // Currently implies tree
		         || (tt.isBush() && !tt.isThorn(Tiles.decodeData(rawtile))));
	}
//======================================================================
	@Override
	public boolean performCheck(TreeTile treetile, int rawtile)
	{
		if(!checkTileType(rawtile)) return true;
		
		if(TreeTile.getTile(rawtile).isMycelium()) return true;
		
		if(checkForWUPoll){ // Check type and fruit state, ignore rest
			if( (treetile.getTile() & 0xFF080000) != (rawtile & 0xFF080000) )
				return true;
		}

		byte age = TreeTile.getAge(Tiles.decodeData(rawtile));
		if(   age <= FoliageAge.YOUNG_FOUR.getAgeId()
		   || age >= FoliageAge.OVERAGED.getAgeId()) return true;

		return false;
	}
//======================================================================
	@Override
	public boolean performTask(TreeTile treetile)
	{
		return plantSeed(treetile.getTile(), treetile.getX(), treetile.getY(), treetile.getType(), treetile.getData());
	}
//======================================================================
	private static boolean plantSeed(int rawtile, int tilex, int tiley, byte type, byte data)
	{
		Server.setWorldResource(tilex, tiley, 0);

		// The server checks the calendar and batch-sets all trees/bushes
		// to be harvestable. The calendar seems to have its own thread,
		// and the status seems to be checked every 125 milliseconds.
		byte new_data = (byte)(data | 0x8);
		byte new_type = TreeTile.convertTile(type, data);
		
		if(type != new_type) Server.modifyFlagsByTileType(tilex, tiley, new_type);
		Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(rawtile), new_type, new_data));
		Players.getInstance().sendChangedTile(tilex, tiley, true, false);
		
		return true;
	}
//======================================================================
}

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
	public String getDescription()
	{
		return "The tree has already been fertilized.";
	}
//======================================================================
	public static boolean checkTileType(int tile)
	{
		Tiles.Tile tt = Tiles.getTile(Tiles.decodeType(tile));
		return (     tt.canBearFruit() // Currently implies tree
		         || (tt.isBush() && !tt.isThorn(Tiles.decodeData(tile))));
	}
//======================================================================
	@Override
	public boolean performCheck(TreeTile treetile, int rawtile)
	{
		// TODO mycelium infected do not grow fruit
		// Implies checkTileType(), if that method was used by the action
		// that created the task.
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
	private static boolean plantSeed(int tile, int tilex, int tiley, byte type, byte data)
	{
		Server.setWorldResource(tilex, tiley, 0);

		byte new_data = (byte)(data | 0x8);
		byte new_type = TreeTile.convertTile(type, data);
		
		if(type != new_type) Server.modifyFlagsByTileType(tilex, tiley, new_type);
		Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), new_type, new_data));
		Players.getInstance().sendChangedTile(tilex, tiley, true, false);
		
		return true;
	}
//======================================================================
}

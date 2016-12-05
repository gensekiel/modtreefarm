package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;

public class GrassGrowAction extends TileAction
{
//======================================================================
	public GrassGrowAction()
	{
		this("Water");
	}
//======================================================================
	public GrassGrowAction(String s)
	{
		super(s, "water", "watering", "Watering");
		
		cost = 5000;
		time = 30;
		item = ItemList.water;
		skill = 10045;
	}
//======================================================================
	@Override
	protected boolean checkTileConditions(Creature performer, int rawtile)
	{
		byte data = Tiles.decodeData(rawtile);
		byte age = GrassGrowTask.getGrowthStage(data);
		String tilename = TileTask.getTileName(rawtile);
		
		if(age >= 3){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " has reached its maximum height.", (byte)1);
			return true;
		}
		return false;
	}
//======================================================================
	@Override
	public void performTileAction(int rawtile, int tilex, int tiley, double multiplier)
	{
		TaskPoller.addTask(new GrassGrowTask(rawtile, tilex, tiley, multiplier));
	}
//======================================================================
	@Override
	protected boolean checkTileType(int rawtile)
	{
		byte data = Tiles.decodeData(rawtile);
		return (GrassGrowTask.checkTileType(rawtile) && GrassData.GrassType.decodeTileData(data).getType() == 0 );
	}
//======================================================================
}

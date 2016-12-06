package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.mesh.Tiles;

public class WateringAction extends TileAction
{
//======================================================================
	public WateringAction()
	{
		this("Water");
	}
//======================================================================
	public WateringAction(String s)
	{
		super(s, "water", "watering", "Watering");
		
		cost = 5000;
		time = 30;
		item = ItemList.water;
		skill = 10048;
	}
//======================================================================
	@Override
	protected boolean checkTileConditions(Creature performer, int rawtile, int tilex, int tiley)
	{
		byte data = Tiles.decodeData(rawtile);
		int age = TreeTileTask.getAge(data);
		String tilename = TileTask.getTileName(rawtile);
		
		if(age >= TreeGrowTask.getAgeLimit()){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " is too old to make it grow by watering it.", (byte)1);
			return true;
		}
		return false;
	}
//======================================================================
	@Override
	public void performTileAction(int rawtile, int tilex, int tiley, double multiplier)
	{
		TaskPoller.addTask(new TreeGrowTask(rawtile, tilex, tiley, multiplier));
	}
//======================================================================
	@Override
	protected boolean checkTileType(int rawtile)
	{
		if(!allowTrees && TileTask.getTile(rawtile).isTree()) return false;
		if(!allowBushes && TileTask.getTile(rawtile).isBush()) return false;
		return TreeGrowTask.checkTileType(rawtile);
	}
//======================================================================
}

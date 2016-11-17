package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.mesh.Tiles;

public class WateringAction extends AbstractAction
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
	}
//======================================================================
	@Override
	protected boolean checkConditions(Creature performer, int tile)
	{
		byte data = Tiles.decodeData(tile);
		int age = TreeTile.getAge(data);
		Tiles.Tile tt = Tiles.getTile(tile);
		
		if(age >= GrowTask.getAgeLimit()){
			performer.getCommunicator().sendNormalServerMessage("The " + tt.getName() + " is too old to make it grow by watering it.");
			return true;
		}
		return false;
	}
//======================================================================
	@Override
	public void performTileAction(int tile, int tilex, int tiley)
	{
		TreeTilePoller.addTreeTile(tile, tilex, tiley, new GrowTask());
	}
//======================================================================
	@Override
	protected boolean checkTileType(int tile)
	{
		return GrowTask.checkTileType(tile);
	}
//======================================================================
}

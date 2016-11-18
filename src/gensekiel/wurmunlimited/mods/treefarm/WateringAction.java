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
	protected boolean checkConditions(Creature performer, int rawtile)
	{
		byte data = Tiles.decodeData(rawtile);
		int age = TreeTile.getAge(data);
		String tilename = TreeTile.getTileName(rawtile);
		
		if(age >= GrowTask.getAgeLimit()){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " is too old to make it grow by watering it.", (byte)1);
			return true;
		}
		return false;
	}
//======================================================================
	@Override
	public void performTileAction(int rawtile, int tilex, int tiley)
	{
		TreeTilePoller.addTreeTile(rawtile, tilex, tiley, new GrowTask());
	}
//======================================================================
	@Override
	protected boolean checkTileType(int rawtile)
	{
		return GrowTask.checkTileType(rawtile);
	}
//======================================================================
}

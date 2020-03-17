package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;

public class WateringAction extends TileAction
{
//======================================================================
	public WateringAction(){ this("Water"); }
//======================================================================
	public WateringAction(String s)
	{
		super(s, AbstractAction.ActionFlavor.WATER_ACTION);

		cost = 5000;
		time = 30;
		item = ItemList.water;
		skill = 10048;
	}
//======================================================================
	@Override
	protected byte getAge(byte tiledata)
	{
		return TreeTileTask.getAge(tiledata);
	}
//======================================================================
	@Override
	protected byte getMaxAge()
	{
		return TreeTileTask.getMaxAge();
	}
//======================================================================
	@Override
	protected boolean checkTileConditions(Creature performer, int rawtile, int tilex, int tiley)
	{
		byte data = Tiles.decodeData(rawtile);
		int age = TreeTileTask.getAge(data);
		String tilename = TileTask.getTileName(rawtile);

		if(age >= TreeTileTask.getMaxAge()){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " is too old to make it grow by watering it.", (byte)1);
			return true;
		}
		return false;
	}
//======================================================================
	@Override
	public void performTileAction(int rawtile, int tilex, int tiley, double multiplier, double chance, double rnd, boolean onSurface)
	{
		TaskPoller.addTask(new TreeGrowTask(rawtile, tilex, tiley, multiplier, chance, rnd, onSurface));
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

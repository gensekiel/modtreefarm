package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;

public class FarmGrowAction extends TileAction
{
	public FarmGrowAction(){ this("Water"); }
//======================================================================
	public FarmGrowAction(String s)
	{
		this(s, AbstractAction.ActionFlavor.WATER_ACTION);
		item = ItemList.water;
	}
//======================================================================
	protected FarmGrowAction(String s, AbstractAction.ActionFlavor f)
	{
		super(s, f);

		cost = 5000;
		time = 50;
		skill = 10049;
	}
//======================================================================
	@Override
	protected byte getAge(byte tiledata)
	{
		return FarmGrowTask.getAge(tiledata);
	}
//======================================================================
	@Override
	protected byte getMaxAge()
	{
		return FarmGrowTask.getMaxAge();
	}
//======================================================================
	@Override
	protected boolean checkTileConditions(Creature performer, int rawtile, int tilex, int tiley)
	{
		byte data = Tiles.decodeData(rawtile);
		String tilename = TileTask.getTileName(rawtile);

		if(getAge(data) >= getMaxAge()){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " has reached its maximum height.", (byte)1);
			return true;
		}
		return false;
	}
//======================================================================
	@Override
	public void performTileAction(int rawtile, int tilex, int tiley, double multiplier)
	{
		TaskPoller.addTask(new FarmGrowTask(rawtile, tilex, tiley, multiplier));
	}
//======================================================================
	@Override
	protected boolean checkTileType(int rawtile)
	{
		return FarmGrowTask.checkTileType(rawtile) && !FarmGrowTask.growsInWater(rawtile);
	}
//======================================================================
}

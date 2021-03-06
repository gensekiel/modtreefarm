package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;

public class FarmFertAction extends TileAction
{
	public FarmFertAction(){ this("Fertilize"); }
//======================================================================
	public FarmFertAction(String s)
	{
		this(s, AbstractAction.ActionFlavor.FERTILIZE_ACTION);
		item = ItemList.ash;
	}
//======================================================================
	protected FarmFertAction(String s, AbstractAction.ActionFlavor f)
	{
		super(s, f);

		cost = 100;
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
	public void performTileAction(int rawtile, int tilex, int tiley, double multiplier, double chance, double rnd, boolean onSurface)
	{
		TaskPoller.addTask(new FarmGrowTask(rawtile, tilex, tiley, multiplier, chance, rnd, onSurface));
	}
//======================================================================
	@Override
	protected boolean checkTileType(int rawtile)
	{
		return FarmGrowTask.checkTileType(rawtile) && FarmGrowTask.growsInWater(rawtile);
	}
//======================================================================
}

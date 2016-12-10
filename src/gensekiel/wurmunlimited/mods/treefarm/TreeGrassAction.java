package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.mesh.Tiles;

public class TreeGrassAction extends TileAction
{
//======================================================================
	public TreeGrassAction()
	{
		this("Water ground");
	}
//======================================================================
	public TreeGrassAction(String s)
	{
		this(s, "water", "watering", "Watering");

		item = ItemList.water;
	}
//======================================================================
	protected TreeGrassAction(String s1, String s2, String s3, String s4)
	{
		super(s1, s2, s3, s4);

		cost = 5000;
		time = 30;
		skill = 10045;
	}
//======================================================================
	@Override
	protected byte getAge(byte tiledata)
	{
		return TreeGrassTask.getGrowthStage(tiledata);
	}
//======================================================================
	@Override
	protected byte getMaxAge()
	{
		return 3;
	}
//======================================================================
	@Override
	protected boolean checkTileConditions(Creature performer, int rawtile, int tilex, int tiley)
	{
		byte data = Tiles.decodeData(rawtile);
		byte age = TreeGrassTask.getGrowthStage(data);
		String tilename = TileTask.getTileName(rawtile);
		
		if(age >= 3){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + "'s grass has reached its maximum height.", (byte)1);
			return true;
		}
		return false;
	}
//======================================================================
	@Override
	public void performTileAction(int rawtile, int tilex, int tiley, double multiplier)
	{
		TaskPoller.addTask(new TreeGrassTask(rawtile, tilex, tiley, multiplier));
	}
//======================================================================
	@Override
	protected boolean checkTileType(int rawtile)
	{
		return TreeGrassTask.checkTileType(rawtile);
	}
//======================================================================
}

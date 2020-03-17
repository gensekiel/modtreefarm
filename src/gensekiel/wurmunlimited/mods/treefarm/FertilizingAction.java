package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;

public class FertilizingAction extends TileAction
{
//======================================================================
	public FertilizingAction(){ this("Fertilize"); }
//======================================================================
	public FertilizingAction(String s)
	{
		super(s, AbstractAction.ActionFlavor.FERTILIZE_ACTION);

		cost = 100;
		time = 50;
		item = ItemList.ash;
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
		return 15;
	}
//======================================================================
	@Override
	protected boolean checkTileConditions(Creature performer, int rawtile, int tilex, int tiley)
	{
		byte data = Tiles.decodeData(rawtile);
		int age = TreeTileTask.getAge(data);

		Tiles.Tile tt = TileTask.getTile(rawtile);
		String tilename = TileTask.getTileName(rawtile);

		if(age <= FoliageAge.YOUNG_FOUR.getAgeId()){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " is too young to bear fruit.", (byte)1);
			return true;
		}else if (age >= FoliageAge.OVERAGED.getAgeId()){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " is too old to bear fruit.", (byte)1);
			return true;
		}

		if(TreeData.hasFruit(data)){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " already bears fruit.", (byte)1);
			return true;
		}

		if(tt.isMycelium()){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " cannot bear fruit.", (byte)1);
			return true;
		}

		return false;
	}
//======================================================================
	@Override
	public void performTileAction(int rawtile, int tilex, int tiley, double multiplier, double chance, double rnd, boolean onSurface)
	{
		TaskPoller.addTask(new FruitTask(rawtile, tilex, tiley, multiplier, chance, rnd, onSurface));
	}
//======================================================================
	@Override
	protected boolean checkTileType(int rawtile)
	{
		if(!allowTrees && TileTask.getTile(rawtile).isTree()) return false;
		if(!allowBushes && TileTask.getTile(rawtile).isBush()) return false;
		return FruitTask.checkTileType(rawtile);
	}
//======================================================================
}

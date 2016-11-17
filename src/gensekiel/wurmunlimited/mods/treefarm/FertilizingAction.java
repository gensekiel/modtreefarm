package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;

public class FertilizingAction extends AbstractAction 
{
//======================================================================
	public FertilizingAction()
	{
		this("Fertilize");
	}
//======================================================================
	public FertilizingAction(String s)
	{
		super(s, "fertilize", "fertilizing", "Fertilizing");

		cost = 1000;
		time = 50;
		item = ItemList.ash;
	}
//======================================================================
	@Override
	protected boolean checkConditions(Creature performer, int tile)
	{
		byte data = Tiles.decodeData(tile);
		int age = TreeTile.getAge(data);

		if(age <= FoliageAge.YOUNG_FOUR.getAgeId()){
			performer.getCommunicator().sendNormalServerMessage("The tree is too young to bear fruit.");
			return true;
		}else if (age >= FoliageAge.OVERAGED.getAgeId()){
			performer.getCommunicator().sendNormalServerMessage("The tree is too old to bear fruit.");
			return true;
		}

		if(TreeData.hasFruit(data)){
			performer.getCommunicator().sendNormalServerMessage("This tree already bears fruit.");
			return true;
		}

		return false;
	}
//======================================================================
	@Override
	public void performTileAction(int tile, int tilex, int tiley)
	{
		TreeTilePoller.addTreeTile(tile, tilex, tiley, new FruitTask());
	}
//======================================================================
	@Override
	protected boolean checkTileType(int tile)
	{
		return FruitTask.checkTileType(tile);
	}
//======================================================================
}

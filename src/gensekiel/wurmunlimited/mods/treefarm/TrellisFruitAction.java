package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;

public class TrellisFruitAction extends ItemAction
{
//======================================================================
	public TrellisFruitAction(){ this("Fertilize"); }
//======================================================================
	public TrellisFruitAction(String s)
	{
		super(s, AbstractAction.ActionFlavor.FERTILIZE_ACTION);

		cost = 100;
		time = 50;
		item = ItemList.ash;
		skill = 10045;
	}
//======================================================================
	protected TrellisFruitAction(String s, AbstractAction.ActionFlavor f){ super(s, f); }
//======================================================================
	@Override protected int getAge(Item target){ return target.getLeftAuxData(); }
	@Override protected int getMaxAge(){ return FoliageAge.OVERAGED.getAgeId(); }
//======================================================================
	@Override
	protected boolean checkItemConditions(Creature performer, Item item)
	{
		int age = getAge(item);
		if(age <= FoliageAge.YOUNG_FOUR.getAgeId()){
			performer.getCommunicator().sendNormalServerMessage("This " + item.getName() + " is too young to bear fruit.", (byte)1);
			return true;
		}else if (age >= FoliageAge.OVERAGED.getAgeId()){
			performer.getCommunicator().sendNormalServerMessage("This " + item.getName() + " is too old to bear fruit.", (byte)1);
			return true;
		}

		if(item.isHarvestable()){
			performer.getCommunicator().sendNormalServerMessage("This " + item.getName() + " can already be harvested.", (byte)1);
			return true;
		}

		return false;
	}
//======================================================================
	@Override protected boolean checkItemType(Item item){ return TrellisFruitTask.checkItemType(item); }
//======================================================================
	@Override
	protected void performItemAction(Item item, double multiplier, double chance, double rnd)
	{
		TaskPoller.addTask(new TrellisFruitTask(item, multiplier, chance, rnd));
	}
//======================================================================
}

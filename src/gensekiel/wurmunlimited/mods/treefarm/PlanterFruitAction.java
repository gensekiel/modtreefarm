package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;

public class PlanterFruitAction extends ItemAction
{
//======================================================================
	private static double costMultiplier = 0.5;
	public static void setCostMultiplier(double d){ costMultiplier = d; }
	public static double getCostMultiplier(){ return costMultiplier; }
//======================================================================
	public PlanterFruitAction(){ this("Fertilize"); }
//======================================================================
	public PlanterFruitAction(String s)
	{
		super(s, AbstractAction.ActionFlavor.FERTILIZE_ACTION);

		cost = 100;
		time = 50;
		item = ItemList.ash;
		skill = 10045;
	}
//======================================================================
	protected PlanterFruitAction(String s, AbstractAction.ActionFlavor f){ super(s, f); }
//======================================================================
	@Override
	protected boolean checkItemConditions(Creature performer, Item item)
	{
		if(!PlanterFruitTask.isFertilizable(item)){
			performer.getCommunicator().sendNormalServerMessage("This " + item.getName() + " is not at the right age.", (byte)1);
			return true;
		}

		if(PlanterFruitTask.isPickable(item)){
			performer.getCommunicator().sendNormalServerMessage("This " + item.getName() + " can already be picked.", (byte)1);
			return true;
		}

		return false;
	}
//======================================================================
	@Override protected boolean checkItemType(Item item){ return PlanterFruitTask.checkItemType(item); }
	@Override protected int getMaxAge(){ return 127; }
	@Override protected int getAge(Item target){ return PlanterFruitTask.getPlanterAge(target); }
//======================================================================
	@Override protected void performItemAction(Item item, double multiplier, double chance, double rnd)
	{
		TaskPoller.addTask(new PlanterFruitTask(item, multiplier, chance, rnd));
	}
//======================================================================
	@Override public int getActionCost(double knowledge, int age, int maxage)
	{
		return (int)(costMultiplier * super.getActionCost(knowledge, age, maxage));
	}
//======================================================================
}

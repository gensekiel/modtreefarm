package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;

public class PlanterAgeAction extends PlanterAction
{
//======================================================================
	public PlanterAgeAction(){ this("Water"); }
//======================================================================
	protected PlanterAgeAction(String s)
	{
		super(s, AbstractAction.ActionFlavor.WATER_ACTION);

		cost = 5000;
		time = 30;
		item = ItemList.water;
		skill = 10045;
	}
//======================================================================
	protected boolean checkItemConditions(Creature performer, Item item)
	{
		if(!PlanterAgeTask.canGrow(item)){
			performer.getCommunicator().sendNormalServerMessage("This " + item.getName() + " is too old.", (byte)1);
			return true;
		}

		return false;
	}
//======================================================================
	@Override protected void performItemAction(Item item, double multiplier)
	{
		TaskPoller.addTask(new PlanterAgeTask(item, multiplier));
	}
//======================================================================
}

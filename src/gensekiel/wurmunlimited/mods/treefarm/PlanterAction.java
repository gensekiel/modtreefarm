package gensekiel.wurmunlimited.mods.treefarm;

import java.util.logging.Level;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.skills.Skill;

public class PlanterAction extends ItemAction
{
//======================================================================
	private static double costMultiplier = 0.5;
	public static void setCostMultiplier(double d){ costMultiplier = d; }
	public static double getCostMultiplier(){ return costMultiplier; }
//======================================================================
	public PlanterAction(){ this("Fertilize"); }
//======================================================================
	public PlanterAction(String s)
	{
		super(s, AbstractAction.ActionFlavor.FERTILIZE_ACTION);

		cost = 100;
		time = 50;
		item = ItemList.ash;
		skill = 10045;
	}
//======================================================================
	protected PlanterAction(String s, AbstractAction.ActionFlavor f){ super(s, f); }
//======================================================================
	@Override
	protected boolean checkItemConditions(Creature performer, Item item)
	{
		if(!PlanterTask.isFertilizable(item)){
			performer.getCommunicator().sendNormalServerMessage("This " + item.getName() + " is not at the right age.", (byte)1);
			return true;
		}

		if(PlanterTask.isPickable(item)){
			performer.getCommunicator().sendNormalServerMessage("This " + item.getName() + " can already be picked.", (byte)1);
			return true;
		}

		return false;
	}
//======================================================================
	@Override protected boolean checkItemType(Item item){ return PlanterTask.checkItemType(item); }
	@Override protected int getMaxAge(){ return 127; }
	@Override protected int getAge(Item target){ return PlanterTask.getPlanterAge(target); }
//======================================================================
	@Override protected void performItemAction(Item item, double multiplier)
	{
		TaskPoller.addTask(new PlanterTask(item, multiplier));
	}
//======================================================================
	@Override public int getActionCost(double knowledge, int age, int maxage)
	{
		return (int)(costMultiplier * super.getActionCost(knowledge, age, maxage));
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter)
	{
		try{
			Skill skl = performer.getSkills().getSkillOrLearn(skill);
			int timeLeft = getActionTime(skl.knowledge);
			int actioncost = getActionCost(skl.knowledge, getAge(target), getMaxAge());
			String itemname = target.getName();

			if(counter == 1.0f){
				if(!checkItemType(target)) return true;
				if(checkConditions) if(checkItemConditions(performer, target)) return true;
				if(checkIfPolled) if(checkStatus(performer, target.getWurmId())) return true;

				if(item != 0) if(checkItem(performer, source, itemname, actioncost)) return true;

				startAction(performer, itemname, timeLeft);
				if(gainSkill) gainSkill(skl);
			}else{
				timeLeft = performer.getCurrentAction().getTimeLeft();
			}
			if(counter * 10.0F > timeLeft){
				double quality = 100.0;
				if(item != 0) quality = source.getCurrentQualityLevel();

				double multiplier = getTaskTimeMultiplier(quality, skl.knowledge);
				performItemAction(target, multiplier);

				if(item != 0) source.setWeight(source.getWeightGrams() - actioncost, true);

				finishAction(performer, itemname);

				return true;
			}
			return false;
		}catch(Exception e){
			logger.log(Level.WARNING, e.getMessage(), e);
			return true;
		}
	}
//======================================================================
}

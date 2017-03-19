package gensekiel.wurmunlimited.mods.treefarm;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.zones.Zones;

public class ItemAction extends AbstractAction
{
//======================================================================
	public ItemAction(){ this("Fertilize"); }
//======================================================================
	public ItemAction(String s)
	{
		super(s, AbstractAction.ActionFlavor.FERTILIZE_ACTION);

		cost = 100;
		time = 50;
		item = ItemList.ash;
		skill = 10045;
	}
//======================================================================
	protected ItemAction(String s, AbstractAction.ActionFlavor f){ super(s, f); }
//======================================================================
	protected boolean checkItemConditions(Creature performer, Item item)
	{
		if(item.isHarvestable()){
			performer.getCommunicator().sendNormalServerMessage("This " + item.getName() + " can already be harvested.", (byte)1);
			return true;
		}

		return false;
	}
//======================================================================
	protected boolean checkItemType(Item item){ return ItemTask.checkItemType(item); }
	protected int getAge(Item target){ return 1; }
	protected int getMaxAge(){ return 1; }
//======================================================================
	protected void performItemAction(Item item, double multiplier)
	{
		TaskPoller.addTask(new ItemTask(item, multiplier));
	}
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Item target)
	{
		if(obeyProtection && Zones.protectedTiles[target.getTileX()][target.getTileY()]) return null;

		if(checkItemType(target)){
			return Arrays.asList(actionEntry);
		}
		return null;
	}
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item target)
	{
		return getBehavioursFor(performer, null, target);
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
	@Override
	public boolean action(Action action, Creature performer, Item target, short num, float counter)
	{
		return action(action, performer, null, target, num, counter);
	}
//======================================================================
}

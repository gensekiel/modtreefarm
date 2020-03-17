package gensekiel.wurmunlimited.mods.treefarm;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.zones.Zones;

public abstract class ItemAction extends AbstractAction
{
//======================================================================
	protected ItemAction(String s, AbstractAction.ActionFlavor f){ super(s, f); }
//======================================================================
	protected abstract int getAge(Item target);
	protected abstract int getMaxAge();
//======================================================================
	protected abstract boolean checkItemType(Item item);
	protected abstract boolean checkItemConditions(Creature performer, Item item);
	protected abstract void performItemAction(Item item, double multiplier, double chance, double rnd);
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
			int timeLeft = getActionTime(skl.getKnowledge());
			int actioncost = getActionCost(skl.getKnowledge(), getAge(target), getMaxAge());
			String itemname = target.getName();

			if(counter == 1.0f){
				if(!checkItemType(target)) return true;
				if(checkConditions) if(checkItemConditions(performer, target)) return true;
				if(checkIfPolled) if(checkStatus(performer, target.getWurmId())) return true;

				if(item != 0) if(checkItem(performer, source, itemname, actioncost)) return true;

				startAction(performer, itemname, timeLeft);
			}else{
				timeLeft = performer.getCurrentAction().getTimeLeft();
			}
			if(counter * 10.0F > timeLeft){
				double quality = 100.0;
				if(item != 0) quality = source.getCurrentQualityLevel();

				double multiplier = getTaskTimeMultiplier(quality, skl.getKnowledge());
				double chance = getRandomChanceMultiplier(quality, skl.getKnowledge());
				double rnd = getRandomTimeMultiplier(quality, skl.getKnowledge());
				performItemAction(target, multiplier, chance, rnd);

				if(item != 0) source.setWeight(source.getWeightGrams() - actioncost, true);
				if(gainSkill) gainSkill(skl);

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

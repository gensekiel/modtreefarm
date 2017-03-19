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
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.zones.Zones;

public class HedgeAction extends AbstractAction
{
	public HedgeAction(){ this("Water"); }
//======================================================================
	protected HedgeAction(String s)
	{
		super(s, AbstractAction.ActionFlavor.WATER_ACTION);

		cost = 5000;
		time = 30;
		item = ItemList.water;
		skill = 10045;
	}
//======================================================================
	protected void performFenceAction(Fence fence, double multiplier)
	{
		TaskPoller.addTask(new HedgeTask(fence, multiplier));
	}
//======================================================================
	protected boolean checkFenceConditions(Creature performer, Fence fence)
	{
		if(!fence.isHedge()){
			performer.getCommunicator().sendNormalServerMessage("This " + fence.getName() + " is not a hedge...", (byte)1);
			return true;
		}

		if(!HedgeTask.canGrow(fence)){
			performer.getCommunicator().sendNormalServerMessage("This " + fence.getName() + " seems to have reached its maximum height.", (byte)1);
			return true;
		}

		return false;
	}
//======================================================================
	protected boolean checkFenceType(Fence fence)
	{
		return HedgeTask.checkFenceType(fence);
	}
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Fence fence)
	{
		if(obeyProtection && Zones.protectedTiles[fence.getTileX()][fence.getTileY()]) return null;

		if(fence.isHedge() && fence.isFinished()){
			return Arrays.asList(actionEntry);
		}
		return null;
	}
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Fence fence)
	{
		return getBehavioursFor(performer, null, fence);
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, Item source, boolean onSurface, Fence target, short num, float counter)
	{
		try{
			Skill skl = performer.getSkills().getSkillOrLearn(skill);
			int timeLeft = getActionTime(skl.knowledge);
			int actioncost = getActionCost(skl.knowledge, HedgeTask.getHedgeAge(target), 2);
			String fencename = target.getName();

			if(counter == 1.0f){
				if(!checkFenceType(target)) return true;
				if(checkConditions) if(checkFenceConditions(performer, target)) return true;
				if(checkIfPolled) if(checkStatus(performer, target.getId())) return true;

				if(item != 0) if(checkItem(performer, source, fencename, actioncost)) return true;

				startAction(performer, fencename, timeLeft);
				if(gainSkill) gainSkill(skl);
			}else{
				timeLeft = performer.getCurrentAction().getTimeLeft();
			}
			if(counter * 10.0F > timeLeft){
				double quality = 100.0;
				if(item != 0) quality = source.getCurrentQualityLevel();

				double multiplier = getTaskTimeMultiplier(quality, skl.knowledge);
				performFenceAction(target, multiplier);

				if(item != 0) source.setWeight(source.getWeightGrams() - actioncost, true);

				finishAction(performer, fencename);

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
	public boolean action(Action action, Creature performer, boolean onSurface, Fence target, short num, float counter)
	{
		return action(action, performer, null, onSurface, target, num, counter);
	}
//======================================================================
}

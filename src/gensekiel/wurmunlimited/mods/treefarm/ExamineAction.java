package gensekiel.wurmunlimited.mods.treefarm;

import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.structures.Fence;

public class ExamineAction implements ModAction, ActionPerformer
{
//======================================================================
	@Override
	public short getActionId(){ return Actions.EXAMINE; }
//======================================================================
	public void action(Creature performer, long key)
	{
		AbstractTask at = TaskPoller.containsTaskFor(key);
		if(at != null){
			performer.getCommunicator().sendNormalServerMessage(at.getDescription());
		}
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int rawtile, short num, float counter)
	{
		boolean ret = ActionPerformer.super.action(action, performer, source, tilex, tiley, onSurface, heightOffset, rawtile, num, counter);
		action(performer, TreeTileTask.getTaskKey(tilex, tiley));
		return ret;
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int rawtile, short num, float counter)
	{
		boolean ret = ActionPerformer.super.action(action, performer, tilex, tiley, onSurface, rawtile, num, counter);
		action(performer, TreeTileTask.getTaskKey(tilex, tiley));
		return ret;
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, Item source, boolean onSurface, Fence target, short num, float counter)
	{
		boolean ret = ActionPerformer.super.action(action, performer, source, onSurface, target, num, counter); 
		action(performer, target.getId());
		return ret;
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, boolean onSurface, Fence target, short num, float counter)
	{
		boolean ret = ActionPerformer.super.action(action, performer, onSurface, target, num, counter);
		action(performer, target.getId());
		return ret;
	}
//======================================================================
}

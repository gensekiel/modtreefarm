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
	private static boolean debug = false;
	public static boolean getDebug(){ return debug; }
	public static void setDebug(boolean b){ debug = b; }
//======================================================================
	@Override
	public short getActionId(){ return Actions.EXAMINE; }
//======================================================================
	public void action(Creature performer, long key)
	{
		AbstractTask at = TaskPoller.containsTaskFor(key);
		if(debug){
			long now = System.currentTimeMillis();
			long lastpoll = TaskPoller.getLastPolled();
			long nextpoll = lastpoll + TaskPoller.getPollInterval();
			performer.getCommunicator().sendNormalServerMessage("Last polled  " + (now - lastpoll) / 1000.0 + " seconds ago.");
			performer.getCommunicator().sendNormalServerMessage("Next poll in " + (nextpoll - now) / 1000.0 + " seconds.");
			if(at != null){
				long tasktime = at.getTaskTime();
				long timestamp = at.getTimeStamp();
				performer.getCommunicator().sendNormalServerMessage("Task timestamp : " + timestamp);
				performer.getCommunicator().sendNormalServerMessage("Task time total: " + (tasktime / 1000.0) + " seconds.");
				performer.getCommunicator().sendNormalServerMessage("Task time left : " + (tasktime - now + timestamp) / 1000.0 + " seconds.");
			}
		}
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

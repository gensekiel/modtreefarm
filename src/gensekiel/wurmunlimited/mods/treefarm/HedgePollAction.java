package gensekiel.wurmunlimited.mods.treefarm;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.structures.Fence;

public class HedgePollAction extends ActionTemplate
{
	private static Logger logger = Logger.getLogger(HedgePollAction.class.getName());
//======================================================================
	public HedgePollAction(){ menuEntry = "Poll"; }
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Fence fence)
	{
		if(performer instanceof Player && performer.getPower() >= 5){
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
		logger.log(Level.INFO, "Polling fence " + target.getId());
		long lastused = System.currentTimeMillis();
		target.poll(lastused + 86400001L);
		target.setLastUsed(lastused);
		return true;
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, boolean onSurface, Fence target, short num, float counter)
	{
		return action(action, performer, null, onSurface, target, num, counter);
	}
//======================================================================
}

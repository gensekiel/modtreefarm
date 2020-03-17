package gensekiel.wurmunlimited.mods.treefarm;

import java.util.Arrays;
import java.util.List;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;

public class BlessAction extends ActionTemplate
{
//======================================================================
	protected BlessAction(){ menuEntry = "-> Bless"; }
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Item target)
	{
		if(target == null) return null;
		if(performer instanceof Player && performer.getPower() >= 5){
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
		if(target == null) return true;
		target.bless(1); // Fo
		return true;
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, Item target, short num, float counter)
	{
		return action(action, performer, null, target, num, counter);
	}
//======================================================================
}

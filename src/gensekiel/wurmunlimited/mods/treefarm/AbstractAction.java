package gensekiel.wurmunlimited.mods.treefarm;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;

public abstract class AbstractAction implements ModAction, BehaviourProvider, ActionPerformer
{
	protected static Logger logger = Logger.getLogger(WateringAction.class.getName());
	protected short actionId;
	protected ActionEntry actionEntry;
//======================================================================
	protected int cost;
	protected int time;
	protected int item;
	protected static boolean checkIfPolled;
	protected static boolean checkConditions;
//======================================================================
	protected String menuEntry;
	protected String actionVerb;
	protected String actionVerbIng;
	protected String actionDesc;
//======================================================================
	public void setCost(int i){ cost = i; }
	public void setTime(int i){ time = i; }
	public void setItem(int i){ item = i; }
	public static void setCheckIfPolled(boolean b){ checkIfPolled = b; }
	public static void setCheckConditions(boolean b){ checkConditions = b; }
//======================================================================
	public int getCost(){ return cost; }
	public int getTime(){ return time; }
	public int getItem(){ return item; }
	public static boolean getCheckIfPolled(){ return checkIfPolled; }
	public static boolean getCheckConditions(){ return checkConditions; }
//======================================================================
	protected AbstractAction(String menu, String verb, String verbing, String desc)
	{
		menuEntry = menu;
		actionVerb = verb;
		actionVerbIng = verbing;
		actionDesc = desc;
	}
//======================================================================
	public void registerAction()
	{
		actionId = (short)ModActions.getNextActionId();
		actionEntry = ActionEntry.createEntry(actionId, menuEntry, actionVerbIng, new int[] {6, 48, 35});
		ModActions.registerAction(actionEntry);
	}
//======================================================================
	protected abstract void performTileAction(int tile, int tilex, int tiley);
//======================================================================
	// Called when action is started.
	protected abstract boolean checkConditions(Creature performer, int tile);
//======================================================================
	// Called when behavior is provided.
	protected abstract boolean checkTileType(int tile);
//======================================================================
	@Override
	public short getActionId(){ return actionId; }
	@Override
	public BehaviourProvider getBehaviourProvider(){ return this; }
	@Override
	public ActionPerformer getActionPerformer(){ return this; }
//======================================================================
	public boolean checkItem(Creature performer, Item source)
	{
		if(source == null || source.getTemplateId() != item){
			performer.getCommunicator().sendNormalServerMessage("You have nothing to " + actionVerb + " a tree with.");
			return true;
		}
		
		int available = source.getWeightGrams();
		if (available < cost){
			performer.getCommunicator().sendNormalServerMessage("You carry too little " + source.getActualName() + " to " + actionVerb + " the tree.");
			return true;
		}

		return false;
	}
//======================================================================
	public void startAction(Creature performer) throws NoSuchActionException
	{
		performer.getCommunicator().sendNormalServerMessage("You start to " + actionVerb + " the tree.");
		Server.getInstance().broadCastAction(performer.getName() + " starts to " + actionVerb + " a tree.", performer, 5);
		performer.getCurrentAction().setTimeLeft(time);
		performer.sendActionControl(actionDesc, true, time);
	}
//======================================================================
	public void finishAction(Creature performer)
	{
		performer.getCommunicator().sendNormalServerMessage("You finish " + actionDesc + " the tree.");
		Server.getInstance().broadCastAction(performer.getName() + " finishes to " + actionVerb + " a tree.", performer, 5);
	}
//======================================================================
	private boolean checkStatus(Creature performer, int x, int y)
	{
		AbstractTask aa = TreeTilePoller.containsTileAt(x, y);
		if(aa != null){
			performer.getCommunicator().sendNormalServerMessage(aa.getDescription());
			return true;
		}
		return false;
	}
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile)
	{
		if(item == 0){
			if(checkTileType(tile) && performer instanceof Player) 
				return Arrays.asList(actionEntry);
			else return null;
		}
		
		if(   checkTileType(tile)
			&& performer instanceof Player
			&& object != null
			&& object.getTemplateId() == item)
		{
			return Arrays.asList(actionEntry);
		}else return null;
	}
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile)
	{
		if(item == 0) return getBehavioursFor(performer, null, tilex, tiley, onSurface, tile);
		return null;
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter)
	{
		try{
			if(counter == 1.0f){
				if(!checkTileType(tile)) return true;
				if(checkConditions) if(checkConditions(performer, tile)) return true;
				if(checkIfPolled) if(checkStatus(performer, tilex, tiley)) return true;
				if(item != 0) if(checkItem(performer, source)) return true;
				startAction(performer);
			}else{
				int timeLeft = performer.getCurrentAction().getTimeLeft();
				// TODO Can the tile change while action is performed?
				if(counter * 10.0F > timeLeft){
					performTileAction(tile, tilex, tiley);

					// Source item can not change.
					if(item != 0) source.setWeight(source.getWeightGrams() - cost, true);

					finishAction(performer);
					// What if item is moved to container while action is 
					// performed?
					// Tested: Used item cannot be moved.
					return true;
				}
			}
			return false;
		}catch(Exception e){
			logger.log(Level.WARNING, e.getMessage(), e);
			return true;
		}
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, short num, float counter)
	{
		if(item == 0) return action(action, performer, null, tilex, tiley, onSurface, 0, tile, num, counter);
		return true;
	}
//======================================================================
}

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
	protected static boolean allowTrees;
	protected static boolean allowBushes;
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
	public static void setAllowTrees(boolean b){ allowTrees = b; }
	public static void setAllowBushes(boolean b){ allowBushes = b; }
//======================================================================
	public int getCost(){ return cost; }
	public int getTime(){ return time; }
	public int getItem(){ return item; }
	public static boolean getCheckIfPolled(){ return checkIfPolled; }
	public static boolean getCheckConditions(){ return checkConditions; }
	public static boolean getAllowTrees(){ return allowTrees; }
	public static boolean getAllowBushes(){ return allowBushes; }
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
	protected abstract void performTileAction(int rawtile, int tilex, int tiley);
//======================================================================
	// Called when action is started.
	protected abstract boolean checkConditions(Creature performer, int rawtile);
//======================================================================
	// Called when behavior is provided.
	protected abstract boolean checkTileType(int rawtile);
//======================================================================
	@Override
	public short getActionId(){ return actionId; }
	@Override
	public BehaviourProvider getBehaviourProvider(){ return this; }
	@Override
	public ActionPerformer getActionPerformer(){ return this; }
//======================================================================
	public boolean checkItem(Creature performer, Item source, String tilename)
	{
		if(source == null){
			performer.getCommunicator().sendNormalServerMessage("You have nothing in your hands to " + actionVerb + " the " + tilename + " with.", (byte)1);
			return true;
		}
		if(source.getTemplateId() != item){
			// Get item name from ID:
			// ItemTemplate.getInstance().getTemplate(item).getName()
			performer.getCommunicator().sendNormalServerMessage("You cannot use " + source.getNameWithGenus() + " to " + actionVerb + " the " + tilename + ".", (byte)1);
			return true;
		}
		
		int available = source.getWeightGrams();
		if (available < cost){
			performer.getCommunicator().sendNormalServerMessage("You have too little " + source.getActualName() + " to " + actionVerb + " the " + tilename + ".", (byte)1);
			return true;
		}

		return false;
	}
//======================================================================
	public void startAction(Creature performer, String tilename) throws NoSuchActionException
	{
		performer.getCommunicator().sendNormalServerMessage("You start to " + actionVerb + " the " + tilename + ".");
		Server.getInstance().broadCastAction(performer.getName() + " starts to " + actionVerb + " some " + tilename + ".", performer, 5);
		performer.getCurrentAction().setTimeLeft(time);
		performer.sendActionControl(actionDesc, true, time);
	}
//======================================================================
	public void finishAction(Creature performer, String tilename)
	{
		performer.getCommunicator().sendNormalServerMessage("You finish " + actionVerbIng + " the " + tilename + ".");
		Server.getInstance().broadCastAction(performer.getName() + " finishes to " + actionVerb + " some " + tilename + ".", performer, 5);
	}
//======================================================================
	private boolean checkStatus(Creature performer, int x, int y, int rawtile)
	{
		AbstractTask aa = TreeTilePoller.containsTileAt(x, y);
		if(aa != null){
			performer.getCommunicator().sendNormalServerMessage(aa.getDescription(rawtile), (byte)1);
			return true;
		}
		return false;
	}
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int rawtile)
	{
		if(   checkTileType(rawtile)
			&& performer instanceof Player)
		{
			return Arrays.asList(actionEntry);
		}else return null;
	}
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int rawtile)
	{
		return getBehavioursFor(performer, null, tilex, tiley, onSurface, rawtile);
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int rawtile, short num, float counter)
	{
		try{
			String tilename = TreeTile.getTileName(rawtile);
			if(counter == 1.0f){
				if(!checkTileType(rawtile)) return true;
				if(checkConditions) if(checkConditions(performer, rawtile)) return true;
				if(checkIfPolled) if(checkStatus(performer, tilex, tiley, rawtile)) return true;
				if(item != 0) if(checkItem(performer, source, tilename)) return true;
				startAction(performer, tilename);
			}else{
				int timeLeft = performer.getCurrentAction().getTimeLeft();
				// TODO Can the tile change while action is performed?
				if(counter * 10.0F > timeLeft){
					performTileAction(rawtile, tilex, tiley);

					// Source item can not change.
					if(item != 0) source.setWeight(source.getWeightGrams() - cost, true);

					finishAction(performer, tilename);
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
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int rawtile, short num, float counter)
	{
		return action(action, performer, null, tilex, tiley, onSurface, 0, rawtile, num, counter);
	}
//======================================================================
}

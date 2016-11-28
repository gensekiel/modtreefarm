package gensekiel.wurmunlimited.mods.treefarm;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.skills.Skill;

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
	protected static double costSkillMultiplier;
	protected static double costAgeMultiplier;
	protected static double timeSkillMultiplier;
	protected static double growthTimeQualityMultiplier;
	protected static double growthTimeSkillMultiplier;
	protected static boolean gainSkill;
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
	public static void setCostSkillMultiplier(double d){ costSkillMultiplier = d; }
	public static void setCostAgeMultiplier(double d){ costAgeMultiplier = d; }
	public static void setTimeSkillMultiplier(double d){ timeSkillMultiplier = d; }
	public static void setGrowthTimeQualityMultiplier(double d){ growthTimeQualityMultiplier = d; }
	public static void setGrowthTimeSkillMultiplier(double d){ growthTimeSkillMultiplier = d; }
	public static void setGainSkill(boolean b){ gainSkill = b; }
//======================================================================
	public int getCost(){ return cost; }
	public int getTime(){ return time; }
	public int getItem(){ return item; }
	public static boolean getCheckIfPolled(){ return checkIfPolled; }
	public static boolean getCheckConditions(){ return checkConditions; }
	public static boolean getAllowTrees(){ return allowTrees; }
	public static boolean getAllowBushes(){ return allowBushes; }
	public static double getCostSkillMultiplier(){ return costSkillMultiplier; }
	public static double getCostAgeMultiplier(){ return costAgeMultiplier; }
	public static double getTimeSkillMultiplier(){ return timeSkillMultiplier; }
	public static double getGrowthTimeQualityMultiplier(){ return growthTimeQualityMultiplier; }
	public static double getGrowthTimeSkillMultiplier(){ return growthTimeSkillMultiplier; }
	public static boolean getGainSkill(){ return gainSkill; }
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
	protected abstract void performTileAction(int rawtile, int tilex, int tiley, double multiplier);
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
	private static double interpolate(double limit, double value, double maxv)
	{
		return 1.0 + (limit - 1.0) * value / maxv;
	}
//======================================================================
	public int getActionCost(double knowledge, int tree_age)
	{
		double multiplier =   interpolate(costSkillMultiplier, knowledge, 100.0)
		                    * interpolate(costAgeMultiplier, tree_age, 15.0);
		return Math.max(0, (int)(cost * multiplier));
	}
//======================================================================
	public int getActionTime(double knowledge)
	{
		double multiplier = interpolate(timeSkillMultiplier, knowledge, 100.0);
		return Math.max(0, (int)(time * multiplier));
	}
//======================================================================
	public double getTaskTimeMultiplier(double quality, double knowledge)
	{
		return Math.max(0.0,
		          interpolate(growthTimeQualityMultiplier, quality, 100.0)
		        * interpolate(growthTimeSkillMultiplier, knowledge, 100.0)
		);
	}
//======================================================================
	public double gainSkill(Skill skill)
	{
		float times = 10.0f;
		double div = 2.0;
		return skill.skillCheck(1.0, null, 1.0, false, times, true, div);
	}
//======================================================================
	public boolean checkItem(Creature performer, Item source, String tilename, int amount)
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
		if (available < amount){
			performer.getCommunicator().sendNormalServerMessage("You have too little " + source.getActualName() + " to " + actionVerb + " the " + tilename + ".", (byte)1);
			return true;
		}

		return false;
	}
//======================================================================
	public void startAction(Creature performer, String tilename, int actiontime) throws NoSuchActionException
	{
		performer.getCommunicator().sendNormalServerMessage("You start to " + actionVerb + " the " + tilename + ".");
		Server.getInstance().broadCastAction(performer.getName() + " starts to " + actionVerb + " some " + tilename + ".", performer, 5);
		performer.getCurrentAction().setTimeLeft(actiontime);
		performer.sendActionControl(actionDesc, true, actiontime);
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
		if(!allowTrees && TreeTile.getTile(rawtile).isTree()) return null;
		if(!allowBushes && TreeTile.getTile(rawtile).isBush()) return null;

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
			byte tiledata = Tiles.decodeData(rawtile);
			Skill skill = performer.getSkills().getSkillOrLearn(10048);
			int timeLeft = getActionTime(skill.knowledge);
			int actioncost = getActionCost(skill.knowledge, TreeTile.getAge(tiledata));
			
			if(counter == 1.0f){
				if(!checkTileType(rawtile)) return true;
				if(checkConditions) if(checkConditions(performer, rawtile)) return true;
				if(checkIfPolled) if(checkStatus(performer, tilex, tiley, rawtile)) return true;

				if(item != 0) if(checkItem(performer, source, tilename, actioncost)) return true;
				
				startAction(performer, tilename, timeLeft);
				if(gainSkill) gainSkill(skill);
			}else{
				timeLeft = performer.getCurrentAction().getTimeLeft();
			}
			if(counter * 10.0F > timeLeft){
				double quality = 100.0;
				if(item != 0) quality = source.getCurrentQualityLevel();
				
				// Can the tile change while action is performed?
				// There seems to be a flag that could lock a tile.
				double multiplier = getTaskTimeMultiplier(quality, skill.knowledge);
				performTileAction(rawtile, tilex, tiley, multiplier);

				// Source item can not change.
				if(item != 0) source.setWeight(source.getWeightGrams() - actioncost, true);

				finishAction(performer, tilename);
				// What if item is moved to container while action is 
				// performed?
				// Tested: Used item cannot be moved.
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
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int rawtile, short num, float counter)
	{
		return action(action, performer, null, tilex, tiley, onSurface, 0, rawtile, num, counter);
	}
//======================================================================
}

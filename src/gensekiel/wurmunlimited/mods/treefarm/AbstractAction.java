package gensekiel.wurmunlimited.mods.treefarm;

import java.util.logging.Logger;

import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.Skill;

public abstract class AbstractAction extends ActionTemplate
{
	protected static Logger logger = Logger.getLogger(WateringAction.class.getName());
//======================================================================
	protected int cost;
	protected int time;
	protected int item;
	protected int skill;
	protected static boolean checkIfPolled = true;
	protected static boolean checkConditions = true;
	protected static double costSkillMultiplier = 1.0;
	protected static double costAgeMultiplier = 1.0;
	protected static double timeSkillMultiplier = 1.0;
	protected static double growthTimeQualityMultiplier = 1.0;
	protected static double growthTimeSkillMultiplier = 1.0;
	protected static double chanceSkillMultiplier = 1.0;
	protected static double chanceQualityMultiplier = 1.0;
	protected static double rndSkillMultiplier = 1.0;
	protected static double rndQualityMultiplier = 1.0;
	protected static boolean gainSkill = true;
	protected static boolean obeyProtection = true;
//======================================================================
	protected String actionVerb;
	protected String actionDesc;
//======================================================================
	public void setCost(int i){ cost = i; }
	public void setTime(int i){ time = i; }
	public void setItem(int i){ item = i; }
	public static void setCheckIfPolled(boolean b){ checkIfPolled = b; }
	public static void setCheckConditions(boolean b){ checkConditions = b; }
	public static void setCostSkillMultiplier(double d){ costSkillMultiplier = d; }
	public static void setCostAgeMultiplier(double d){ costAgeMultiplier = d; }
	public static void setTimeSkillMultiplier(double d){ timeSkillMultiplier = d; }
	public static void setGrowthTimeQualityMultiplier(double d){ growthTimeQualityMultiplier = d; }
	public static void setGrowthTimeSkillMultiplier(double d){ growthTimeSkillMultiplier = d; }
	public static void setChanceSkillMultiplier(double d){ chanceSkillMultiplier = d; }
	public static void setChanceQualityMultiplier(double d){ chanceQualityMultiplier = d; }
	public static void setRndSkillMultiplier(double d){ rndSkillMultiplier = d; }
	public static void setRndQualityMultiplier(double d){ rndQualityMultiplier = d; }
	public static void setGainSkill(boolean b){ gainSkill = b; }
	public static void setObeyProtection(boolean b){ obeyProtection = b; }
//======================================================================
	public int getCost(){ return cost; }
	public int getTime(){ return time; }
	public int getItem(){ return item; }
	public static boolean getCheckIfPolled(){ return checkIfPolled; }
	public static boolean getCheckConditions(){ return checkConditions; }
	public static double getCostSkillMultiplier(){ return costSkillMultiplier; }
	public static double getCostAgeMultiplier(){ return costAgeMultiplier; }
	public static double getTimeSkillMultiplier(){ return timeSkillMultiplier; }
	public static double getGrowthTimeQualityMultiplier(){ return growthTimeQualityMultiplier; }
	public static double getGrowthTimeSkillMultiplier(){ return growthTimeSkillMultiplier; }
	public static double getChanceSkillMultiplier(){ return chanceSkillMultiplier; }
	public static double getChanceQualityMultiplier(){ return chanceQualityMultiplier; }
	public static double getRndSkillMultiplier(){ return rndSkillMultiplier; }
	public static double getRndQualityMultiplier(){ return rndQualityMultiplier; }
	public static boolean getGainSkill(){ return gainSkill; }
	public static boolean getObeyProtection(){ return obeyProtection; }
//======================================================================
	public enum ActionFlavor{
		WATER_ACTION,
		FERTILIZE_ACTION
	}
//======================================================================
	protected AbstractAction(ActionFlavor f)
	{
		switch(f){
		case WATER_ACTION:
			menuEntry = "Water";
			actionVerb = "water";
			actionVerbIng = "watering";
			actionDesc = "Watering";
			break;
		case FERTILIZE_ACTION:
			menuEntry = "Fertilize";
			actionVerb = "fertilize";
			actionVerbIng = "fertilizing";
			actionDesc = "Fertilizing";
			break;
		}
	}
//======================================================================
	protected AbstractAction(String menu, ActionFlavor f)
	{
		this(f);
		menuEntry = menu;
	}
//======================================================================
	private static double interpolate(double limit, double value, double maxv)
	{
		return 1.0 + (limit - 1.0) * value / maxv;
	}
//======================================================================
	public int getActionCost(double knowledge, int age, int max_age)
	{
		double multiplier =   interpolate(costSkillMultiplier, knowledge, 100.0)
		                    * interpolate(costAgeMultiplier, age, max_age);
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
	public double getRandomChanceMultiplier(double quality, double knowledge)
	{
		return Math.max(0.0,
		          interpolate(chanceQualityMultiplier, quality, 100.0)
		        * interpolate(chanceSkillMultiplier, knowledge, 100.0)
		);
	}
//======================================================================
	public double getRandomTimeMultiplier(double quality, double knowledge)
	{
		return Math.max(0.0,
		          interpolate(rndQualityMultiplier, quality, 100.0)
		        * interpolate(rndSkillMultiplier, knowledge, 100.0)
		);
	}
//======================================================================
	public double gainSkill(Skill skl)
	{
		float times = 10.0f;
		double div = 2.0;
		return skl.skillCheck(1.0, null, 1.0, false, times, true, div);
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
	protected boolean checkStatus(Creature performer, long key)
	{
		AbstractTask at = TaskPoller.containsTaskFor(key);
		if(at != null){
			performer.getCommunicator().sendNormalServerMessage(at.getDescription(), (byte)1);
			return true;
		}
		return false;
	}
}

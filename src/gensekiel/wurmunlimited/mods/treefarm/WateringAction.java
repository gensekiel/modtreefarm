package gensekiel.wurmunlimited.mods.treefarm;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Server;
import com.wurmonline.mesh.Tiles;

public class WateringAction implements ModAction, BehaviourProvider, ActionPerformer
{
	private static Logger logger = Logger.getLogger(WateringAction.class.getName());
	private final short actionId;
	private final ActionEntry actionEntry;
//======================================================================
	private static int wateringCost = 5000;
	private static int wateringTime = 30;
	private static boolean wateringCheck = true;
	private static boolean treeAgeCheck = true;
//======================================================================
	public static void setWateringCost(int i){ wateringCost = i; }
	public static void setWateringTime(int i){ wateringTime = i; }
	public static void setWateringCheck(boolean b){ wateringCheck = b; }
	public static void setTreeAgeCheck(boolean b){ treeAgeCheck = b; }
//======================================================================
	public static int getWateringCost(){ return wateringCost; }
	public static int getWateringTime(){ return wateringTime; }
	public static boolean getWateringCheck(){ return wateringCheck; }
	public static boolean getTreeAgeCheck(){ return treeAgeCheck; }
//======================================================================
	public WateringAction()
	{
		actionId = (short) ModActions.getNextActionId();
		actionEntry = ActionEntry.createEntry(actionId, "Water", "watering", new int[] {6, 48, 36});
		ModActions.registerAction(actionEntry);
	}
//======================================================================
	@Override
	public BehaviourProvider getBehaviourProvider(){ return this; }
//======================================================================
	@Override
	public ActionPerformer getActionPerformer(){ return this; }
//======================================================================
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile)
	{
		byte type = Tiles.decodeType(tile);
		Tiles.Tile theTile = Tiles.getTile(type);
		if((theTile.isNormalTree() || theTile.isEnchantedTree() || theTile.isMyceliumTree())
			&& performer instanceof Player
			&& object != null
			&& object.getTemplateId() == ItemList.water)
		{
			return Arrays.asList(actionEntry);
		}else{
			return null;
		}
	}
//======================================================================
	@Override
	public short getActionId(){ return actionId; }
//======================================================================
	private int checkWater(Creature performer, Item carried_water)
	{
		if(carried_water == null){
			performer.getCommunicator().sendNormalServerMessage("You need water to water a tree.");
			return 0;
		}
		
		int water_available = carried_water.getWeightGrams();
		if (water_available < wateringCost){
			performer.getCommunicator().sendNormalServerMessage("You carry too little water to water the tree.");
			return 0;
		}

		return water_available;
	}
//======================================================================
	private boolean checkStatus(Creature performer, int x, int y)
	{
		if(wateringCheck){
			if(TreeTilePoller.containsTileAt(x, y)){
				performer.getCommunicator().sendNormalServerMessage("The tree was recently watered.");
				return true;
			}
		}
		return false;
	}
//======================================================================
	private boolean checkTreeAge(Creature performer, int tile)
	{
		if(treeAgeCheck){
			byte data = Tiles.decodeData(tile);
			int age = TreeTilePoller.getAgeFromData(data);
			if(age >= TreeTilePoller.getAgeLimit()){
				performer.getCommunicator().sendNormalServerMessage("The tree is too old to make it grow by watering it.");
				return true;
			}
		}
		return false;
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter)
	{
		try{
			if(counter == 1.0f){
				if(checkTreeAge(performer, tile)) return true;
				if(checkStatus(performer, tilex, tiley)) return true;
				
				int water_available = checkWater(performer, source);
				if(water_available == 0) return true;

				performer.getCommunicator().sendNormalServerMessage("You start to water the tree.");
				Server.getInstance().broadCastAction(performer.getName() + " starts to water a tree.", performer, 5);

				performer.getCurrentAction().setTimeLeft(wateringTime);
				performer.sendActionControl("Watering", true, wateringTime);
			}else{
				int time = performer.getCurrentAction().getTimeLeft();
				if(counter * 10.0F > time){
					int water_available = checkWater(performer, source);
					if(water_available == 0) return true;
					
					source.setWeight(water_available - wateringCost, true);
					performer.getCommunicator().sendNormalServerMessage("You finish watering the tree.");
					Server.getInstance().broadCastAction(performer.getName() + " finishes to water a tree.", performer, 5);
					
					TreeTilePoller.addTreeTile(tile, tilex, tiley);
					
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
}

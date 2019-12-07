package gensekiel.wurmunlimited.mods.treefarm;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.zones.Zones;

public abstract class TileAction extends AbstractAction
{
	protected static boolean allowTrees;
	protected static boolean allowBushes;
	public static void setAllowTrees(boolean b){ allowTrees = b; }
	public static void setAllowBushes(boolean b){ allowBushes = b; }
	public static boolean getAllowTrees(){ return allowTrees; }
	public static boolean getAllowBushes(){ return allowBushes; }
//======================================================================
	protected TileAction(String s, AbstractAction.ActionFlavor f){ super(s, f); }
//======================================================================
	protected abstract void performTileAction(int rawtile, int tilex, int tiley, double multiplier);
	protected abstract boolean checkTileConditions(Creature performer, int rawtile, int tilex, int tiley);
	protected abstract boolean checkTileType(int rawtile);
//======================================================================
	protected byte getAge(byte tiledata){ return 0; }
//======================================================================
	protected byte getMaxAge(){ return 1; }
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int rawtile)
	{
		if(obeyProtection && Zones.protectedTiles[tilex][tiley]) return null;

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
			String tilename = TileTask.getTileName(rawtile);
			byte tiledata = Tiles.decodeData(rawtile);
			Skill skl = performer.getSkills().getSkillOrLearn(skill);
			int timeLeft = getActionTime(skl.getKnowledge());
			int actioncost = getActionCost(skl.getKnowledge(), getAge(tiledata), getMaxAge());

			if(counter == 1.0f){
				if(!checkTileType(rawtile)) return true;
				if(checkConditions) if(checkTileConditions(performer, rawtile, tilex, tiley)) return true;
				if(checkIfPolled) if(checkStatus(performer, TreeTileTask.getTaskKey(tilex, tiley))) return true;

				if(item != 0) if(checkItem(performer, source, tilename, actioncost)) return true;

				startAction(performer, tilename, timeLeft);
			}else{
				timeLeft = performer.getCurrentAction().getTimeLeft();
			}
			if(counter * 10.0F > timeLeft){
				double quality = 100.0;
				if(item != 0) quality = source.getCurrentQualityLevel();

				// Can the tile change while action is performed?
				// There seems to be a flag that could lock a tile.
				double multiplier = getTaskTimeMultiplier(quality, skl.getKnowledge());
				performTileAction(rawtile, tilex, tiley, multiplier);

				// Source item can not change.
				if(item != 0) source.setWeight(source.getWeightGrams() - actioncost, true);
				if(gainSkill) gainSkill(skl);

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

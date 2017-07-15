package gensekiel.wurmunlimited.mods.treefarm;

import java.util.Arrays;
import java.util.List;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Wall;

public abstract class DebugAction extends ActionTemplate
{
//======================================================================
	protected DebugAction(String menu){ menuEntry = menu; }
//======================================================================
	protected abstract void action(Creature performer);
//======================================================================
	protected List<ActionEntry> getBehaviorsFor(Creature performer){
		if(performer instanceof Player && performer.getPower() >= 5) return Arrays.asList(actionEntry);
		else return null;
	}
//======================================================================
//	@Override public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter){ action(performer); return true; }
//	@Override public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, int planetId, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item source, int planetId, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Wound target, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item source, Wound target, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item target, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Creature target, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item source, Wall target, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Wall target, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item source, boolean onSurface, Fence target, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, boolean onSurface, Fence target, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item source, Skill skill, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Skill skill, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item source, boolean onSurface, Floor target, int encodedTile, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, boolean onSurface, Floor floor, int encodedTile, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, Tiles.TileBorderDirection dir, long borderId, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, Tiles.TileBorderDirection dir, long borderId, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item[] targets, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter){ action(performer); return true; }
	@Override public boolean action(Action action, Creature performer, Item item, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter){ action(performer); return true; }
//======================================================================
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, long target){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile, int dir){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile, int dir){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, Tiles.TileBorderDirection dir, boolean border, int heightOffset){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, Tiles.TileBorderDirection dir, boolean border, int heightOffset){ return getBehaviorsFor(performer); }
//	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, boolean corner, int tile){ return getBehaviorsFor(performer); }
//	@Override public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Skill skill){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Skill skill){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item target){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Item target){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Wound target){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Wound target){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Creature target){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Creature target){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Wall target){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Wall target){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Fence target){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Fence target){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int planetId){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, int planetId){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, boolean onSurface, Floor floor){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item item, boolean onSurface, Floor floor){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, boolean aOnSurface, BridgePart aBridgePart){ return getBehaviorsFor(performer); }
	@Override public List<ActionEntry> getBehavioursFor(Creature performer, Item item, boolean aOnSurface, BridgePart aBridgePart){ return getBehaviorsFor(performer); }
//======================================================================
}

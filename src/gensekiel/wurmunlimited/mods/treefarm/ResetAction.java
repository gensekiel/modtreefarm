package gensekiel.wurmunlimited.mods.treefarm;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.shared.constants.StructureConstantsEnum;

public class ResetAction extends ActionTemplate
{
//======================================================================
	protected ResetAction(){ menuEntry = "Reset growth"; }
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int rawtile)
	{
		if(performer instanceof Player && performer.getPower() >= 5){
			Tiles.Tile ttile = TileTask.getTile(rawtile);
			if((ttile.isBush() || ttile.isTree() || ttile.isGrass() || FarmGrowTask.checkTileType(rawtile)) && performer instanceof Player){
				return Arrays.asList(actionEntry);
			}else return null;
		}
		return null;
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
		Tiles.Tile ttile = TileTask.getTile(rawtile);
		byte type = TileTask.getType(rawtile);
		byte data = TileTask.getData(rawtile);
		if(ttile.isTree() || ttile.isBush()){
			Server.setWorldResource(tilex, tiley, 0);
			byte new_type = TreeTileTask.convertTile(type, data);
			byte new_data = Tiles.encodeTreeData(FoliageAge.YOUNG_ONE, TreeData.hasFruit(data), TreeData.isCentre(data), GrassData.GrowthTreeStage.SHORT);
			Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(rawtile), new_type, new_data));
			Server.modifyFlagsByTileType(tilex, tiley, new_type);
			Players.getInstance().sendChangedTile(tilex, tiley, true, false);
		}
		if(ttile.isGrass()){
			GrassData.FlowerType ft = GrassData.FlowerType.decodeTileData(data);
			byte newdata = GrassData.encodeGrassTileData(GrassData.GrowthStage.SHORT, ft);
			Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(rawtile), type, newdata));
			Server.modifyFlagsByTileType(tilex, tiley, type);
			Players.getInstance().sendChangedTile(tilex, tiley, true, false);
		}
		if(FarmGrowTask.checkTileType(rawtile)){
			Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(rawtile), type, (byte)(data & 0x8F)));
			Server.modifyFlagsByTileType(tilex, tiley, type);
			Players.getInstance().sendChangedTile(tilex, tiley, true, false);
		}
		return true;
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int rawtile, short num, float counter)
	{
		return action(action, performer, null, tilex, tiley, onSurface, 0, rawtile, num, counter);
	}
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Fence fence)
	{
		return Arrays.asList(actionEntry);
	}
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Fence fence)
	{
		if(fence.isHedge() && fence.isFinished()){
			return getBehavioursFor(performer, null, fence);
		}else return null;
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, Item source, boolean onSurface, Fence target, short num, float counter)
	{
		if(target.isHedge() && target.isFinished()){
			VolaTile vtile = AbstractTask.getVolaTile(target.getZoneId(), target.getTileX(), target.getTileY());
			target.setType(StructureConstantsEnum.getEnumByValue((short)(HedgeTask.getHedgeType(target) * 3 + StructureConstantsEnum.HEDGE_FLOWER1_LOW.value)));
			try{
				target.save();
				if(vtile != null) vtile.updateFence(target);
			}
			catch(IOException ioe){ /* oops */ }
		}
		return true;
	}
//======================================================================
	@Override
	public boolean action(Action action, Creature performer, boolean onSurface, Fence target, short num, float counter)
	{
		return action(action, performer, null, onSurface, target, num, counter);
	}
//======================================================================
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Item target)
	{
		if(target != null && target.isTrellis() || target.getTemplateId() == 1162){
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

		if(target.isTrellis()){
			target.setLeftAuxData(0);
			target.updateName();
		}
		if(target.getTemplateId() == 1162){
			target.setAuxData((byte)(target.getAuxData() & 0x80));
		}
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

package gensekiel.wurmunlimited.mods.treefarm;

import java.io.IOException;

import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.shared.constants.StructureConstantsEnum;

public class HedgeTask extends AbstractTask
{
	private static final long serialVersionUID = 3L;
//======================================================================
	private long key;
	private int zoneID;
	private int x;
	private int y;
	private int type;
	private int age;
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//======================================================================
	public HedgeTask(Fence fence, double multiplier)
	{
		super(multiplier);

		tasktime *= growthMultiplier;

		key = fence.getId();
		zoneID = fence.getZoneId();
		x = fence.getTileX();
		y = fence.getTileY();
		type = getHedgeType(fence);
		age = getHedgeAge(fence);
	}
//======================================================================
	public static boolean checkFenceType(Fence fence)
	{
		return fence.isHedge();
	}
//======================================================================
	@Override
	public boolean performCheck()
	{
		Fence fence = getFence();
		if(fence == null) return true;
		// Type will change when the hedge grows.

		if(!fence.isHedge()) return true;
		if(!fence.isFinished()) return true;
		if(getHedgeType(fence) != type) return true;

		if(checkForWUPoll){
			if(!keepGrowing && getHedgeAge(fence) != age) return true;
		}

		if(!canGrow(fence)) return true;

		return false;
	}
//======================================================================
	@Override
	public boolean performTask()
	{
		VolaTile vtile = getVolaTile(zoneID, x, y);
		Fence fence = getFence(vtile, key);
		if(fence != null){
			forceHedgeGrowth(fence, vtile);
			if(keepGrowing) return false;
		}
		return true;
	}
//======================================================================
	@Override
	public long getTaskKey()
	{
		return key;
	}
//======================================================================
	@Override
	public String getDescription()
	{
		Fence fence = getFence();
		if(fence == null) return "This hedge has been watered recently.";
		return "This " + fence.getName() + " has been watered recently.";
	}
//======================================================================
	public Fence getFence()
	{
		return getFence(getVolaTile(zoneID, x, y), key);
	}
//======================================================================
	public static Fence getFence(VolaTile vtile, long key)
	{
		if(vtile != null) return vtile.getFence(key);
		return null;
	}
//======================================================================
	public static boolean canGrow(Fence fence)
	{
		if(fence.getType() == StructureConstantsEnum.HEDGE_FLOWER1_LOW) return false; // Low Lavender
		if(fence.getType() == StructureConstantsEnum.HEDGE_FLOWER3_MEDIUM) return false; // Medium Camellia
		return !fence.isHighHedge();
	}
//======================================================================
	public static int getHedgeAge(Fence fence)
	{
		return (fence.getType().value - StructureConstantsEnum.HEDGE_FLOWER1_LOW.value) % 3;
	}
//======================================================================
	public static int getHedgeType(Fence fence)
	{
		return (fence.getType().value - StructureConstantsEnum.HEDGE_FLOWER1_LOW.value) / 3;
	}
//======================================================================
	public static void forceHedgeGrowth(Fence fence, VolaTile vtile)
	{
		fence.setType(StructureConstantsEnum.getEnumByValue((short)(fence.getType().value + 1)));
		try{
			fence.save();
			if(vtile != null) vtile.updateFence(fence);
		}
		catch(IOException ioe){ /* oops */ }
	}
//======================================================================
}

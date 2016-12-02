package gensekiel.wurmunlimited.mods.treefarm;

import java.io.IOException;

import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.Zones;

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
	private static boolean keepGrowing = false;
	public static void setKeepGrowing(boolean b){ keepGrowing = b; }
	public static boolean getKeepGrowing(){ return keepGrowing; }
//======================================================================
	public HedgeTask(Fence fence, double multiplier)
	{
		super(multiplier);
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
			if(canGrow(fence) && keepGrowing) return false;
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
		if(fence == null) return "This hedge has already been watered.";
		return "This " + fence.getName() + " has already been watered.";
	}
//======================================================================
	public static VolaTile getVolaTile(int zoneID, int x, int y)
	{
		VolaTile vtile = null;
		try{
			Zone zone = Zones.getZone(zoneID);
			vtile = zone.getTileOrNull(x, y);
		}
		catch(NoSuchZoneException nsze){ /* oops */ }
		return vtile;
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
		if(fence.getType() == 105) return false; // Low Lavender
		if(fence.getType() == 112) return false; // Medium Camellia
		return !fence.isHighHedge();
	}
//======================================================================
	public static int getHedgeAge(Fence fence)
	{
		return (fence.getType() - 105) % 3;
	}
//======================================================================
	public static int getHedgeType(Fence fence)
	{
		return (fence.getType() - 105) / 3;
	}
//======================================================================
	public static void forceHedgeGrowth(Fence fence, VolaTile vtile)
	{
		if(fence.isHedge()){
			if(!canGrow(fence)) return;
			fence.setType((byte)(fence.getType() + 1));
			try{
				fence.save();
				if(vtile != null) vtile.updateFence(fence);
			}
			catch(IOException ioe){ /* oops */ }
		}
	}
//======================================================================
}

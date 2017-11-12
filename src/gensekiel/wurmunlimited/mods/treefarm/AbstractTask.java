package gensekiel.wurmunlimited.mods.treefarm;

import java.io.Serializable;

import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.Zones;

public abstract class AbstractTask implements Serializable
{
	private static final long serialVersionUID = 3L;
//======================================================================
	protected static boolean checkForWUPoll = true;
	public static void setCheckForWUPoll(boolean b){ checkForWUPoll = b; }
	public static boolean getCheckForWUPoll(){ return checkForWUPoll; }
//----------------------------------------------------------------------
	protected static long BaseGrowthTime = 600000;
	public static void setBaseGrowthTime(long l){ BaseGrowthTime = l; }
	public static long getBaseGrowthTime(){ return BaseGrowthTime; }
//----------------------------------------------------------------------
	protected long timestamp;
	public final long getTimeStamp(){ return timestamp; }
	public final void setTimeStamp(long l){ timestamp = l; }
//----------------------------------------------------------------------
	protected long tasktime;
	public final long getTaskTime(){ return tasktime; }
//======================================================================
	public abstract boolean performCheck();
	public abstract boolean performTask();
	public abstract long getTaskKey();
	public abstract String getDescription();
//======================================================================
	protected AbstractTask(double multiplier)
	{
		tasktime = BaseGrowthTime;
		tasktime *= multiplier;

		resetTimestamp();
	}
//======================================================================
	protected void resetTimestamp()
	{
		timestamp = System.currentTimeMillis();
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
	public static VolaTile getVolaTile(int tilex, int tiley){
		VolaTile vtile = null;
		try{
			Zone zone = Zones.getZone(tilex, tiley, true);
			vtile = zone.getTileOrNull(tilex, tiley);
		}
		catch(NoSuchZoneException nsze){ /* oops */ }
		return vtile;
	}
}

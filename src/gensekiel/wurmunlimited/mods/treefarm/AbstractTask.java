package gensekiel.wurmunlimited.mods.treefarm;

import java.io.Serializable;
import java.util.Random;

import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.Zones;

public abstract class AbstractTask implements Serializable
{
	private static final long serialVersionUID = 5L;
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
	private static Random rnd = new Random();
	protected double fail_chance = 1.0;
	public double getFailChance(){ return fail_chance; }
	public final boolean shouldFail(){ return rnd.nextDouble() < fail_chance; }
	protected long random_time = 0;
	public double getRandomTime(){ return random_time; }
	protected double random_factor = 1.0;
	public double getRandomFactor(){ return random_factor; }
	public final void randomizeTaskTime(){ random_time = (long)(tasktime * rnd.nextDouble() * random_factor); }
//----------------------------------------------------------------------
	protected long tasktime;
	public final long getTaskTime(){ return tasktime + random_time; }
//======================================================================
	public abstract boolean performCheck();
	public abstract boolean performTask();
	public abstract long getTaskKey();
	public abstract String getDescription();
//======================================================================
	protected AbstractTask()
	{
		tasktime = BaseGrowthTime;
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

package gensekiel.wurmunlimited.mods.treefarm;

import java.io.Serializable;

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

		timestamp = System.currentTimeMillis();
	}
//======================================================================
}

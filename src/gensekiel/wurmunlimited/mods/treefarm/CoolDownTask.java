package gensekiel.wurmunlimited.mods.treefarm;

public class CoolDownTask extends AbstractTask
{
	private static final long serialVersionUID = 3L;
//======================================================================
	private long key;
	private static double coolDownMultiplier = 0.25;
	public static void setCoolDownMultiplier(double d){ coolDownMultiplier = d; }
	public static double getCoolDownMultiplier(){ return coolDownMultiplier; }
//======================================================================
	public CoolDownTask(long k, long time)
	{
		super(1.0);
		key = k;
		tasktime = (long)(time * coolDownMultiplier);
	}
//======================================================================
	@Override
	public boolean performCheck(){ return false; }
	@Override
	public boolean performTask(){ return true; }
	@Override
	public long getTaskKey(){ return key; }
	@Override
	public String getDescription(){ return "You need to give it a little time."; }
//======================================================================
}

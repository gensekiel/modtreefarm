package gensekiel.wurmunlimited.mods.treefarm;

import java.io.Serializable;

public abstract class AbstractTask implements Serializable{
	private static final long serialVersionUID = 2L;
//======================================================================
	protected static boolean checkForWUPoll = true;
//======================================================================
	public static void setCheckForWUPoll(boolean b){ checkForWUPoll = b; }
//======================================================================
	public static boolean getCheckForWUPoll(){ return checkForWUPoll; }
//======================================================================
	public abstract boolean performCheck(TreeTile treetile, int tile);
	public abstract boolean performTask(TreeTile treetile);
	public abstract String getDescription();
	public abstract double getGrowthMultiplier();
//======================================================================
}

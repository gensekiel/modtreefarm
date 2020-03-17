package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.items.Item;

public abstract class PlanterTask extends ItemTask
{
	private static final long serialVersionUID = 5L;
//======================================================================
	protected static int planter_id = 1162;
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//----------------------------------------------------------------------
	private static double ChanceMultiplier = 0.0;
	public static void setChanceMultiplier(double d){ ChanceMultiplier = d; }
	public static double getChanceMultiplier(){ return ChanceMultiplier; }
//----------------------------------------------------------------------
	private static double RndMultiplier = 0.0;
	public static void setRndMultiplier(double d){ RndMultiplier = d; }
	public static double getRndMultiplier(){ return RndMultiplier; }
//======================================================================
	public PlanterTask(Item item)
	{
		super(item);
		tasktime *= growthMultiplier;
		fail_chance *= ChanceMultiplier;
		random_factor *= RndMultiplier;
	}
//======================================================================
	public static boolean checkItemType(Item item)
	{
		if(planter_id == item.getTemplateId()) return true;
		else return false;
	}
//======================================================================
	public static int getPlanterAge(Item item)
	{
		return item.getAuxData() & 0x7F;
	}
//======================================================================
}

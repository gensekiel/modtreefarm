package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.items.Item;

public class PlanterAgeTask extends PlanterTask
{
	private static final long serialVersionUID = 5L;
//======================================================================
	private int age;
//======================================================================
	private static int planterAgeStep = 1;
	public static void setPlanterAgeStep(int i){ planterAgeStep = i; }
	public static int getPlanterAgeStep(){ return planterAgeStep; }
//----------------------------------------------------------------------
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//----------------------------------------------------------------------
	protected static byte ageLimit = 1;
	public static void setAgeLimit(byte b){ ageLimit = b; }
	public static byte getAgeLimit(){ return ageLimit; }
//======================================================================
	public PlanterAgeTask(Item item, double multiplier, double chance, double rnd)
	{
		super(item);
		age = getPlanterAge(item);
		tasktime *= growthMultiplier * multiplier;
		fail_chance *= chance;
		random_factor *= rnd;
	}
//======================================================================
	public static boolean canGrow(Item item)
	{
		int age = getPlanterAge(item);
		if(age < 127) return true;
		else return false;
	}
//======================================================================
	@Override
	public boolean performCheck()
	{
		Item item = getItem();
		if(item == null) return true;

		if(!checkItemType(item)) return true;

		int newage = getPlanterAge(item);

		if(checkForWUPoll && newage > ageLimit){
			if(newage != age) return true;
		}

		if(age < ageLimit && newage == ageLimit) return true;

		if(!canGrow(item)) return true;

		return false;
	}
//======================================================================
	@Override
	public boolean performTask()
	{
		Item item = getItem();
		if(item != null){
			byte aux = item.getAuxData();
			item.setAuxData((byte)( (aux & 0x80) | ((aux & 0x7F) + planterAgeStep) ));

			if(getPlanterAge(item) < ageLimit){
				return false;
			}
		}
		return true;
	}
//======================================================================
	@Override public String getDescription(){ return getDescription("watered"); }
//======================================================================
}

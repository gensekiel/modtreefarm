package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.items.Item;

public class PlanterAgeTask extends ItemTask
{
	private static final long serialVersionUID = 3L;
//======================================================================
	private static int planterAgeStep = 1;
	public static void setPlanterAgeStep(int i){ planterAgeStep = i; }
	public static int getPlanterAgeStep(){ return planterAgeStep; }
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//======================================================================
	public PlanterAgeTask(Item item, double multiplier)
	{
		super(item, multiplier);
	}
//======================================================================
	public static boolean canGrow(Item item)
	{
		int age = PlanterTask.getPlanterAge(item);
		if(age < 127) return true;
		else return false;
	}
//======================================================================
	@Override
	public boolean performCheck()
	{
		Item item = getItem();
		if(item == null) return true;

		if(!PlanterTask.checkItemType(item)) return true;
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
		}
		return true;
	}
//======================================================================
	@Override
	public String getDescription()
	{
		Item item = getItem();
		if(item == null) return "It has been watered recently.";
		return "This " + item.getName() + " has been watered recently.";
	}
//======================================================================
}

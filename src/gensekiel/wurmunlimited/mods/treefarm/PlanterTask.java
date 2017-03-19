package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.items.Item;

public class PlanterTask extends ItemTask
{
	private static final long serialVersionUID = 3L;
//======================================================================
	private static int planter_id = 1162;
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//======================================================================
	public PlanterTask(Item item, double multiplier)
	{
		super(item, multiplier);
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
	public static boolean isFertilizable(Item item)
	{
		int age = getPlanterAge(item);
		if(age > 5 && age < 95) return true;
		else return false;
	}
//======================================================================
	public static boolean isPickable(Item item)
	{
		return ((item.getAuxData() & 0x80) != 0);
	}
//======================================================================
	@Override
	public boolean performCheck()
	{
		Item item = getItem();
		if(item == null) return true;

		if(!checkItemType(item)) return true;
		if(!isFertilizable(item)) return true;

		return false;
	}
//======================================================================
	@Override
	public boolean performTask()
	{
		Item item = getItem();
		if(item != null){
			item.setAuxData((byte)(item.getAuxData() | 0x80));
		}
		return true;
	}
//======================================================================
}

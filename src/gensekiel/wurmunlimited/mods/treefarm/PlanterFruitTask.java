package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.items.Item;

public class PlanterFruitTask extends PlanterTask
{
	private static final long serialVersionUID = 5L;
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//======================================================================
	public PlanterFruitTask(Item item, double multiplier, double chance, double rnd)
	{
		super(item);
		tasktime *= growthMultiplier * multiplier;
		fail_chance *= chance;
		random_factor *= rnd;
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
	@Override public String getDescription(){ return getDescription("fertilized"); }
//======================================================================
}

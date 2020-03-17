package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.server.items.Item;

public class TrellisFruitTask extends TrellisTask
{
	private static final long serialVersionUID = 5L;
//======================================================================
	private static int allowed_ids[] = {920, 1018, 1274};
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//======================================================================
	public TrellisFruitTask(Item item, double multiplier, double chance, double rnd)
	{
		super(item);
		tasktime *= growthMultiplier * multiplier;
		fail_chance *= chance;
		random_factor *= rnd;
	}
//======================================================================
	public static boolean checkItemType(Item item)
	{
		return checkItemType(allowed_ids, item);
	}
//======================================================================
	public static boolean isFertilizable(Item item)
	{
		int age = item.getLeftAuxData();
		if(age > FoliageAge.YOUNG_FOUR.getAgeId() && age < FoliageAge.OVERAGED.getAgeId()) return true;
		return false;
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
			item.setHarvestable(true);
		}
		return true;
	}
//======================================================================
	@Override public String getDescription(){ return getDescription("fertilized"); }
//======================================================================
}

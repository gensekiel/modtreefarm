package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.server.items.Item;

public class TrellisAgeTask extends ItemTask
{
	private static final long serialVersionUID = 3L;
//======================================================================
	private static int allowed_ids[] = {919, 920, 1018, 1274};
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//======================================================================
	public TrellisAgeTask(Item item, double multiplier)
	{
		super(item, multiplier);
		tasktime *= growthMultiplier;
	}
//======================================================================
	public static boolean checkItemType(Item item)
	{
		return checkItemType(allowed_ids, item);
	}
//======================================================================
	public static boolean canGrow(Item item)
	{
		int age = item.getLeftAuxData();
		if(age < FoliageAge.SHRIVELLED.getAgeId()) return true;
		else return false;
	}
//======================================================================
	@Override
	public boolean performCheck()
	{
		Item item = getItem();
		if(item == null) return true;

		if(!checkItemType(item)) return true;
		if(!canGrow(item)) return true;

		return false;
	}
//======================================================================
	@Override
	public boolean performTask()
	{
		Item item = getItem();
		if(item != null){
			item.setLeftAuxData(item.getLeftAuxData() + 1);
			item.updateName();
		}
		return true;
	}
//======================================================================
	@Override public String getDescription(){ return getDescription("watered"); }
//======================================================================
}

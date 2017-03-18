package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.items.Item;

public class ItemTask extends AbstractTask
{
	private static final long serialVersionUID = 3L;
//======================================================================
	private long id;
	private static int allowed_ids[] = {920, 1018, 1274};
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//======================================================================
	public ItemTask(Item item, double multiplier)
	{
		super(multiplier);

		tasktime *= growthMultiplier;
		
		id = item.getWurmId();
	}
//======================================================================
	public static boolean checkItemType(Item item)
	{
		for(int id : allowed_ids) if(id == item.getTemplateId()) return true;
		return false;
	}
//======================================================================
	@Override
	public boolean performCheck()
	{
		Item item = getItem();
		if(item == null) return true;

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
	@Override
	public long getTaskKey()
	{
		return id;
	}
//======================================================================
	@Override
	public String getDescription()
	{
		Item item = getItem();
		if(item == null) return "It has been fertilized recently.";
		return "This " + item.getName() + " has been fertilized recently.";
	}
//======================================================================
	public Item getItem()
	{
		try {
			return Items.getItem(id);
		}catch(NoSuchItemException e){
			return null;
		}
	}
//======================================================================
}

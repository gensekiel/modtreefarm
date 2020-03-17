package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.items.Item;

public abstract class ItemTask extends AbstractTask
{
	private static final long serialVersionUID = 5L;
//======================================================================
	protected long id;
//======================================================================
	public ItemTask(Item item)
	{
		super();
		id = item.getWurmId();
	}
//======================================================================
	public static boolean checkItemType(int ids[], Item item)
	{
		for(int id : ids) if(id == item.getTemplateId()) return true;
		return false;
	}
//======================================================================
	@Override
	public long getTaskKey()
	{
		return id;
	}
//======================================================================
	public String getDescription(String word)
	{
		Item item = getItem();
		if(item == null) return "It has been " + word + " recently.";
		return "This " + item.getName() + " has been " + word + " recently.";
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

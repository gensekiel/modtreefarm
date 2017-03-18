package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;

public class NoSproutAction extends DebugAction
{
//======================================================================
	protected NoSproutAction(String menu){ super(menu); }
//======================================================================
	@Override
	protected void action(Creature performer)
	{
		for(Item i : performer.getInventory().getAllItems(false)){
			if(i.getTemplateId() == 266) Items.destroyItem(i.getWurmId());
		}
	}
//======================================================================
}

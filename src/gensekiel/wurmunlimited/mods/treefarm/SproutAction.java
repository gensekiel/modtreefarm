package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.shared.constants.ItemMaterials;

public class SproutAction extends DebugAction
{
//======================================================================
	protected SproutAction(String menu){ super(menu); }
//======================================================================
	@Override
	protected void action(Creature performer)
	{
		byte materials[] = {
			ItemMaterials.MATERIAL_WOOD_BIRCH,
			ItemMaterials.MATERIAL_WOOD_PINE,
			ItemMaterials.MATERIAL_WOOD_OAK,
			ItemMaterials.MATERIAL_WOOD_CEDAR,
			ItemMaterials.MATERIAL_WOOD_WILLOW,
			ItemMaterials.MATERIAL_WOOD_MAPLE,
			ItemMaterials.MATERIAL_WOOD_APPLE,
			ItemMaterials.MATERIAL_WOOD_LEMON,
			ItemMaterials.MATERIAL_WOOD_OLIVE,
			ItemMaterials.MATERIAL_WOOD_CHERRY,
			ItemMaterials.MATERIAL_WOOD_LAVENDER,
			ItemMaterials.MATERIAL_WOOD_ROSE,
			ItemMaterials.MATERIAL_WOOD_THORN,
			ItemMaterials.MATERIAL_WOOD_GRAPE,
			ItemMaterials.MATERIAL_WOOD_CAMELLIA,
			ItemMaterials.MATERIAL_WOOD_OLEANDER,
			ItemMaterials.MATERIAL_WOOD_CHESTNUT,
			ItemMaterials.MATERIAL_WOOD_WALNUT,
			ItemMaterials.MATERIAL_WOOD_FIR,
			ItemMaterials.MATERIAL_WOOD_LINDEN,
//			ItemMaterials.MATERIAL_WOOD_IVY,
			ItemMaterials.MATERIAL_WOOD_HAZELNUT,
			ItemMaterials.MATERIAL_WOOD_ORANGE,
			ItemMaterials.MATERIAL_WOOD_BLUEBERRY,
			ItemMaterials.MATERIAL_WOOD_RASPBERRY
		};

		for(byte m : materials){
			try {
				Item sprout = ItemFactory.createItem(266, 99.0f, m, (byte)0, null);
				performer.getInventory().insertItem(sprout);
			}catch(FailedException | NoSuchTemplateException e){}
		}
	}
//======================================================================
}

package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;

public class ForageBotanizeAction extends TileAction
{
//======================================================================
	public ForageBotanizeAction(){ this("Fertilize ground"); }
//======================================================================
	public ForageBotanizeAction(String s)
	{
		super(s, AbstractAction.ActionFlavor.FERTILIZE_ACTION);

		cost = 100;
		time = 50;
		item = ItemList.ash;
		skill = 10045;
	}
//======================================================================
	@Override
	protected boolean checkTileConditions(Creature performer, int rawtile, int tilex, int tiley)
	{
		Tiles.Tile ttile = TileTask.getTile(rawtile);
		if(ttile == null) return true;
		String tilename = TileTask.getTileName(rawtile);

		boolean fert_required = true;
		boolean foragable   = Server.isForagable  (tilex, tiley);
		boolean botanizable = Server.isBotanizable(tilex, tiley);
		boolean canforage   = ttile.canForage();
		boolean canbotanize = ttile.canBotanize();
		if( canforage && !canbotanize && foragable  ) fert_required = false;
		if(!canforage &&  canbotanize && botanizable) fert_required = false;
		if( canforage &&  canbotanize && botanizable && foragable) fert_required = false;

		if(!fert_required){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + "'s ground does not require fertilization.", (byte)1);
			return true;
		}

		return false;
	}
//======================================================================
	@Override
	public void performTileAction(int rawtile, int tilex, int tiley, double multiplier)
	{
		TaskPoller.addTask(new ForageBotanizeTask(rawtile, tilex, tiley, multiplier));
	}
//======================================================================
	@Override
	protected boolean checkTileType(int rawtile)
	{
		return ForageBotanizeTask.checkTileType(rawtile);
	}
//======================================================================
}

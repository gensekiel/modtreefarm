package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;

public class ForageBotanizeAction extends TileAction 
{
//======================================================================
	public ForageBotanizeAction()
	{
		this("Fertilize");
	}
//======================================================================
	public ForageBotanizeAction(String s)
	{
		super(s, "fertilize", "fertilizing", "Fertilizing");

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
		
		if( ttile.canForage() && !ttile.canBotanize() && Server.isForagable  (tilex, tiley)) 
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " does not require fertilization.", (byte)1);
		if(!ttile.canForage() &&  ttile.canBotanize() && Server.isBotanizable(tilex, tiley))
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " does not require fertilization.", (byte)1);
		if( ttile.canForage() &&  ttile.canBotanize() && Server.isBotanizable(tilex, tiley) && Server.isForagable(tilex, tiley))
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " does not require fertilization.", (byte)1);

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

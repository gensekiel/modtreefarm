package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemList;

public class GrassGrowAction extends TileAction
{
	private static boolean allowFlowers = true;
	public static void setAllowFlowers(boolean b){ allowFlowers = b; }
	public static boolean getAllowFlowers(){ return allowFlowers; }
//======================================================================
	public GrassGrowAction(){ this("Water ground"); }
//======================================================================
	public GrassGrowAction(String s)
	{
		this(s, AbstractAction.ActionFlavor.WATER_ACTION);
		item = ItemList.water;
	}
//======================================================================
	protected GrassGrowAction(String s, AbstractAction.ActionFlavor f)
	{
		super(s, f);

		cost = 5000;
		time = 30;
		skill = 10045;
	}
//======================================================================
	@Override
	protected byte getAge(byte tiledata)
	{
		return GrassGrowTask.getGrowthStage(tiledata);
	}
//======================================================================
	@Override
	protected byte getMaxAge()
	{
		return 3;
	}
//======================================================================
	public static boolean isPureGrassTile(int rawtile)
	{
		return (Tiles.decodeType(rawtile) == Tiles.Tile.TILE_GRASS.id);
	}
//======================================================================
	@Override
	protected boolean checkTileConditions(Creature performer, int rawtile, int tilex, int tiley)
	{
		byte data = Tiles.decodeData(rawtile);
		String tilename = TileTask.getTileName(rawtile);
		GrassData.GrowthStage gs = GrassData.GrowthStage.decodeTileData(data);

		if(gs.isMax() && (!allowFlowers || !isPureGrassTile(rawtile) || FlowerGrowTask.containsFlowers(rawtile))){
			performer.getCommunicator().sendNormalServerMessage("This " + tilename + " has reached its maximum height.", (byte)1);
			return true;
		}
		return false;
	}
//======================================================================
	@Override
	public void performTileAction(int rawtile, int tilex, int tiley, double multiplier, double chance, double rnd, boolean onSurface)
	{
		GrassData.GrowthStage gs = GrassData.GrowthStage.decodeTileData(Tiles.decodeData(rawtile));
		if(gs.isMax()){
			TaskPoller.addTask(new FlowerGrowTask(rawtile, tilex, tiley, multiplier, chance, rnd, onSurface));
		}else{
			TaskPoller.addTask(new GrassGrowTask(rawtile, tilex, tiley, multiplier, chance, rnd, onSurface));
		}
	}
//======================================================================
	@Override
	protected boolean checkTileType(int rawtile)
	{
		return (GrassGrowTask.checkTileType(rawtile) && isPureGrassTile(rawtile));
	}
//======================================================================
}

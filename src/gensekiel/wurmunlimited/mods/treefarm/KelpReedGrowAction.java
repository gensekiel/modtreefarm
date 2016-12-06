package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.items.ItemList;
import com.wurmonline.mesh.Tiles;

public class KelpReedGrowAction extends GrassGrowAction
{
//======================================================================
	private static boolean allowKelp = true;
	private static boolean allowReed = true;
	public static void setAllowKelp(boolean b){ allowKelp = b; }
	public static void setAllowReed(boolean b){ allowReed = b; }
	public static boolean getAllowKelp(){ return allowKelp; }
	public static boolean getAllowReed(){ return allowReed; }
//======================================================================
	public KelpReedGrowAction()
	{
		this("Fertilize");
	}
//======================================================================
	public KelpReedGrowAction(String s)
	{
		super(s, "fertilize", "fertilizing", "Fertilizing");
		item = ItemList.ash;
	}
//======================================================================
	@Override
	protected boolean checkTileType(int rawtile)
	{
		byte type = Tiles.decodeType(rawtile);
		return (     GrassGrowTask.checkTileType(rawtile) 
		         && (    (allowReed && type == Tiles.Tile.TILE_REED.id)
		              || (allowKelp && type == Tiles.Tile.TILE_KELP.id) ));
	}
//======================================================================
}

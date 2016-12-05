package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;

public abstract class TileTask extends AbstractTask
{
	private static final long serialVersionUID = 3L;
//======================================================================
	protected int tile;
	public final int getTile(){ return tile; }
	public final void setTile(int i){ tile = i; }
//----------------------------------------------------------------------
	protected int x;
	public final int getX(){ return x; }
//----------------------------------------------------------------------
	protected int y;
	public final int getY(){ return y; }
//----------------------------------------------------------------------
	protected static double GrowthMultiplierNormal = 1.0;
	protected static double GrowthMultiplierEnchanted = 1.0;
	protected static double GrowthMultiplierMycelium = 1.0;
	public static void setGrowthMultiplierNormal(double d){ GrowthMultiplierNormal = d; }
	public static void setGrowthMultiplierEnchanted(double d){ GrowthMultiplierEnchanted = d; }
	public static void setGrowthMultiplierMycelium(double d){ GrowthMultiplierMycelium = d; }
	public static double getGrowthMultiplierNormal(){ return GrowthMultiplierNormal; }
	public static double getGrowthMultiplierEnchanted(){ return GrowthMultiplierEnchanted; }
	public static double getGrowthMultiplierMycelium(){ return GrowthMultiplierMycelium; }
//======================================================================
	protected TileTask(int rawtile, int tilex, int tiley, double multiplier)
	{
		super(multiplier);
		
		tile = rawtile;
		x = tilex;
		y = tiley;

		Tiles.Tile tiletype = getTile(tile);
		
		     if(tiletype.isNormalTree())    tasktime *= GrowthMultiplierNormal;
		else if(tiletype.isEnchantedTree()) tasktime *= GrowthMultiplierEnchanted;
		else if(tiletype.isMyceliumTree())  tasktime *= GrowthMultiplierMycelium;
	}
//======================================================================
	public long getTaskKey()
	{
		return getTaskKey(x, y);
	}
//======================================================================
	public static boolean compareTileTypes(int rawtile1, int rawtile2)
	{
		return ((rawtile1 & 0xFF000000) == (rawtile2 & 0xFF000000));
	}
//======================================================================
	public static long getTaskKey(int tx, int ty)
	{
		return Long.valueOf(Tiles.getTileId(tx, ty, 0, true));
	}
//======================================================================
	public byte getType()
	{
		return Tiles.decodeType(tile);
	}
//======================================================================
	public byte getData()
	{
		return Tiles.decodeData(tile);
	}
//======================================================================
	public static Tiles.Tile getTile(int x, int y)
	{
		return getTile(Server.surfaceMesh.getTile(x, y));
	}
//======================================================================
	public static Tiles.Tile getTile(int rawtile)
	{
		return Tiles.getTile(Tiles.decodeType(rawtile));
	}
//======================================================================
	public static String getTileName(int rawtile)
	{
		Tiles.Tile tt = getTile(rawtile);
		return tt.getTileName(Tiles.decodeData(rawtile));
	}
//======================================================================
}

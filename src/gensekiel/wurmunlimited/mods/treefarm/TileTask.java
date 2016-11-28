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
//======================================================================
	protected TileTask(int rawtile, int tilex, int tiley, double multiplier)
	{
		super(multiplier);
		
		tile = rawtile;
		x = tilex;
		y = tiley;
	}
//======================================================================
	public long getTaskKey()
	{
		return getTaskKey(x, y);
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

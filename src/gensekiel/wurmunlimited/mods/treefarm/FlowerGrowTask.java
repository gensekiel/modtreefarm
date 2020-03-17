package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;

public class FlowerGrowTask extends TileTask
{
	private static final long serialVersionUID = 5L;
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//----------------------------------------------------------------------
	private static double ChanceMultiplier = 0.0;
	public static void setChanceMultiplier(double d){ ChanceMultiplier = d; }
	public static double getChanceMultiplier(){ return ChanceMultiplier; }
//----------------------------------------------------------------------
	private static double RndMultiplier = 0.0;
	public static void setRndMultiplier(double d){ RndMultiplier = d; }
	public static double getRndMultiplier(){ return RndMultiplier; }
//======================================================================
	public FlowerGrowTask(int rawtile, int tilex, int tiley, double multiplier, double chance, double rnd, boolean onSurface)
	{
		super(rawtile, tilex, tiley, onSurface);
		tasktime *= growthMultiplier * multiplier;
		fail_chance *= ChanceMultiplier * chance;
		random_factor *= RndMultiplier * rnd;
	}
//======================================================================
	@Override
	public String getDescription()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);
		return "This " + getTileName(rawtile) + " has been watered recently.";
	}
//======================================================================
	public static boolean checkTileType(int rawtile)
	{
		return GrassGrowAction.isPureGrassTile(rawtile);
	}
//======================================================================
	public static boolean containsFlowers(int rawtile)
	{
		GrassData.FlowerType ft = GrassData.FlowerType.decodeTileData(Tiles.decodeData(rawtile));
		return (ft != GrassData.FlowerType.NONE);
	}
//======================================================================
	@Override
	public boolean performCheck()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);
		Tiles.Tile ttile = getTile(rawtile);

		// Generic check
		if(ttile == null) return true;

		if(!checkTileType(rawtile)) return true;

		if(!TileTask.compareTileTypes(tile, rawtile)) return true;

		if(checkForWUPoll){
			if((tile & 0x000F0000) != (rawtile & 0x000F0000))
				return true;
		}

		if(containsFlowers(rawtile)) return true;

		return false;
	}
//======================================================================
	@Override
	public boolean performTask()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);
		forceFlowerGrowth(rawtile, x, y, getType(), getData());
		return true;
	}
//======================================================================
	public static void forceFlowerGrowth(int rawtile, int tilex, int tiley, byte type, byte data)
	{
		GrassData.GrowthStage gs = GrassData.GrowthStage.decodeTileData(data);
		GrassData.FlowerType ft = GrassData.FlowerType.decodeTileData(data);
		ft = GrassData.FlowerType.fromInt(Server.rand.nextInt(7) + 1);
		byte newdata = GrassData.encodeGrassTileData(gs, ft);
		Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(rawtile), type, newdata));
		Server.modifyFlagsByTileType(tilex, tiley, type);
		Players.getInstance().sendChangedTile(tilex, tiley, true, false);
	}
}

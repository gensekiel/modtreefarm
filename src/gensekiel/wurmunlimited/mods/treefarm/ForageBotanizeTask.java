package gensekiel.wurmunlimited.mods.treefarm;

import java.lang.reflect.Method;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.TilePoller;
import com.wurmonline.server.zones.VolaTile;

public class ForageBotanizeTask extends GrassTileTask
{
	private static final long serialVersionUID = 3L;
//======================================================================
	private static double growthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ growthMultiplier = d; }
	public static double getGrowthMultiplier(){ return growthMultiplier; }
//----------------------------------------------------------------------
	protected static boolean useOriginalGrowthFunction = false;
	public static void setUseOriginalGrowthFunction(boolean b){ useOriginalGrowthFunction = b; }
	public static boolean getUseOriginalGrowthFunction(){ return useOriginalGrowthFunction; }
//======================================================================
	protected ForageBotanizeTask(int rawtile, int tilex, int tiley, double multiplier)
	{
		super(rawtile, tilex, tiley, multiplier);
		tasktime *= growthMultiplier;
	}
//======================================================================
	public static boolean checkTileType(int rawtile)
	{
		return (    getTile(rawtile).canForage()
		         || getTile(rawtile).canBotanize() );
	}
//======================================================================
	@Override
	public boolean performCheck() {
		int rawtile = Server.surfaceMesh.getTile(x, y);
		Tiles.Tile ttile = getTile(rawtile);
		
		// Generic check
		if(ttile == null) return true;
		
		if(!checkTileType(rawtile)) return true;

		if(!TileTask.compareTileTypes(tile, rawtile)) return true;

		if(checkForWUPoll){
			if( ttile.canForage() && !ttile.canBotanize() && Server.isForagable  (x, y)) return true; 
			if(!ttile.canForage() &&  ttile.canBotanize() && Server.isBotanizable(x, y)) return true; 
			if( ttile.canForage() &&  ttile.canBotanize() && Server.isBotanizable(x, y) && Server.isForagable(x, y)) return true; 
		}
		
		return false;
	}
//======================================================================
	@Override
	public boolean performTask() {
		if(useOriginalGrowthFunction)
			callSeedGrowthWrapper(tile, x, y);
		else
			forceSeedGrowth(tile, x, y);
		return true;
	}
//======================================================================
	@Override
	public String getDescription(){
		int rawtile = Server.surfaceMesh.getTile(x, y);
		return "This " + getTileName(rawtile) + " has been fertilized recently.";
	}
//======================================================================
	private void callSeedGrowthWrapper(int rawtile, int tilex, int tiley)
	{
		if(TaskPoller.getProtectTasks()) TaskPoller.ignoreNextMatch();
		try{
			Method method = TilePoller.class.getMethod("wrap_checkForSeedGrowth", int.class, int.class, int.class);
			method.invoke(null, rawtile, tilex, tiley);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}
//======================================================================
	public void forceSeedGrowth(int rawtile, int tilex, int tiley)
	{
		Tiles.Tile tt = getTile(rawtile);
		VolaTile vtile = getVolaTile(tilex, tiley);
		if(vtile == null) return;
		if(vtile.getStructure() != null) return;
		if(vtile.getFences().length > 0) return;

		boolean addforage = false;
		boolean addbotanize = false;
		if(tt.canForage  () && !Server.isForagable  (tilex, tiley)) addforage   = true;
		if(tt.canBotanize() && !Server.isBotanizable(tilex, tiley)) addbotanize = true;
		if(addforage || addbotanize) TilePoller.setGrassHasSeeds(tilex, tiley, addforage, addbotanize);
	}
//======================================================================
}

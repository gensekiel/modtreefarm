package gensekiel.wurmunlimited.mods.treefarm;

import java.lang.reflect.Method;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Crops;
import com.wurmonline.server.zones.TilePoller;

public class FarmGrowTask extends TileTask
{
	private static final long serialVersionUID = 5L;
//======================================================================
	private static double GrowthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ GrowthMultiplier = d; }
	public static double getGrowthMultiplier(){ return GrowthMultiplier; }
//----------------------------------------------------------------------
	protected static boolean useOriginalGrowthFunction = false;
	public static void setUseOriginalGrowthFunction(boolean b){ useOriginalGrowthFunction = b; }
	public static boolean getUseOriginalGrowthFunction(){ return useOriginalGrowthFunction; }
//----------------------------------------------------------------------
	protected static byte ageLimit = 1;
	public static void setAgeLimit(byte b){ ageLimit = b; }
	public static byte getAgeLimit(){ return ageLimit; }
//----------------------------------------------------------------------
	private static double[] GrowthMultiplierAge = {1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6};
	public static void setGrowthMultiplierAge(int age, double d){ GrowthMultiplierAge[age] = d; }
	public static double getGrowthMultiplierAge(int age){ return GrowthMultiplierAge[age]; }
//----------------------------------------------------------------------
	private static boolean retainState = false;
	public static void setStateLock(boolean b){ retainState = b; }
	public static boolean getStateLock(){ return retainState; }
//----------------------------------------------------------------------
	private static double ChanceMultiplier = 0.0;
	public static void setChanceMultiplier(double d){ ChanceMultiplier = d; }
	public static double getChanceMultiplier(){ return ChanceMultiplier; }
//----------------------------------------------------------------------
	private static double RndMultiplier = 0.0;
	public static void setRndMultiplier(double d){ RndMultiplier = d; }
	public static double getRndMultiplier(){ return RndMultiplier; }
//======================================================================
	public FarmGrowTask(int rawtile, int tilex, int tiley, double multiplier, double chance, double rnd, boolean onSurface)
	{
		super(rawtile, tilex, tiley, onSurface);

		tasktime *= GrowthMultiplier * multiplier;
		fail_chance *= ChanceMultiplier * chance;
		random_factor *= RndMultiplier * rnd;

		byte tage = getAge();
		if(tage < getMaxAge()) tasktime *= GrowthMultiplierAge[tage];
	}
//======================================================================
	public byte getAge()
	{
		return getAge(getData());
	}
//======================================================================
	public static byte getAge(byte data)
	{
		return (byte)(data >> 4 & 0x7);
	}
//======================================================================
	public static byte getMaxAge()
	{
		return 7;
	}
//======================================================================
	public static boolean growsInWater(int rawtile)
	{
		int crop = Crops.getCropNumber(getType(rawtile), getData(rawtile));
		return (crop == 11 || crop == 12);
	}
//======================================================================
	@Override
	public String getDescription()
	{
		int rawtile = Server.surfaceMesh.getTile(x, y);
		int crop = Crops.getCropNumber(getType(), getData());
		if(growsInWater(rawtile)){
			return "This " + getTileName(rawtile) + " (" + Crops.getCropName(crop) + ") has been fertilized recently.";
		}else{
			return "This " + getTileName(rawtile) + " (" + Crops.getCropName(crop) + ") has been watered recently.";
		}
	}
//======================================================================
	public static boolean checkTileType(int rawtile)
	{
		return (    getTile(rawtile).id == Tiles.Tile.TILE_FIELD.id
		         || getTile(rawtile).id == Tiles.Tile.TILE_FIELD2.id );
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

		byte age = getAge(getData(rawtile));

		if(checkForWUPoll && age >= ageLimit){ // Check age
			if((tile & 0x00700000) != (rawtile & 0x00700000))
				return true;
		}

		if(getAge(getData(tile)) < ageLimit && age == ageLimit) return true;

		if(age >= getMaxAge()) return true;

		return false;
	}
//======================================================================
	@Override
	public boolean performTask()
	{
		int rawtile = onSurface ? Server.surfaceMesh.getTile(x, y) : Server.caveMesh.getTile(x, y);

		if(useOriginalGrowthFunction)
			callFarmGrowthWrapper(rawtile);
		else
			forceFarmGrowth(rawtile);

		if(getAge(getData(rawtile)) + 1 < ageLimit){
			return false;
		}
		return true;
	}
//======================================================================
	private void callFarmGrowthWrapper(int rawtile)
	{
		// If the protection is active, calling the growth function
		// will also check for tasks and prevent the execution on
		// tracked objects --> allow execution once.
		if(TaskPoller.getProtectTasks()) TaskPoller.ignoreNextMatch();
		try{
			Method method = TilePoller.class.getMethod("wrap_checkForFarmGrowth", int.class, int.class, int.class, byte.class, byte.class, boolean.class);
			method.invoke(null, rawtile, x, y, getType(rawtile), getData(rawtile), onSurface);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}
//======================================================================
	private void forceFarmGrowth(int rawtile)
	{
		byte type = getType(rawtile);
		byte data = getData(rawtile);
		byte age = getAge(data);
		byte new_data = 0;
		if(retainState){
			new_data = (byte)((((age+1) & 0x7) << 4) | (data & 0x8F));
		}else{
			new_data = (byte)((((age+1) & 0x7) << 4) | (data & 0x0F));
		}
		if(onSurface){
			Server.surfaceMesh.setTile(x, y, Tiles.encode(Tiles.decodeHeight(rawtile), type, new_data));
		}else{
			Server.caveMesh.setTile(x, y, Tiles.encode(Tiles.decodeHeight(rawtile), type, new_data));
		}
		Server.modifyFlagsByTileType(x, y, type);
		Players.getInstance().sendChangedTile(x, y, onSurface, false);
	}
//======================================================================
}

package gensekiel.wurmunlimited.mods.treefarm;

public abstract class GrassTileTask extends TileTask
{
	private static final long serialVersionUID = 5L;
//======================================================================
	private static double GrowthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ GrowthMultiplier = d; }
	public static double getGrowthMultiplier(){ return GrowthMultiplier; }
//----------------------------------------------------------------------
	private static double ChanceMultiplier = 0.0;
	public static void setChanceMultiplier(double d){ ChanceMultiplier = d; }
	public static double getChanceMultiplier(){ return ChanceMultiplier; }
//----------------------------------------------------------------------
	private static double RndMultiplier = 0.0;
	public static void setRndMultiplier(double d){ RndMultiplier = d; }
	public static double getRndMultiplier(){ return RndMultiplier; }
//======================================================================
	public GrassTileTask(int rawtile, int tilex, int tiley, boolean onSurface)
	{
		super(rawtile, tilex, tiley, onSurface);

		tasktime *= GrowthMultiplier;
		fail_chance *= ChanceMultiplier;
		random_factor *= RndMultiplier;
	}
//======================================================================
}

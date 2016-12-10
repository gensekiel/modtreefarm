package gensekiel.wurmunlimited.mods.treefarm;

public abstract class GrassTileTask extends TileTask
{
	private static final long serialVersionUID = 3L;
//======================================================================
	private static double GrowthMultiplier = 1.0;
	public static void setGrowthMultiplier(double d){ GrowthMultiplier = d; }
	public static double getGrowthMultiplier(){ return GrowthMultiplier; }
//======================================================================
	public GrassTileTask(int rawtile, int tilex, int tiley, double multiplier)
	{
		super(rawtile, tilex, tiley, multiplier);

		tasktime *= GrowthMultiplier;
	}
//======================================================================
}

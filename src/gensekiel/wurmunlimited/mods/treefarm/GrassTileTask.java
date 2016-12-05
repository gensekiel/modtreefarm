package gensekiel.wurmunlimited.mods.treefarm;

public abstract class GrassTileTask extends TileTask
{
	private static final long serialVersionUID = 3L;
//======================================================================
	private static double GrowthMultiplierGrass = 1.0;
	public static void setGrowthMultiplierGrass(double d){ GrowthMultiplierGrass = d; }
	public static double getGrowthMultiplierGrass(){ return GrowthMultiplierGrass; }
//======================================================================
	public GrassTileTask(int rawtile, int tilex, int tiley, double multiplier)
	{
		super(rawtile, tilex, tiley, multiplier);

		tasktime *= GrowthMultiplierGrass;
	}
//======================================================================
}

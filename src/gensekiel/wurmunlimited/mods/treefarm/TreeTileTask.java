package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.BushData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.TreeData;

public abstract class TreeTileTask extends TileTask
{
	private static final long serialVersionUID = 3L;
	// In the upper 16 bytes the tile integer encodes tile type
	// and data, data being age (4 bit), fruit (1 bit),
	// center (1 bit) and grass height (2 bit).
	// Only compare type and age.
//======================================================================
	private static double GrowthMultiplierBirch = 1.0;
	private static double GrowthMultiplierPine = 1.0;
	private static double GrowthMultiplierOak = 2.0;
	private static double GrowthMultiplierCedar = 1.0;
	private static double GrowthMultiplierWillow = 1.5;
	private static double GrowthMultiplierMaple = 1.0;
	private static double GrowthMultiplierApple = 1.0;
	private static double GrowthMultiplierLemon = 1.0;
	private static double GrowthMultiplierOlive = 1.0;
	private static double GrowthMultiplierCherry = 1.0;
	private static double GrowthMultiplierChestnut = 1.0;
	private static double GrowthMultiplierWalnut = 1.0;
	private static double GrowthMultiplierFir = 1.0;
	private static double GrowthMultiplierLinden = 1.0;
//----------------------------------------------------------------------
	private static double GrowthMultiplierTree = 1.0;
	private static double GrowthMultiplierBush = 1.0;
//----------------------------------------------------------------------
	private static double GrowthMultiplierCamellia = 1.0;
	private static double GrowthMultiplierGrape = 1.0;
	private static double GrowthMultiplierLavender = 1.0;
	private static double GrowthMultiplierOleander = 1.0;
	private static double GrowthMultiplierRose = 1.0;
	private static double GrowthMultiplierThorn = 1.0;
//----------------------------------------------------------------------
	private static double[] GrowthMultiplierAge = {1.0, 1.0, 1.1, 1.1, 1.2, 1.2, 1.3, 1.3, 1.4, 1.4, 1.5, 1.5, 1.6, 1.6, 1.7};
//======================================================================
	public TreeTileTask(int rawtile, int tilex, int tiley, double multiplier)
	{
		super(rawtile, tilex, tiley, multiplier);
		
		byte tdata = getData();
		Tiles.Tile tiletype = getTile(tile);
		
		if(tiletype.isTree()){
			tasktime *= GrowthMultiplierTree;
			
			     if(tiletype.getTreeType(tdata) == TreeData.TreeType.BIRCH)    tasktime *= GrowthMultiplierBirch;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.PINE)     tasktime *= GrowthMultiplierPine;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.OAK)      tasktime *= GrowthMultiplierOak;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.CEDAR)    tasktime *= GrowthMultiplierCedar;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.WILLOW)   tasktime *= GrowthMultiplierWillow;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.MAPLE)    tasktime *= GrowthMultiplierMaple;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.APPLE)    tasktime *= GrowthMultiplierApple;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.LEMON)    tasktime *= GrowthMultiplierLemon;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.OLIVE)    tasktime *= GrowthMultiplierOlive;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.CHERRY)   tasktime *= GrowthMultiplierCherry;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.CHESTNUT) tasktime *= GrowthMultiplierChestnut;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.WALNUT)   tasktime *= GrowthMultiplierWalnut;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.FIR)      tasktime *= GrowthMultiplierFir;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.LINDEN)   tasktime *= GrowthMultiplierLinden;
		}
		
		if(tiletype.isBush()){
			tasktime *= GrowthMultiplierBush;
			
			     if(tiletype.getBushType(tdata) == BushData.BushType.CAMELLIA) tasktime *= GrowthMultiplierCamellia;
			else if(tiletype.getBushType(tdata) == BushData.BushType.GRAPE)    tasktime *= GrowthMultiplierGrape;
			else if(tiletype.getBushType(tdata) == BushData.BushType.LAVENDER) tasktime *= GrowthMultiplierLavender;
			else if(tiletype.getBushType(tdata) == BushData.BushType.OLEANDER) tasktime *= GrowthMultiplierOleander;
			else if(tiletype.getBushType(tdata) == BushData.BushType.ROSE)     tasktime *= GrowthMultiplierRose;
			else if(tiletype.getBushType(tdata) == BushData.BushType.THORN)    tasktime *= GrowthMultiplierThorn;
		}
	
		byte tage = getAge();
		if(tage < 15) tasktime *= GrowthMultiplierAge[tage];
	}
//======================================================================
	public byte getAge()
	{
		return getAge(getData());
	}
//======================================================================
	public static byte getAge(byte data)
	{
		return (byte)(data >> 4 & 0xF);
	}
//======================================================================
	public static byte convertTile(byte type, byte data)
	{
		Tiles.Tile ttile = Tiles.getTile(type);
		if(ttile.isTree()){
			TreeData.TreeType tt = ttile.getTreeType(data);
			if(ttile.isNormalTree()) return tt.asNormalTree();
			if(ttile.isMyceliumTree()) return tt.asMyceliumTree();
			if(ttile.isEnchantedTree()) return tt.asEnchantedTree();
		}
		if(ttile.isBush()){
			BushData.BushType tt = ttile.getBushType(data);
			if(ttile.isNormalBush()) return tt.asNormalBush();
			if(ttile.isMyceliumBush()) return tt.asMyceliumBush();
			if(ttile.isEnchantedBush()) return tt.asEnchantedBush();
		}
		return type;
	}
//======================================================================
	public static void setGrowthMultiplierBirch(double d){ GrowthMultiplierBirch = d; }
	public static void setGrowthMultiplierPine(double d){ GrowthMultiplierPine = d; }
	public static void setGrowthMultiplierOak(double d){ GrowthMultiplierOak = d; }
	public static void setGrowthMultiplierCedar(double d){ GrowthMultiplierCedar = d; }
	public static void setGrowthMultiplierWillow(double d){ GrowthMultiplierWillow = d; }
	public static void setGrowthMultiplierMaple(double d){ GrowthMultiplierMaple = d; }
	public static void setGrowthMultiplierApple(double d){ GrowthMultiplierApple = d; }
	public static void setGrowthMultiplierLemon(double d){ GrowthMultiplierLemon = d; }
	public static void setGrowthMultiplierOlive(double d){ GrowthMultiplierOlive = d; }
	public static void setGrowthMultiplierCherry(double d){ GrowthMultiplierCherry = d; }
	public static void setGrowthMultiplierChestnut(double d){ GrowthMultiplierChestnut = d; }
	public static void setGrowthMultiplierWalnut(double d){ GrowthMultiplierWalnut = d; }
	public static void setGrowthMultiplierFir(double d){ GrowthMultiplierFir = d; }
	public static void setGrowthMultiplierLinden(double d){ GrowthMultiplierLinden = d; }
	public static void setGrowthMultiplierTree(double d){ GrowthMultiplierTree = d; }
	public static void setGrowthMultiplierBush(double d){ GrowthMultiplierBush = d; }
	public static void setGrowthMultiplierCamellia(double d){ GrowthMultiplierCamellia = d; }
	public static void setGrowthMultiplierGrape(double d){ GrowthMultiplierGrape = d; }
	public static void setGrowthMultiplierLavender(double d){ GrowthMultiplierLavender = d; }
	public static void setGrowthMultiplierOleander(double d){ GrowthMultiplierOleander = d; }
	public static void setGrowthMultiplierRose(double d){ GrowthMultiplierRose = d; }
	public static void setGrowthMultiplierThorn(double d){ GrowthMultiplierThorn = d; }
	public static void setGrowthMultiplierAge(int age, double d){ GrowthMultiplierAge[age] = d; }
//======================================================================
	public static double getGrowthMultiplierBirch(){ return GrowthMultiplierBirch; }
	public static double getGrowthMultiplierPine(){ return GrowthMultiplierPine; }
	public static double getGrowthMultiplierOak(){ return GrowthMultiplierOak; }
	public static double getGrowthMultiplierCedar(){ return GrowthMultiplierCedar; }
	public static double getGrowthMultiplierWillow(){ return GrowthMultiplierWillow; }
	public static double getGrowthMultiplierMaple(){ return GrowthMultiplierMaple; }
	public static double getGrowthMultiplierApple(){ return GrowthMultiplierApple; }
	public static double getGrowthMultiplierLemon(){ return GrowthMultiplierLemon; }
	public static double getGrowthMultiplierOlive(){ return GrowthMultiplierOlive; }
	public static double getGrowthMultiplierCherry(){ return GrowthMultiplierCherry; }
	public static double getGrowthMultiplierChestnut(){ return GrowthMultiplierChestnut; }
	public static double getGrowthMultiplierWalnut(){ return GrowthMultiplierWalnut; }
	public static double getGrowthMultiplierFir(){ return GrowthMultiplierFir; }
	public static double getGrowthMultiplierLinden(){ return GrowthMultiplierLinden; }
	public static double getGrowthMultiplierTree(){ return GrowthMultiplierTree; }
	public static double getGrowthMultiplierBush(){ return GrowthMultiplierBush; }
	public static double getGrowthMultiplierCamellia(){ return GrowthMultiplierCamellia; }
	public static double getGrowthMultiplierGrape(){ return GrowthMultiplierGrape; }
	public static double getGrowthMultiplierLavender(){ return GrowthMultiplierLavender; }
	public static double getGrowthMultiplierOleander(){ return GrowthMultiplierOleander; }
	public static double getGrowthMultiplierRose(){ return GrowthMultiplierRose; }
	public static double getGrowthMultiplierThorn(){ return GrowthMultiplierThorn; }
	public static double getGrowthMultiplierAge(int age){ return GrowthMultiplierAge[age]; }
//======================================================================
}

package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.mesh.BushData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.server.Server;

import java.io.Serializable;

public class TreeTile implements Serializable
{
	private static final long serialVersionUID = 2L;
	// In the upper 16 bytes the tile integer encodes tile type
	// and data, data being age (4 bit), fruit (1 bit),
	// center (1 bit) and grass height (2 bit).
	// Only compare type and age.
//======================================================================
	private static long BaseGrowthTime = 600000;
//----------------------------------------------------------------------
	private static double GrowthModifierBirch = 1.0;
	private static double GrowthModifierPine = 1.0;
	private static double GrowthModifierOak = 2.0;
	private static double GrowthModifierCedar = 1.0;
	private static double GrowthModifierWillow = 1.5;
	private static double GrowthModifierMaple = 1.0;
	private static double GrowthModifierApple = 1.0;
	private static double GrowthModifierLemon = 1.0;
	private static double GrowthModifierOlive = 1.0;
	private static double GrowthModifierCherry = 1.0;
	private static double GrowthModifierChestnut = 1.0;
	private static double GrowthModifierWalnut = 1.0;
	private static double GrowthModifierFir = 1.0;
	private static double GrowthModifierLinden = 1.0;
//----------------------------------------------------------------------
	private static double GrowthModifierNormal = 1.0;
	private static double GrowthModifierEnchanted = 1.0;
	private static double GrowthModifierMycelium = 1.0;
//----------------------------------------------------------------------
	private static double GrowthModifierTree = 1.0;
	private static double GrowthModifierBush = 1.0;
//----------------------------------------------------------------------
	private static double GrowthModifierCamellia = 1.0;
	private static double GrowthModifierGrape = 1.0;
	private static double GrowthModifierLavender = 1.0;
	private static double GrowthModifierOleander = 1.0;
	private static double GrowthModifierRose = 1.0;
	private static double GrowthModifierThorn = 1.0;
//----------------------------------------------------------------------
	private static double[] GrowthModifierAge = {1.0, 1.0, 1.1, 1.1, 1.2, 1.2, 1.3, 1.3, 1.4, 1.4, 1.5, 1.5, 1.6, 1.6, 1.7};
//======================================================================
	private AbstractTask task; 
	private int rawtile;
	private int x;
	private int y;
	private long timestamp;
	private long growthtime;
//======================================================================
	public TreeTile(int tile, int tilex, int tiley, AbstractTask t, double multiplier)
	{
		task = t;
		rawtile = tile;
		x = tilex;
		y = tiley;

		byte tdata = getData();
		byte tage = getAge();
		Tiles.Tile tiletype = getTile(rawtile);
		
		growthtime = BaseGrowthTime;
		
		growthtime *= task.getGrowthMultiplier();
		
		if(tiletype.isTree()){
			growthtime *= GrowthModifierTree;
			
			     if(tiletype.getTreeType(tdata) == TreeData.TreeType.BIRCH)    growthtime *= GrowthModifierBirch;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.PINE)     growthtime *= GrowthModifierPine;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.OAK)      growthtime *= GrowthModifierOak;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.CEDAR)    growthtime *= GrowthModifierCedar;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.WILLOW)   growthtime *= GrowthModifierWillow;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.MAPLE)    growthtime *= GrowthModifierMaple;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.APPLE)    growthtime *= GrowthModifierApple;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.LEMON)    growthtime *= GrowthModifierLemon;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.OLIVE)    growthtime *= GrowthModifierOlive;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.CHERRY)   growthtime *= GrowthModifierCherry;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.CHESTNUT) growthtime *= GrowthModifierChestnut;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.WALNUT)   growthtime *= GrowthModifierWalnut;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.FIR)      growthtime *= GrowthModifierFir;
			else if(tiletype.getTreeType(tdata) == TreeData.TreeType.LINDEN)   growthtime *= GrowthModifierLinden;
		}
		
		if(tiletype.isBush()){
			growthtime *= GrowthModifierBush;
			
			     if(tiletype.getBushType(tdata) == BushData.BushType.CAMELLIA) growthtime *= GrowthModifierCamellia;
			else if(tiletype.getBushType(tdata) == BushData.BushType.GRAPE)    growthtime *= GrowthModifierGrape;
			else if(tiletype.getBushType(tdata) == BushData.BushType.LAVENDER) growthtime *= GrowthModifierLavender;
			else if(tiletype.getBushType(tdata) == BushData.BushType.OLEANDER) growthtime *= GrowthModifierOleander;
			else if(tiletype.getBushType(tdata) == BushData.BushType.ROSE)     growthtime *= GrowthModifierRose;
			else if(tiletype.getBushType(tdata) == BushData.BushType.THORN)    growthtime *= GrowthModifierThorn;
		}
	
		if(tage < 15) growthtime *= GrowthModifierAge[tage];
	
		     if(tiletype.isNormalTree())    growthtime *= GrowthModifierNormal;
		else if(tiletype.isEnchantedTree()) growthtime *= GrowthModifierEnchanted;
		else if(tiletype.isMyceliumTree())  growthtime *= GrowthModifierMycelium;
		
		growthtime *= multiplier;
		
		timestamp = System.currentTimeMillis();
	}
//======================================================================
	public byte getType()
	{
		return Tiles.decodeType(rawtile);
	}
//======================================================================
	public byte getData()
	{
		return Tiles.decodeData(rawtile);
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
		Tiles.Tile tt = TreeTile.getTile(rawtile);
		return tt.getTileName(Tiles.decodeData(rawtile));
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
	public final AbstractTask getTask(){ return task; }
	public final int getTile(){ return rawtile; }
	public final void setTile(int i){ rawtile = i; }
	public final int getX(){ return x; }
	public final int getY(){ return y; }
	public final long getTimeStamp(){ return timestamp; }
	public final long getGrowthTime(){ return growthtime; }
	public final void setTimeStamp(long l){ timestamp = l; }
//======================================================================
	public static void setBaseGrowthTime(long l){ BaseGrowthTime = l; }
	public static void setGrowthModifierBirch(double d){ GrowthModifierBirch = d; }
	public static void setGrowthModifierPine(double d){ GrowthModifierPine = d; }
	public static void setGrowthModifierOak(double d){ GrowthModifierOak = d; }
	public static void setGrowthModifierCedar(double d){ GrowthModifierCedar = d; }
	public static void setGrowthModifierWillow(double d){ GrowthModifierWillow = d; }
	public static void setGrowthModifierMaple(double d){ GrowthModifierMaple = d; }
	public static void setGrowthModifierApple(double d){ GrowthModifierApple = d; }
	public static void setGrowthModifierLemon(double d){ GrowthModifierLemon = d; }
	public static void setGrowthModifierOlive(double d){ GrowthModifierOlive = d; }
	public static void setGrowthModifierCherry(double d){ GrowthModifierCherry = d; }
	public static void setGrowthModifierChestnut(double d){ GrowthModifierChestnut = d; }
	public static void setGrowthModifierWalnut(double d){ GrowthModifierWalnut = d; }
	public static void setGrowthModifierFir(double d){ GrowthModifierFir = d; }
	public static void setGrowthModifierLinden(double d){ GrowthModifierLinden = d; }
	public static void setGrowthModifierNormal(double d){ GrowthModifierNormal = d; }
	public static void setGrowthModifierEnchanted(double d){ GrowthModifierEnchanted = d; }
	public static void setGrowthModifierMycelium(double d){ GrowthModifierMycelium = d; }
	public static void setGrowthModifierTree(double d){ GrowthModifierTree = d; }
	public static void setGrowthModifierBush(double d){ GrowthModifierBush = d; }
	public static void setGrowthModifierCamellia(double d){ GrowthModifierCamellia = d; }
	public static void setGrowthModifierGrape(double d){ GrowthModifierGrape = d; }
	public static void setGrowthModifierLavender(double d){ GrowthModifierLavender = d; }
	public static void setGrowthModifierOleander(double d){ GrowthModifierOleander = d; }
	public static void setGrowthModifierRose(double d){ GrowthModifierRose = d; }
	public static void setGrowthModifierThorn(double d){ GrowthModifierThorn = d; }
	public static void setGrowthModifierAge(int age, double d){ GrowthModifierAge[age] = d; }
//======================================================================
	public static long getBaseGrowthTime(){ return BaseGrowthTime; }
	public static double getGrowthModifierBirch(){ return GrowthModifierBirch; }
	public static double getGrowthModifierPine(){ return GrowthModifierPine; }
	public static double getGrowthModifierOak(){ return GrowthModifierOak; }
	public static double getGrowthModifierCedar(){ return GrowthModifierCedar; }
	public static double getGrowthModifierWillow(){ return GrowthModifierWillow; }
	public static double getGrowthModifierMaple(){ return GrowthModifierMaple; }
	public static double getGrowthModifierApple(){ return GrowthModifierApple; }
	public static double getGrowthModifierLemon(){ return GrowthModifierLemon; }
	public static double getGrowthModifierOlive(){ return GrowthModifierOlive; }
	public static double getGrowthModifierCherry(){ return GrowthModifierCherry; }
	public static double getGrowthModifierChestnut(){ return GrowthModifierChestnut; }
	public static double getGrowthModifierWalnut(){ return GrowthModifierWalnut; }
	public static double getGrowthModifierFir(){ return GrowthModifierFir; }
	public static double getGrowthModifierLinden(){ return GrowthModifierLinden; }
	public static double getGrowthModifierNormal(){ return GrowthModifierNormal; }
	public static double getGrowthModifierEnchanted(){ return GrowthModifierEnchanted; }
	public static double getGrowthModifierMycelium(){ return GrowthModifierMycelium; }
	public static double getGrowthModifierTree(){ return GrowthModifierTree; }
	public static double getGrowthModifierBush(){ return GrowthModifierBush; }
	public static double getGrowthModifierCamellia(){ return GrowthModifierCamellia; }
	public static double getGrowthModifierGrape(){ return GrowthModifierGrape; }
	public static double getGrowthModifierLavender(){ return GrowthModifierLavender; }
	public static double getGrowthModifierOleander(){ return GrowthModifierOleander; }
	public static double getGrowthModifierRose(){ return GrowthModifierRose; }
	public static double getGrowthModifierThorn(){ return GrowthModifierThorn; }
	public static double getGrowthModifierAge(int age){ return GrowthModifierAge[age]; }
//======================================================================
}

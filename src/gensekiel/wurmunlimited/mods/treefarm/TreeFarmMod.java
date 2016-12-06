package gensekiel.wurmunlimited.mods.treefarm;

import java.util.Properties;

import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class TreeFarmMod implements
	WurmServerMod,
	Initable,
	PreInitable,
	Configurable,
	ServerStartedListener
{
//======================================================================
	private static WateringAction wateringaction = new WateringAction();
	private static FertilizingAction fertilizingaction = new FertilizingAction();
	private static HedgeAction hedgeaction = new HedgeAction();
	private static GrassGrowAction grassaction = new GrassGrowAction();
	private static KelpReedGrowAction kelpreedaction = new KelpReedGrowAction();
	private static ForageBotanizeAction foragebotanizeaction = new ForageBotanizeAction();
	private static boolean allowGrow = true;
	private static boolean allowFertilize = true;
	private static boolean allowHedges = true;
	private static boolean allowGrass = true;
	private static boolean allowForageBotanize = true;
	private static boolean augmentExamine = true;
	private static boolean debug = false;
	private static boolean useOriginalGrowthFunction = false;
//======================================================================
	@Override
	public void onServerStarted()
	{
		boolean kelpreed = (KelpReedGrowAction.getAllowReed() || KelpReedGrowAction.getAllowKelp());
		if(allowGrow)           wateringaction.registerAction();
		if(allowFertilize)      fertilizingaction.registerAction();
		if(allowHedges)         hedgeaction.registerAction();
		if(allowGrass)          grassaction.registerAction();
		if(kelpreed)            kelpreedaction.registerAction();
		if(allowForageBotanize) foragebotanizeaction.registerAction();
		if(augmentExamine)      ModActions.registerAction(new ExamineAction());
		
		if(debug){
			WateringAction wa2 = new WateringAction("Water (debug)");
			FertilizingAction fa2 = new FertilizingAction("Fertilize (debug)");
			HedgeAction ha2 = new HedgeAction("Water (debug)");
			GrassGrowAction ga2 = new GrassGrowAction("Water (debug)");
			KelpReedGrowAction kr2 = new KelpReedGrowAction("Fertilize (debug)");
			ForageBotanizeAction fb2 = new ForageBotanizeAction("Fertilize (debug)");
	
			wa2.setCost(0); wa2.setTime(0); wa2.setItem(0);
			fa2.setCost(0); fa2.setTime(0); fa2.setItem(0);
			ha2.setCost(0); ha2.setTime(0); ha2.setItem(0);
			ga2.setCost(0); ga2.setTime(0); ga2.setItem(0);
			kr2.setCost(0); kr2.setTime(0); kr2.setItem(0);
			fb2.setCost(0); fb2.setTime(0); fb2.setItem(0);

			wa2.registerAction();
			fa2.registerAction();
			ha2.registerAction();
			ga2.registerAction();
			kr2.registerAction();
			fb2.registerAction();
			
			HedgePollAction hpa = new HedgePollAction();
			hpa.registerAction();
		}
	}
//======================================================================
	@Override
	public void init()
	{
		if(useOriginalGrowthFunction){
			Hooks.injectTreeGrowthWrapper();
			Hooks.injectGrassGrowthWrapper();
			Hooks.injectSeedGrowthWrapper();
		}
		
		Hooks.registerListLoadingHook();
		Hooks.registerPollingHook();
		Hooks.registerListSavingHook();

		if(TaskPoller.getProtectTasks()){
			Hooks.registerTreeProtectionHook();
			Hooks.registerHedgeProtectionHook();
			Hooks.registerGrassProtectionHook();
			Hooks.registerSeedProtectionHook();
		}
	}
//======================================================================
	@Override
	public void preInit()
	{
		ModActions.init();
	}
//======================================================================
	private Integer getOption(String option, Integer default_value, Properties properties){ return Integer.valueOf(properties.getProperty(option, Integer.toString(default_value))); }
	private Double getOption(String option, Double default_value, Properties properties){ return Double.valueOf(properties.getProperty(option, Double.toString(default_value))); }
	private Long getOption(String option, Long default_value, Properties properties){ return Long.valueOf(properties.getProperty(option, Long.toString(default_value))); }
	private Boolean getOption(String option, Boolean default_value, Properties properties){ return Boolean.valueOf(properties.getProperty(option, Boolean.toString(default_value))); }
	private Byte getOption(String option, Byte default_value, Properties properties){ return Byte.valueOf(properties.getProperty(option, Byte.toString(default_value))); }
//======================================================================
	@Override
	public void configure(Properties properties)
	{
		debug = getOption("Debug", debug, properties);
		allowGrow = getOption("AllowGrow", allowGrow, properties);
		allowFertilize = getOption("AllowFertilize", allowFertilize, properties);
		allowHedges = getOption("AllowHedges", allowHedges, properties);
		allowGrass = getOption("AllowGrass", allowGrass, properties);
		allowForageBotanize = getOption("AllowForageBotanize", allowForageBotanize, properties);
		augmentExamine = getOption("StatusOnExamine", augmentExamine, properties);
		
		TileAction.setAllowTrees(getOption("AllowTrees", TileAction.getAllowTrees(), properties));
		TileAction.setAllowBushes(getOption("AllowBushes", TileAction.getAllowBushes(), properties));
		KelpReedGrowAction.setAllowReed(getOption("AllowReed", KelpReedGrowAction.getAllowReed(), properties));
		KelpReedGrowAction.setAllowKelp(getOption("AllowKelp", KelpReedGrowAction.getAllowKelp(), properties));
		
		wateringaction.setCost(getOption("WateringCost", wateringaction.getCost(), properties));
		wateringaction.setTime(getOption("WateringTime", wateringaction.getTime(), properties));
		wateringaction.setItem(getOption("WateringItem", wateringaction.getItem(), properties));

		hedgeaction.setCost(getOption("WateringCost", hedgeaction.getCost(), properties));
		hedgeaction.setTime(getOption("WateringTime", hedgeaction.getTime(), properties));
		hedgeaction.setItem(getOption("WateringItem", hedgeaction.getItem(), properties));

		grassaction.setCost(getOption("WateringCost", grassaction.getCost(), properties));
		grassaction.setTime(getOption("WateringTime", grassaction.getTime(), properties));
		grassaction.setItem(getOption("WateringItem", grassaction.getItem(), properties));

		kelpreedaction.setCost(getOption("FertilizingCost", kelpreedaction.getCost(), properties));
		kelpreedaction.setTime(getOption("FertilizingTime", kelpreedaction.getTime(), properties));
		kelpreedaction.setItem(getOption("FertilizingItem", kelpreedaction.getItem(), properties));

		fertilizingaction.setCost(getOption("FertilizingCost", fertilizingaction.getCost(), properties));
		fertilizingaction.setTime(getOption("FertilizingTime", fertilizingaction.getTime(), properties));
		fertilizingaction.setItem(getOption("FertilizingItem", fertilizingaction.getItem(), properties));

		useOriginalGrowthFunction = getOption("UseOriginalGrowthFunction", useOriginalGrowthFunction, properties);
		TreeGrowTask.setUseOriginalGrowthFunction(useOriginalGrowthFunction);
		GrassGrowTask.setUseOriginalGrowthFunction(useOriginalGrowthFunction);
		ForageBotanizeTask.setUseOriginalGrowthFunction(useOriginalGrowthFunction);

		AbstractAction.setCostSkillMultiplier(getOption("CostSkillMultiplier", AbstractAction.getCostSkillMultiplier(), properties));
		AbstractAction.setCostAgeMultiplier(getOption("CostAgeMultiplier", AbstractAction.getCostAgeMultiplier(), properties));
		AbstractAction.setTimeSkillMultiplier(getOption("TimeSkillMultiplier", AbstractAction.getTimeSkillMultiplier(), properties));
		AbstractAction.setGrowthTimeQualityMultiplier(getOption("GrowthTimeQualityMultiplier", AbstractAction.getGrowthTimeQualityMultiplier(), properties));
		AbstractAction.setGrowthTimeSkillMultiplier(getOption("GrowthTimeSkillMultiplier", AbstractAction.getGrowthTimeSkillMultiplier(), properties));
		AbstractAction.setGainSkill(getOption("GainSkill", AbstractAction.getGainSkill(), properties));

		AbstractAction.setCheckIfPolled(getOption("CheckIfPolled", AbstractAction.getCheckIfPolled(), properties));
		AbstractAction.setCheckConditions(getOption("CheckConditions", AbstractAction.getCheckConditions(), properties));

		CoolDownTask.setCoolDownMultiplier(getOption("CoolDownMultiplier", CoolDownTask.getCoolDownMultiplier(), properties));

		TaskPoller.setPollInterval(getOption("PollInterval", TaskPoller.getPollInterval(), properties));
		TaskPoller.setPreserveList(getOption("PreserveList", TaskPoller.getPreserveList(), properties));
		TaskPoller.setProtectTasks(getOption("ProtectTasks", TaskPoller.getProtectTasks(), properties));

		boolean keepgrowing = getOption("KeepGrowing", TreeGrowTask.getKeepGrowing(), properties);
		TreeGrowTask.setKeepGrowing(keepgrowing);
		HedgeTask.setKeepGrowing(keepgrowing);
		GrassGrowTask.setKeepGrowing(keepgrowing);

		TreeGrowTask.setAgeLimit(getOption("AgeLimit", TreeGrowTask.getAgeLimit(), properties));
		TreeGrowTask.setCheckForWUPoll(getOption("CheckForWUPoll", TreeGrowTask.getCheckForWUPoll(), properties));
		
		AbstractTask.setBaseGrowthTime(getOption("BaseGrowthTime", AbstractTask.getBaseGrowthTime(), properties));
		
		double growthMultiplierGrow = getOption("GrowthMultiplierGrow",  TreeGrowTask .getGrowthMultiplier(), properties);
		TreeGrowTask .setGrowthMultiplier(growthMultiplierGrow);
		GrassGrowTask .setGrowthMultiplier(growthMultiplierGrow);
		FruitTask.setGrowthMultiplier(getOption("GrowthMultiplierFruit", FruitTask.getGrowthMultiplier(), properties));

		TreeTileTask.setGrowthMultiplierTree(getOption("GrowthMultiplierTree", TreeTileTask.getGrowthMultiplierTree(), properties));
		TreeTileTask.setGrowthMultiplierBush(getOption("GrowthMultiplierBush", TreeTileTask.getGrowthMultiplierBush(), properties));
		GrassTileTask.setGrowthMultiplierGrass(getOption("GrowthMultiplierGrass", GrassTileTask.getGrowthMultiplierGrass(), properties));
		HedgeTask.setGrowthMultiplier(getOption("GrowthMultiplierHedge", HedgeTask.getGrowthMultiplier(), properties));

		TreeTileTask.setGrowthMultiplierBirch   (getOption("GrowthMultiplierBirch",    TreeTileTask.getGrowthMultiplierBirch(),    properties));
		TreeTileTask.setGrowthMultiplierPine    (getOption("GrowthMultiplierPine",     TreeTileTask.getGrowthMultiplierPine(),     properties));
		TreeTileTask.setGrowthMultiplierOak     (getOption("GrowthMultiplierOak",      TreeTileTask.getGrowthMultiplierOak(),      properties));
		TreeTileTask.setGrowthMultiplierCedar   (getOption("GrowthMultiplierCedar",    TreeTileTask.getGrowthMultiplierCedar(),    properties));
		TreeTileTask.setGrowthMultiplierWillow  (getOption("GrowthMultiplierWillow",   TreeTileTask.getGrowthMultiplierWillow(),   properties));
		TreeTileTask.setGrowthMultiplierMaple   (getOption("GrowthMultiplierMaple",    TreeTileTask.getGrowthMultiplierMaple(),    properties));
		TreeTileTask.setGrowthMultiplierApple   (getOption("GrowthMultiplierApple",    TreeTileTask.getGrowthMultiplierApple(),    properties));
		TreeTileTask.setGrowthMultiplierLemon   (getOption("GrowthMultiplierLemon",    TreeTileTask.getGrowthMultiplierLemon(),    properties));
		TreeTileTask.setGrowthMultiplierOlive   (getOption("GrowthMultiplierOlive",    TreeTileTask.getGrowthMultiplierOlive(),    properties));
		TreeTileTask.setGrowthMultiplierCherry  (getOption("GrowthMultiplierCherry",   TreeTileTask.getGrowthMultiplierCherry(),   properties));
		TreeTileTask.setGrowthMultiplierChestnut(getOption("GrowthMultiplierChestnut", TreeTileTask.getGrowthMultiplierChestnut(), properties));
		TreeTileTask.setGrowthMultiplierWalnut  (getOption("GrowthMultiplierWalnut",   TreeTileTask.getGrowthMultiplierWalnut(),   properties));
		TreeTileTask.setGrowthMultiplierFir     (getOption("GrowthMultiplierFir",      TreeTileTask.getGrowthMultiplierFir(),      properties));
		TreeTileTask.setGrowthMultiplierLinden  (getOption("GrowthMultiplierLinden",   TreeTileTask.getGrowthMultiplierLinden(),   properties));

		TreeTileTask.setGrowthMultiplierNormal   (getOption("GrowthMultiplierNormal",    TreeTileTask.getGrowthMultiplierNormal(),    properties));
		TreeTileTask.setGrowthMultiplierEnchanted(getOption("GrowthMultiplierEnchanted", TreeTileTask.getGrowthMultiplierEnchanted(), properties));
		TreeTileTask.setGrowthMultiplierMycelium (getOption("GrowthMultiplierMycelium",  TreeTileTask.getGrowthMultiplierMycelium(),  properties));
		
		TreeTileTask.setGrowthMultiplierCamellia(getOption("GrowthMultiplierCamellia", TreeTileTask.getGrowthMultiplierCamellia(), properties));
		TreeTileTask.setGrowthMultiplierGrape   (getOption("GrowthMultiplierGrape",    TreeTileTask.getGrowthMultiplierGrape(),    properties));
		TreeTileTask.setGrowthMultiplierLavender(getOption("GrowthMultiplierLavender", TreeTileTask.getGrowthMultiplierLavender(), properties));
		TreeTileTask.setGrowthMultiplierOleander(getOption("GrowthMultiplierOleander", TreeTileTask.getGrowthMultiplierOleander(), properties));
		TreeTileTask.setGrowthMultiplierRose    (getOption("GrowthMultiplierRose",     TreeTileTask.getGrowthMultiplierRose(),     properties));
		TreeTileTask.setGrowthMultiplierThorn   (getOption("GrowthMultiplierThorn",    TreeTileTask.getGrowthMultiplierThorn(),    properties));
		
		for(int i = 0; i < 15; i++){
			TreeTileTask.setGrowthMultiplierAge( i, getOption("GrowthMultiplierAge" + i,  TreeTileTask.getGrowthMultiplierAge(i), properties));
		}
	}
//======================================================================
}

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
	private static boolean allowGrow = true;
	private static boolean allowFertilize = true;
	private static boolean allowHedges = true;
//======================================================================
	@Override
	public void onServerStarted()
	{
		wateringaction.registerAction();
		fertilizingaction.registerAction();
		hedgeaction.registerAction();
		
		if(allowGrow) ModActions.registerAction(wateringaction);
		if(allowFertilize) ModActions.registerAction(fertilizingaction);
		if(allowHedges) ModActions.registerAction(hedgeaction);
		
		boolean debug = true;
		if(debug){
			WateringAction wa2 = new WateringAction("WaterEX");
			FertilizingAction fa2 = new FertilizingAction("FertilizeEX");
			HedgeAction he2 = new HedgeAction("WaterEX");
	
			wa2.setCost(0);
			wa2.setTime(0);
			wa2.setItem(0);
	
			fa2.setCost(0);
			fa2.setTime(0);
			fa2.setItem(0);

			he2.setCost(0);
			he2.setTime(0);
			he2.setItem(0);

			wa2.registerAction();
			fa2.registerAction();
			he2.registerAction();
	
			ModActions.registerAction(wa2);
			ModActions.registerAction(fa2);
			ModActions.registerAction(he2);
			
			HedgePollAction hpa = new HedgePollAction();
			hpa.registerAction();
			ModActions.registerAction(hpa);
		}
	}
//======================================================================
	@Override
	public void init()
	{
		if(GrowTask.getUseOriginalGrowthFunction()){
			Hooks.injectTreeGrowthWrapper();
		}
		
		Hooks.registerListLoadingHook();
		Hooks.registerPollingHook();
		Hooks.registerListSavingHook();

		if(TaskPoller.getProtectTasks()){
			Hooks.registerTreeProtectionHook();
			Hooks.registerHedgeProtectionHook();
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
		allowGrow = getOption("AllowGrow", allowGrow, properties);
		allowFertilize = getOption("AllowFertilize", allowFertilize, properties);
		allowHedges = getOption("AllowHedges", allowHedges, properties);
		
		TileAction.setAllowTrees(getOption("AllowTrees", TileAction.getAllowTrees(), properties));
		TileAction.setAllowBushes(getOption("AllowBushes", TileAction.getAllowBushes(), properties));
		
		wateringaction.setCost(getOption("WateringCost", wateringaction.getCost(), properties));
		wateringaction.setTime(getOption("WateringTime", wateringaction.getTime(), properties));
		wateringaction.setItem(getOption("WateringItem", wateringaction.getItem(), properties));

		hedgeaction.setCost(getOption("WateringCost", hedgeaction.getCost(), properties));
		hedgeaction.setTime(getOption("WateringTime", hedgeaction.getTime(), properties));
		hedgeaction.setItem(getOption("WateringItem", hedgeaction.getItem(), properties));

		fertilizingaction.setCost(getOption("FertilizingCost", fertilizingaction.getCost(), properties));
		fertilizingaction.setTime(getOption("FertilizingTime", fertilizingaction.getTime(), properties));
		fertilizingaction.setItem(getOption("FertilizingItem", fertilizingaction.getItem(), properties));

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

		boolean keepgrowing = getOption("KeepGrowing", GrowTask.getKeepGrowing(), properties);
		GrowTask.setKeepGrowing(keepgrowing);
		HedgeTask.setKeepGrowing(keepgrowing);

		GrowTask.setAgeLimit(getOption("AgeLimit", GrowTask.getAgeLimit(), properties));
		GrowTask.setCheckForWUPoll(getOption("CheckForWUPoll", GrowTask.getCheckForWUPoll(), properties));
		GrowTask.setUseOriginalGrowthFunction(getOption("UseOriginalGrowthFunction", GrowTask.getUseOriginalGrowthFunction(), properties));
		
		AbstractTask.setBaseGrowthTime(getOption("BaseGrowthTime", AbstractTask.getBaseGrowthTime(), properties));
		
		GrowTask .setGrowthMultiplier(getOption("GrowthMultiplierGrow",  GrowTask .getGrowthMultiplier(), properties));
		FruitTask.setGrowthMultiplier(getOption("GrowthMultiplierFruit", FruitTask.getGrowthMultiplier(), properties));

		TreeTileTask.setGrowthMultiplierTree(getOption("GrowthMultiplierTree", TreeTileTask.getGrowthMultiplierTree(), properties));
		TreeTileTask.setGrowthMultiplierBush(getOption("GrowthMultiplierBush", TreeTileTask.getGrowthMultiplierBush(), properties));

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

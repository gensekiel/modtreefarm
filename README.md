# TreeFarmMod - An Accelerated Forestry Server Mod for Wurm Unlimited

If your trees don't want to grow, make them.

Current version: **beta 0.9.1**

This mod allows you to speed up tree and bush growth by watering, and to
fertilize them, making them grow fruit/produce out of season. Without 
waiting for ages.

Activating water in your inventory enables a watering action for tree 
and bush tiles that will consume some amount of water. After watering,
growth by one stage will occur within a configurable amount of time, 
after which the tile can be watered again. 
Activating a fertilizer item (default: ash) enables a fertilizing 
action, which will consume some amount of fertilizer. After fertilizing,
the tile will be harvestable after a configurable amount of time. After
harvesting it can be fertilized again.

* Accelerated growth ignores all related effects that otherwise
would be invoked when a tree or bush grows\*
* Accelerated fruit/produce ripening ignores seasons
* Configurable action time, action item, action cost
* Configurable poll interval for watered/fertilized tiles
* Configurable growth rates and modifiers (for type, age, etc.)
* Age limit for growth by watering
* Persistent tracked tile list when shutting down the server
* Optional continuous growth until the age limit is reached
* Disable single actions or plant types
* Optionally use the original growth function will all related 
effects and chances

\* Such as tree death & self re-plant, mycelium decay, small chances for 
tree growth depending on age and tree type, killing area checks for oaks 
and willows, sprouting to nearby tiles, mushrooms, etc. These effects 
still occur, but at a normal rate.

Growth time/ripening time can be defined by a base growth time and several multipliers
for tree type, age, species, task and modifier (normal, enchanted, mycelium). The time 
required to grow a tree, t, is 

    TreeGrowthTime < t < TreeGrowthTime + PollInterval

where TreeGrowthTime is the growth time as specified by TreeGrowthBaseTime and respective multipliers.

    TreeGrowthTime =   TreeGrowthBaseTime
                     * task_multiplier
                     * type_multiplier
                     * species_multiplier 
                     * age_multiplier
                     * modifier_multiplier

Modifiers can be tweaked separately for all tree/bush types and ages 0 to 14.

For more information, see the mod's properties file.

Planned features:

* Incorporate forestry/gardening skill; watering time, amount of water
and growth time are reduced and the skill is improved
* Cooldown for watering and fertilizing
* Grow grass
* Fertilize grass -> refresh forage & botanize
* Grow flowers using something other than flowers
* Grow hedges
* Active protection against normal tile polling; prevent
conventional tree growth for tracked tiles alltogether
* Show status on examine
* Rename mod to Accelerated Forestry or similar

Requirements:

* [Ago's Mod Launcher](https://github.com/ago1024/WurmServerModLauncher), version 0.21 ([Wurm Forum page](http://forum.wurmonline.com/index.php?/topic/133085-released-server-mod-loader-priest-crops-seasons-server-packs-bag-of-holding/))
* Intended for Wurm Unlimited 1.1.2.3

As always with modded content, I rid me of all liability. Although the 
mod in non-invasive and can be removed at any time without any effect on 
the world, make a backup. Just to be sure.

[0.9-beta](https://github.com/gensekiel/modtreefarm/releases/tag/0.9-beta)

[GitHub page](https://github.com/gensekiel/modtreefarm)

**Installation:** Just extract the contents of the .zip file into the 
mods folder in your WurmServerLauncher directory. If you don't have a 
mods directory, you need to install Agos's mod launcher. 

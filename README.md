# TreeFarmMod - A Tree Farm Server Mod for Wurm Unlimited 

If your trees don't want to grow, make them.

Current version: **beta 0.9**

This mod allows you to speed up tree growth by watering. Activating
water in your inventory enables a watering action for tree tiles that 
will consume some amount of water. After watering a tree it will grow 
by one stage withing a configurable amount of time, after which it can
be watered again.

Options and features:

* Accelerated tree growth ignores all related effects that otherwise
would be invoked when a tree grows\*
* Configurable amount of water required for watering
* Configurable amount of time spent for watering
* Configurable poll interval for watered tree tiles
* Configurable growth rates and modifiers (for type, age, etc.)
* Tree age limit after which watering will not make the tree grow
* Persistent watered tree tile list when shutting down the server
* Optional continuous growth until the tree age limit is reached
* Optionally use the original tree growth function will all related 
effects and chances

\* Such as tree death & self re-plant, mycelium decay, small chances for 
tree growth depending on age and tree type, killing area checks for oaks 
and willows, sprouting to nearby tiles, mushrooms, etc. These effects 
still occur, but at a normal rate.

Growth time can be defined by a base growth time and several multipliers
for tree type, age and modifier (normal, enchanted, mycelium). The time 
required to grow a tree, t, is 

    TreeGrowthTime < t < TreeGrowthTime + PollInterval

where TreeGrowthTime is the growth time for a certain type and age of
tree as specified by TreeGrowthBaseTime and respective multipliers.

    TreeGrowthTime =   TreeGrowthBaseTime 
                     * type_multiplier 
                     * age_multiplier
                     * modifier_multiplier

Modifiers can be tweaked separately for all tree types and ages 0 to 14.

For more information, see the mod's properties file.

Things to do:

* Incorporate gardening skill; watering time, amount of water
and growth time are reduced and the skill is improved

* Active protection against normal tile polling; prevent
conventional tree growth for watered tiles alltogether

* Show watering status on examine

Requirements:

* [Ago's Mod Launcher](https://github.com/ago1024/WurmServerModLauncher), version 0.21
* Intended for Wurm Unlimited 1.1.2.3

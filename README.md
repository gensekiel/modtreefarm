# Accelerated Forestry & Gardening (& Farming) Server Mod for Wurm Unlimited

This mod allows to speed up the growth of trees, bushes, fruit (and
other harvestables), hedges, grass, kelp, reed, flowers, trellises, herb
planters, fields and to refresh foraging and botanizing for tiles that
allow it. The default items for watering and fertilizing are water and
ash, respectively.

Every action to start a task consumes a certain amount of a watering or
fertilizing item. After the action is performed, a certain time span is
required for the task to be performed. After the task has been finished,
another task can be started for that tile/object. While there can exist
only one task for a particular tile/object at a time, the number of
simultaneous tasks for different tiles/objects is potentially infinite.

Possible tasks are *(if enabled, actions use and increase either the
forestry or gardening skill; the skill governing an action is indicated
in parentheses)*:

* Make trees and bushes grow by watering them. *(forestry)*
* Make hedges grow by watering them. *(gardening)*
* Have trees and bushes grow fruit (or other harvestables) out of season
by fertilizing them. *(forestry)*
* Increase the grass height on grass and tree tiles by watering the
ground. *(gardening)*
* Grow (random) flowers on a grass tile by watering it when at maximum
height. In contrast to conventional flower growth, all flowers have the
same probability. *(gardening)*
* Make kelp and reed grow by fertilizing them. Note that there is no
visual indicator of height for these tiles. *(gardening)*
* Refresh the ability to forage and/or botanize tiles that allow it by
fertilizing them. *(gardening)*
* Make trellises grow by watering them. *(gardening)*
* Make trellises (except ivy) harvestable out of season by fertilizing
them. *(gardening)*
* Make herb planters age faster by watering them to reach the age at
which they can be harvested. *(gardening)*
* Make herb planters harvestable by fertilizing them when at the right
age. *(gardening)*
* Make fields grow by watering them. *(farming)*
* Make underwater fields grow by fertilizing them. *(farming)*

Almost every aspect can be modified via the configuration file.

* Time required by tasks and polling interval (see below).
* Cost, time and item required for actions.
* Influence of skill, item quality and age on task time, action
time and action cost.

Other options are:

* Block certain actions or tile/object types.
* Show status on examine if a task is enqueued for the examined
tile/object.
* Enforce a cool down time for an object or tile after a task has been
finished.
* Continuous growth until some age or height limit is reached
(conventional step-wise growth after that).
* Age limit for trees and bushes.
* Gain skill for performing actions.
* Persistent task list when shutting down the server.
* Obey tile protection.
* Active protection against conventional server poll for tracked
objects.
* Use the original growth functions with all related effects and
chances.
* Optional deactivation of the vanilla timer-based field growth mechanics.

Task time can be defined by a base time and several multipliers for
task type, object/tile type, species, age, modifier (normal, enchanted,
mycelium), skill and item quality. The time required to grow a tree, t,
is

    TaskTime < t < TaskTime + PollInterval

where TaskTime is the time required for a task as specified by
BaseTaskTime and a collection of multipliers.

    TaskTime =   BaseTaskTime
               * task_multiplier
               * type_multiplier
               * species_multiplier
               * age_multiplier
               * modifier_multiplier
               * skill_multiplier
               * quality_multiplier

Multipliers can be tweaked separately for all kinds of conditions. For
more information, see the mod's properties file.

Other information:

* Accelerated tree and bush growth ignores all related effects that
otherwise would be invoked when a tree or bush grows.\*
* Accelerated fruit/harvestable growth ignores seasons.
* Accelerated grass growth ignores seasonal growth rates.

\* Such as tree death & self re-plant, mycelium decay, killing area
checks for oaks and willows, sprouting to nearby tiles, mushrooms, etc.
These effects still occur, but at a normal rate.

Planned features:

* What else could need acceleration? Any suggestions?

Requirements:

* [Ago's Mod Launcher](https://github.com/ago1024/WurmServerModLauncher), version 0.43 ([Wurm Forum page](http://forum.wurmonline.com/index.php?/topic/133085-released-server-mod-loader-priest-crops-seasons-server-packs-bag-of-holding/))
* Intended for Wurm Unlimited 1.9.1.5

As always with modded content, I rid me of all liability. Although the
mod is non-invasive and can be removed at any time without any effect on
the world, make a backup. Just to be sure.

[Get it here](https://github.com/gensekiel/modtreefarm/releases/latest)

[GitHub page](https://github.com/gensekiel/modtreefarm)

**Installation:** Just extract the contents of the .zip file into the
mods folder in your WurmServerLauncher directory. If you don't have a
mods directory, you need to install Agos's mod launcher.

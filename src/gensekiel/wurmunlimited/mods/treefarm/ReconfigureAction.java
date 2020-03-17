package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.creatures.Creature;

public class ReconfigureAction extends DebugAction
{
//======================================================================
	protected ReconfigureAction(String menu){ super(menu); }
//======================================================================
	@Override
	protected void action(Creature performer)
	{
		if(TreeFarmMod.reconfigure()){
			performer.getCommunicator().sendNormalServerMessage("Config read from properties file.", (byte)1);
		}else{
			performer.getCommunicator().sendNormalServerMessage("Could not read properties file.", (byte)1);
		}
	}
//======================================================================
}

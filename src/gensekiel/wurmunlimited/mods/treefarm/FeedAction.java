package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.creatures.Creature;

public class FeedAction extends DebugAction
{
//======================================================================
	protected FeedAction(String menu){ super(menu); }
//======================================================================
	@Override
	protected void action(Creature performer)
	{
		performer.getStatus().modifyThirst(-65535.0f,2000.0f,300.0f,80.0f,50.0f);
		performer.getStatus().modifyHunger(-65535,0.99f,2000.0f,300.0f,80.0f,50.0f);
	}
//======================================================================
}

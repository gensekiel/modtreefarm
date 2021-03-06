package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class GainSomeSkillAction extends DebugAction
{
//======================================================================
	protected GainSomeSkillAction(String menu){ super(menu); }
//======================================================================
	@Override
	protected void action(Creature performer)
	{
		int skills[] = {10045, 10048, 10049};
		for(int i : skills){
			Skill s = performer.getSkills().getSkillOrLearn(i);
			s.setKnowledge(99.0, false);
		}
		performer.getCommunicator().sendNormalServerMessage("Wiped " + skills.length + " skills.", (byte)1);
	}
//======================================================================
}

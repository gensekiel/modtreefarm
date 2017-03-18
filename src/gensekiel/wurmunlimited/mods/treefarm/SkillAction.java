package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;

public class SkillAction extends DebugAction
{
//======================================================================
	protected SkillAction(String menu){ super(menu); }
//======================================================================
	@Override
	protected void action(Creature performer)
	{
		for(int i : SkillList.skillArray) performer.getSkills().getSkillOrLearn(i);
		Skill skills[] = performer.getSkills().getSkills();
		for(Skill s : skills) s.setKnowledge(99.0, false);
		performer.getCommunicator().sendNormalServerMessage("Max'ed " + skills.length + " skills.", (byte)1);
	}
//======================================================================
}

package gensekiel.wurmunlimited.mods.treefarm;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;

public class NoSkillAction extends DebugAction
{
//======================================================================
	protected NoSkillAction(String menu){ super(menu); }
//======================================================================
	@Override
	protected void action(Creature performer)
	{
		for(int i : SkillList.skillArray) performer.getSkills().getSkillOrLearn(i);
		Skill skills[] = performer.getSkills().getSkills();
		for(Skill s : skills) s.setKnowledge(0.0, false);
		performer.getCommunicator().sendNormalServerMessage("Wiped " + skills.length + " skills.", (byte)1);
	}
//======================================================================
}

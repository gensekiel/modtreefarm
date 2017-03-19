package gensekiel.wurmunlimited.mods.treefarm;

import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.behaviours.ActionEntry;

public abstract class ActionTemplate implements ModAction, BehaviourProvider, ActionPerformer
{
//======================================================================
	protected short actionId;
	protected ActionEntry actionEntry;
	protected String menuEntry = null;
	protected String actionVerbIng = "-";
//======================================================================
	public void register()
	{
		actionId = (short)ModActions.getNextActionId();
		actionEntry = ActionEntry.createEntry(actionId, menuEntry, actionVerbIng, new int[] {6, 48, 35});
		ModActions.registerAction(actionEntry);
		ModActions.registerAction(this);
	}
//======================================================================
	@Override
	public short getActionId(){ return actionId; }
	@Override
	public BehaviourProvider getBehaviourProvider(){ return this; }
	@Override
	public ActionPerformer getActionPerformer(){ return this; }
//======================================================================
}

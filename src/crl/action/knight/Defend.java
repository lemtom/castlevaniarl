package crl.action.knight;

import crl.action.Action;
import crl.action.HeartAction;
import crl.actor.Actor;

public class Defend extends HeartAction {
private static final long serialVersionUID = 1L;
	public int getHeartCost() {
		return 1;
	}

	public String getID() {
		return "DEFEND";
	}

	@Override
	public boolean needsDirection() {
		return true;
	}

	@Override
	public String getPromptDirection() {
		return "Where will you locate your shield to?";
	}

	@Override
	public void execute() {
		super.execute();
		if (targetDirection == Action.SELF) {
			return;
		}
		getPlayer().getLevel().addMessage("You defend yourself with your " + getPlayer().getShield().getDescription());
		getPlayer().setShieldGuard(targetDirection, 5);
	}

	@Override
	public boolean canPerform(Actor a) {
		if (getPlayer().getShield() == null) {
			invalidationMessage = "You don't have a shield.";
			return false;
		}
		return super.canPerform(a);
	}

}

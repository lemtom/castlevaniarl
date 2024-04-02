package crl.action.vanquisher;

import crl.action.HeartAction;

public class Cure extends HeartAction {
	public int getHeartCost() {
		return 5;
	}

	public String getID() {
		return "CURE";
	}

	@Override
	public void execute() {
		super.execute();
		getPlayer().cure();
	}
}

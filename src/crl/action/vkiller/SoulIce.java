package crl.action.vkiller;

import crl.action.HeartAction;
import crl.player.Player;

public class SoulIce extends HeartAction {
private static final long serialVersionUID = 1L;
	public int getHeartCost() {
		return 20;
	}

	public String getID() {
		return "Soul Ice";
	}

	@Override
	public int getCost() {
		Player p = (Player) performer;
		return (int) (p.getCastCost() * 1.1);
	}

	@Override
	public void execute() {
		super.execute();
		Player aPlayer = (Player) performer;
		aPlayer.recoverHitsP(20 + aPlayer.getSoulPower());
		aPlayer.getLevel().addMessage("You feel relieved!");
	}
}
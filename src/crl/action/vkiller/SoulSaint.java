package crl.action.vkiller;

import crl.action.HeartAction;
import crl.player.Consts;
import crl.player.Player;

public class SoulSaint extends HeartAction {
private static final long serialVersionUID = 1L;
	public int getHeartCost() {
		return 15;
	}

	public String getID() {
		return "Soul Saint";
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
		aPlayer.setCounter(Consts.C_FIREBALL_WHIP, 50 + 3 * aPlayer.getSoulPower());
		aPlayer.getLevel().addMessage("Your whip glows!");
	}
}
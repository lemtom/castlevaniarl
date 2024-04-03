package crl.action.vanquisher;

import crl.action.Action;
import crl.actor.Actor;
import crl.level.Level;
import crl.player.Damage;
import crl.player.Player;

public class MajorJinx extends Action {
private static final long serialVersionUID = 1L;
	public String getID() {
		return "MAJOR_JINX";
	}

	@Override
	public int getCost() {
		Player p = (Player) performer;
		return (int) (p.getCastCost() * 1.1);
	}

	public void execute() {
		Player aPlayer = (Player) performer;
		int recover = 5 + aPlayer.getSoulPower() * 2;
		aPlayer.addHearts(recover);
		aPlayer.selfDamage("You exchange vitality for power!! (+" + recover + ")", Player.DAMAGE_JINX,
				new Damage(5, true));
	}

	@Override
	public boolean canPerform(Actor a) {
		Player aPlayer = (Player) a;
		Level aLevel = performer.getLevel();
		if (aPlayer.getHits() <= 5) {
			aLevel.addMessage("That would be suicidal!");
			return false;
		}
		return true;
	}
}
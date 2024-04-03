package crl.action.invoker;

import sz.util.Util;
import crl.action.HeartAction;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Consts;
import crl.player.Player;

public class Charm extends HeartAction {
private static final long serialVersionUID = 1L;
	public String getID() {
		return "Charm";
	}

	@Override
	public String getPromptPosition() {
		return "Who?";
	}

	@Override
	public boolean needsPosition() {
		return true;
	}

	public int getHeartCost() {
		return 5;
	}

	@Override
	public void execute() {
		super.execute();
		Player aPlayer = (Player) performer;
		Level x = performer.getLevel();
		Monster m = x.getMonsterAt(targetPosition);
		int chance = 40 + 2 * aPlayer.getSoulPower();
		if (aPlayer.getFlag("SKILL_CONFIDENCE"))
			chance += 20;
		if (m == null) {
			x.addMessage("Nothing happens.");
		} else {
			if (Util.chance(chance)) {
				x.addMessage("You manipulate the " + m.getDescription() + " soul!");
				m.setCounter(Consts.C_MONSTER_CHARM, 50 + aPlayer.getSoulPower() * 5);
			} else {
				x.addMessage("You tried to manipulate the " + m.getDescription() + " soul.");
			}

		}

	}

	@Override
	public String getSFX() {
		return "wav/clockbel.wav";
	}

	public double getTimeCostModifier() {
		return 3;
	}
}
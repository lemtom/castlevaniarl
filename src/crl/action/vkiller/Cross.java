package crl.action.vkiller;

import sz.util.Util;
import crl.action.ProjectileSkill;
import crl.feature.SmartFeature;
import crl.feature.SmartFeatureFactory;
import crl.feature.ai.CrossAI;
import crl.player.Player;

public class Cross extends ProjectileSkill {
	private static final long serialVersionUID = 1L;

	public int getDamage() {
		return 5 + getPlayer().getShotLevel() + 2 * getPlayer().getSoulPower();
	}

	public int getHit() {
		return 100;
	}

	public int getPathType() {
		return PATH_LINEAR;
	}

	public int getRange() {
		return 7;
	}

	public String getSelfTargettedMessage() {
		return "You throw the holy cross!";
	}

	public String getSFXID() {
		return "SFX_CROSS";
	}

	public String getShootMessage() {
		return "You throw the holy cross!";
	}

	public String getSpellAttackDesc() {
		return "cross";
	}

	public int getHeartCost() {
		return 3;
	}

	public String getID() {
		return "Cross";
	}

	@Override
	public void execute() {
		super.execute();
		if (targetPosition.equals(performer.getPosition())) {
			if (Util.chance(50)) {
				performer.getLevel().addMessage("The cross falls heads! You catch the cross.");
			} else {
				performer.getLevel().addMessage("The cross falls tails! You catch the cross.");
			}
			return;
		}
		if (performer instanceof Player) {
			SmartFeature cross = SmartFeatureFactory.getFactory().buildFeature("CROSS");
			((CrossAI) cross.getSelector()).setTargetPosition(getPlayer().getPosition());
			cross.setPosition(finalPoint);
			getPlayer().getLevel().addSmartFeature(cross);
		}
	}

	public String getPromptPosition() {
		return "Where do you want to throw the Cross?";
	}

	@Override
	public int getCost() {
		if (performer instanceof Player) {
			Player p = (Player) performer;
			return 25 / (p.getShotLevel() + 1);
		} else
			return 40;
	}

	@Override
	public boolean piercesThru() {
		return true;
	}

	@Override
	public String getSFX() {
		return "wav/misswipe.wav";
	}
}

package crl.action.manbeast;

import crl.action.ProjectileSkill;

public class ClawAssault extends ProjectileSkill {
private static final long serialVersionUID = 1L;
	public int getDamage() {
		return 15 + getPlayer().getPunchDamage() * 2;
	}

	public int getHit() {
		return 100;
	}

	public int getPathType() {
		return PATH_LINEAR;
	}

	public String getPromptPosition() {
		return "Where?";
	}

	public int getRange() {
		return 5;
	}

	public String getSelfTargettedMessage() {
		return null;
	}

	public String getSFXID() {
		return null;
	}

	public String getShootMessage() {
		return "You jump into the enemy!";
	}

	public String getSpellAttackDesc() {
		return "attack";
	}

	public int getHeartCost() {
		return 5;
	}

	public String getID() {
		return "ClawAssault";
	}

	@Override
	public boolean allowsSelfTarget() {
		return false;
	}

	@Override
	public boolean piercesThru() {
		return true;
	}

	@Override
	public void execute() {
		super.execute();
		getPlayer().setPosition(finalPoint);
		getPlayer().land();
	}
}

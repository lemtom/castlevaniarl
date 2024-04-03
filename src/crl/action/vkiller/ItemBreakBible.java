package crl.action.vkiller;

import crl.action.BeamProjectileSkill;
import crl.player.Player;

public class ItemBreakBible extends BeamProjectileSkill {
private static final long serialVersionUID = 1L;

	public int getDamage() {
		return 7 + getPlayer().getShotLevel() + getPlayer().getSoulPower() * 2;
	}

	@Override
	public boolean piercesThru() {
		return true;
	}

	public int getHit() {
		return 100;
	}

	public int getPathType() {
		return PATH_LINEAR;
	}

	public int getRange() {
		return 15;
	}

	public String getSelfTargettedMessage() {
		return "The fireball flies to the heavens";
	}

    public String getSFXID() {
		return "SFX_ITEMBREAKBIBLE";
	}

	public String getShootMessage() {
		return "The bible opens and shreds a beam of light!";
	}

	public String getSpellAttackDesc() {
		return "beam of light";
	}

	public int getHeartCost() {
		return 2;
	}

	public String getID() {
		return "ItemBreakBible";
	}

	@Override
	public String getSFX() {
		return "wav/fire.wav";
	}

	@Override
	public int getCost() {
		Player p = (Player) performer;
		return (int) (p.getCastCost() * 1.1);
	}

	public String getPromptPosition() {
		return "Where?";
	}
}
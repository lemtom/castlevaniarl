package crl.action.vanquisher;

import crl.action.ProjectileSkill;
import crl.player.Player;

public class LitSpell extends ProjectileSkill {
private static final long serialVersionUID = 1L;
	public int getDamage() {
		return 5+4*getPlayer().getSoulPower();
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
		return "You feel shocked";
	}

	public String getSFXID() {
		return "SFX_LIT_SPELL";
	}

	public String getShootMessage() {
		return "You invoke the spell of Lighting!";
	}

	public String getSpellAttackDesc() {
		return "lighting bolt";
	}

    public int getHeartCost() {
		return 8;
	}

	public String getID(){
		return "LitSpell";
	}
	
	@Override
	public int getCost(){
		Player p = (Player) performer;
		return (int)(p.getCastCost() * 1.3);
	}
	
	public String getPromptPosition(){
		return "Where do you want to invoke the lighting?";
	}
}
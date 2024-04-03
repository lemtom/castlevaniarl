package crl.action.renegade;

import crl.action.ProjectileSkill;
import crl.player.Player;

public class Fireball extends ProjectileSkill {
private static final long serialVersionUID = 1L;
	
	public int getDamage() {
		return 3+getPlayer().getSoulPower()*2;
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
		return "SFX_FIREBALL";
	}

	public String getShootMessage() {
		return "You launch a fireball!";
	}

	public String getSpellAttackDesc() {
		return "fireball";
	}

	public int getHeartCost() {
		return 2;
	}

	public String getID(){
		return "Fireball";
	}
	
	@Override
	public String getSFX(){
		return "wav/fire.wav";
	}

	@Override
	public int getCost(){
		Player p = (Player) performer;
		return (int)(p.getCastCost() * 1.1);
	}
	
	public String getPromptPosition(){
		return "Where do you want to throw the fireball?";
	}
}
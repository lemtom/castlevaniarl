package crl.action.renegade;

import crl.action.ProjectileSkill;
import crl.player.Player;

public class SummonSpirit extends ProjectileSkill{
	public int getDamage() {
		return 10+getPlayer().getSoulPower()*2;
	}

	public int getHit() {
		return 100;
	}

	public int getPathType() {
		return PATH_DIRECT;
	}

	public String getPromptPosition() {
		return "Where?";
	}

	public int getRange() {
		return 20;
	}

	public String getSelfTargettedMessage() {
		return "The spirit circles in an infinite spiral";
	}

	public String getSFXID() {
		return "SFX_SUMMON_SPIRIT";
	}

	public String getShootMessage() {
		return "You summon a white spirit!";
	}

	public String getSpellAttackDesc() {
		return "white spirit";
	}

	public int getHeartCost() {
		return 4;
	}

	public String getID(){
		return "SummonSpirit";
	}
	
	@Override
	public String getSFX(){
		return "wav/scrch.wav";
	}
	
	@Override
	public int getCost(){
		Player p = (Player) performer;
		return (int)(p.getCastCost() * 1.6);
	}
}
package crl.action.weapon;

import crl.action.ProjectileSkill;

public class EnergyBeam extends ProjectileSkill{
	public int getDamage() {
		return 8 + getPlayer().getSoulPower()*5;
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
		return "The beam fissles";
	}

	public String getSFXID() {
		return "SFX_ENERGY_BEAM";
	}

	public String getShootMessage() {
		return "A beam of light rises from your weapon";
	}

	public String getSpellAttackDesc() {
		return "beam";
	}

	public int getHeartCost() {
		return 10;
	}

	public String getID(){
		return "EnergyBeam";
	}
	
	public String getPromptPosition(){
		return "Where do you want to point the staff at?";
	}

	@Override
	public boolean piercesThru() {
		return true;
	}
	
	
}
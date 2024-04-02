package crl.action.vanquisher;

import java.util.Vector;

import crl.action.ProjectileSkill;
import crl.monster.Monster;
import crl.player.Consts;
import crl.player.Player;

public class MindLock extends ProjectileSkill{
	public int getDamage() {
		return 0;
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
		return "You fall asleep!";
	}
	
	@Override
	public boolean allowsSelfTarget() {
		return false;
	}

	public String getSFXID() {
		return "SFX_SLEEP_SPELL";
	}

	public String getShootMessage() {
		return "You throw a mindlock ray!";
	}

	public String getSpellAttackDesc() {
		return "mental disruption";
	}

	@Override
	public boolean piercesThru() {
		return true;
	}

	public int getHeartCost() {
		return 7;
	}

	public String getID(){
		return "Mindlock";
	}
	
	@Override
	public void execute(){
		super.execute();
		Vector<Monster> hitMonsters = getHitMonsters();
		for (int i = 0; i < hitMonsters.size(); i++){
			Monster targetMonster = hitMonsters.elementAt(i);
			if (targetMonster.wasSeen())
				targetMonster.getLevel().addMessage("The "+targetMonster.getDescription()+ "'s mind is locked!");
			targetMonster.setCounter(Consts.C_MONSTER_SLEEP, getPlayer().getSoulPower()*2+5);
		}
	}

	@Override
	public int getCost(){
		Player p = (Player) performer;
		return (int)(p.getCastCost() * 1.3);
	}
	
	public String getPromptPosition(){
		return "Where do you want to project the ray?";
	}
	
}
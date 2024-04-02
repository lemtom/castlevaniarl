package crl.action.vanquisher;

import java.util.ArrayList;

import crl.action.ProjectileSkill;
import crl.monster.Monster;
import crl.player.Consts;
import crl.player.Player;

public class Sleep extends ProjectileSkill{
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

	public String getSFXID() {
		return "SFX_SLEEP_SPELL";
	}

	public String getShootMessage() {
		return "You invoke the spell of Sleep!";
	}

	public String getSpellAttackDesc() {
		return "sleep beam";
	}

	@Override
	public boolean piercesThru() {
		return true;
	}

	public int getHeartCost() {
		return 7;
	}

	public String getID(){
		return "SpellSpell";
	}
	
	@Override
	public void execute(){
		super.execute();
		ArrayList<Monster> hitMonsters = getHitMonsters();
		for (int i = 0; i < hitMonsters.size(); i++){
			Monster targetMonster = hitMonsters.get(i);
			if (targetMonster.wasSeen())
				targetMonster.getLevel().addMessage("The "+targetMonster.getDescription()+ " is frozen!");
			targetMonster.setCounter(Consts.C_MONSTER_SLEEP, 10);
		}
	}

	@Override
	public int getCost(){
		Player p = (Player) performer;
		return (int)(p.getCastCost() * 1.3);
	}
	
	public String getPromptPosition(){
		return "Where do you want to invoke the frost?";
	}
	
}
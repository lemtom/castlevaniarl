package crl.ai.monster;

import crl.ai.ActionSelector;
import crl.monster.Monster;
import crl.player.Consts;
import sz.util.Debug;

import java.util.List;

public abstract class MonsterAI implements ActionSelector, Cloneable {
	private static final long serialVersionUID = 1L;
	protected List<RangedAttack> rangedAttacks;

	protected boolean checkIfEnemyInLevel(Monster aMonster) {
		return aMonster.getEnemy() != null && !aMonster.getLevel().getMonsters().contains(aMonster.getEnemy());
	}

	protected boolean checkIfEnemyOrCharmed(Monster aMonster) {
		return aMonster.getEnemy() != null || aMonster.hasCounter(Consts.C_MONSTER_CHARM);
	}

	/**
	 * Stare at your enemy or pick a enemy from nearby monsters
	 */
	protected int establishDirectionToMonster(Monster aMonster) {
		int directionToMonster = -1;
		if (aMonster.getEnemy() != null) {
			directionToMonster = aMonster.stareMonster(aMonster.getEnemy());
		} else {
			directionToMonster = aMonster.stareMonster();
		}
		return directionToMonster;
	}

	public void setRangedAttacks(List<RangedAttack> pRangedAttacks) {
		rangedAttacks = pRangedAttacks;
	}

	public ActionSelector derive() {
		try {
			return (ActionSelector) clone();
		} catch (Exception e) {
			Debug.byebye("Failed to clone MonsterAI " + getID());
			return null;
		}
	}
}

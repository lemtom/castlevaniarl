package crl.ai.monster;

import java.util.ArrayList;

import sz.util.Debug;
import crl.ai.ActionSelector;

public abstract class MonsterAI implements ActionSelector, Cloneable {
	protected ArrayList<RangedAttack> rangedAttacks;

	public void setRangedAttacks(ArrayList<RangedAttack> pRangedAttacks) {
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

package crl.ai.monster;

import crl.ai.ActionSelector;
import sz.util.Debug;

import java.util.ArrayList;

public abstract class MonsterAI implements ActionSelector, Cloneable {
private static final long serialVersionUID = 1L;
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

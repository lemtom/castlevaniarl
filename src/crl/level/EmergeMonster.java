package crl.level;

import sz.util.*;
import crl.action.*;
import crl.monster.*;

public class EmergeMonster extends Action {
private static final long serialVersionUID = 1L;
	private static EmergeMonster singleton = new EmergeMonster();
	
	public String getID(){
		return "EmergeMonster";
	}
	
	public void execute(){
		Level level = performer.getLevel();
		Emerger em = (Emerger) performer;
		Monster monster = em.getMonster();
		monster.setPosition(new Position(em.getPoint()));
		level.addMonster(monster);
		if (em.getMound() != null)
			level.destroyFeature(em.getMound());
	}

	public static EmergeMonster getAction(){
		return singleton;
	}
}
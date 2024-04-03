package crl.level;

import crl.action.*;
import crl.ui.effects.*;


public class PreemergeEffects extends Action {
private static final long serialVersionUID = 1L;
	private static PreemergeEffects singleton = new PreemergeEffects();
	
	public String getID(){
		return "Preemerge";
	}

	public void execute(){
		Level aLevel = performer.getLevel();
		Emerger em = (Emerger) performer;

        drawEffect(EffectFactory.getSingleton().createLocatedEffect(em.getPoint(), "SFX_MONSTER_CRAWLING"));
	}

	public static PreemergeEffects getAction(){
		return singleton;
	}
}
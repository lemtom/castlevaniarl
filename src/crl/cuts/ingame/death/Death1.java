package crl.cuts.ingame.death;

import crl.cuts.Unleasher;
import crl.game.Game;
import crl.level.Level;
import crl.monster.Monster;

public class Death1 extends Unleasher {
private static final long serialVersionUID = 1L;

	public void unleash(Level level, Game game) {
		Monster death = level.getMonsterByID("DEATH");
		//death.setSelector(SelectorFactory.getSelectorFactory().getSelector("NULL_SELECTOR"));
		level.addCounter("COUNTBACK_DEATH", 2);
		level.setFlag("DEATH_LEVEL", true);
		enabled = false;
	}

}

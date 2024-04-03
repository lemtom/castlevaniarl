package crl.cuts.ingame.badBelmont;

import crl.cuts.Unleasher;
import crl.game.Game;
import crl.level.Level;
import crl.monster.Monster;
import crl.ui.Display;

public class BadBelmont2 extends Unleasher {
private static final long serialVersionUID = 1L;

	public void unleash(Level level, Game game) {
		if (level.getPlayer().getFlag("SAVED_SOLIEYU")){
			enabled = false;
			return;
		}
		Monster belmont = level.getMonsterByID("BADBELMONT");
		if (belmont != null)
			return;
		Display.thus.showChat("BADSOLIEYU2", game);
		enabled = false;
	}
}
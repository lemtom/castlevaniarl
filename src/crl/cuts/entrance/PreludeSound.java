package crl.cuts.entrance;

import crl.cuts.Unleasher;
import crl.game.Game;
import crl.game.STMusicManagerNew;
import crl.level.Level;

public class PreludeSound extends Unleasher {

	private static final long serialVersionUID = 1L;

	public void unleash(Level level, Game game) {
		STMusicManagerNew.thus.stopMusic();
		STMusicManagerNew.thus.playKeyOnce("PRELUDE");
		enabled = false;
	}

}

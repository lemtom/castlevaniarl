package crl.levelgen;

import crl.game.CRLException;
import crl.level.Level;

public abstract class LockableLevelGenerator extends LevelGenerator {
	public abstract Level generateLevel(int xdim, int ydim, boolean locked) throws CRLException;
}

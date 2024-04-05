package crl.levelgen.featureCarve;

import java.util.ArrayList;
import java.util.List;

import sz.util.Position;
import sz.util.Util;
import crl.action.Action;
import crl.game.CRLException;
import crl.level.Cell;
import crl.level.Level;
import crl.level.MapCellFactory;
import crl.levelgen.LevelGenerator;

public class FeatureCarveGenerator extends LevelGenerator {
	private String[][] preLevel;
	private boolean[][] mask;
	private String[][] preLevelB;
	private boolean[][] maskB;
	private final ArrayList<Position> hotspots = new ArrayList<>();
	private final ArrayList<Position> roomHotspots = new ArrayList<>();
	private String solidCell;
	private String corridor;
	private List<Feature> levelFeatures;
	private String backExit;
	private String nextExit;

	public void initialize(List<Feature> levelFeatures, String solidCell, int xdim, int ydim, String corridor,
			String backExit, String nextExit) {
		preLevel = new String[xdim][ydim];
		mask = new boolean[xdim][ydim];
		preLevelB = new String[xdim][ydim];
		maskB = new boolean[xdim][ydim];
		this.solidCell = solidCell;
		this.corridor = corridor;
		this.levelFeatures = levelFeatures;
		this.backExit = backExit;
		this.nextExit = nextExit;
	}

	boolean checkCorridor = true;

	public void setCheckCorridor(boolean val) {
		checkCorridor = val;
	}

	private void save() {
		for (int i = 0; i < mask.length; i++) {
			System.arraycopy(mask[i], 0, maskB[i], 0, mask[i].length);
			System.arraycopy(preLevel[i], 0, preLevelB[i], 0, preLevel[i].length);
		}
	}

	private void rollBack() {
		for (int i = 0; i < mask.length; i++) {
			System.arraycopy(maskB[i], 0, mask[i], 0, mask[i].length);
			System.arraycopy(preLevelB[i], 0, preLevel[i], 0, preLevel[i].length);
		}
	}

	public Level generateLevel() throws CRLException {
		boolean checked = false;
		boolean placed = false;
		int i = 0;
		go: while (!checked) {
			ArrayList<Feature> pendingFeatures = new ArrayList<>(levelFeatures);
			hotspots.clear();
			roomHotspots.clear();
			fillLevelSolid();

			// Dig out a single room or a feature in the center of the map
			Position pos = new Position(getLevelWidth() / 2, getLevelHeight() / 2);
			Feature room = null;
			int direction = 0;
			boolean finished = false;

			while (!placed) {
				room = (Feature) Util.randomElementOf(pendingFeatures);
				direction = setRandomDirection();
				if (room.drawOverCanvas(preLevel, pos, direction, mask, hotspots)) {
					pendingFeatures.remove(room);
					if (pendingFeatures.isEmpty()) {
						finished = true;
						checked = true;
					}
					placed = true;
				} else {
					i++;
					if (i > 50000) {
						i = 0;
						continue go;
					}
				}
			}

			placed = false;
			save();
			boolean letsRollBack = false;
			while (!finished) {
				pos = (Position) Util.randomElementOf(hotspots);
				// Try to make a branch (corridor + room)
				int corridors = Util.rand(1, 3);
				int j = 0;
				while (j < corridors && !letsRollBack) {
					CorridotFeature corridorF = new CorridotFeature(Util.rand(4, 5), corridor);
					direction = setRandomDirection();
					if (corridorF.drawOverCanvas(preLevel, pos, direction, mask, roomHotspots)) {
						j++;
						pos = corridorF.getTip();
					} else {
						letsRollBack = true;
					}
				}
				if (letsRollBack) {
					rollBack();
					letsRollBack = false;
					continue;
				}

				room = (Feature) Util.randomElementOf(pendingFeatures);
				// direction is kept from the last corridor
				if (room.drawOverCanvas(preLevel, pos, direction, mask, hotspots)) {
					pendingFeatures.remove(room);
					save();
					if (pendingFeatures.isEmpty()) {
						finished = true;
						checked = true;
					}
					placed = true;
				} else {
					rollBack();
				}
				placed = false;

			}
		}

		Level ret = new Level();
		Cell[][][] cells = new Cell[1][][];
		cells[0] = renderLevel(preLevel);
		ret.setCells(cells);
		renderLevelFeatures(ret, preLevel);

		configureExitOrEntrance(ret, "_BACK", backExit);
		configureExitOrEntrance(ret, "_NEXT", nextExit);

		return ret;
	}

	/**
	 * Fill the level with solid element
	 */
	private void fillLevelSolid() {
		for (int x = 0; x < getLevelWidth(); x++) {
			for (int y = 0; y < getLevelHeight(); y++) {
				preLevel[x][y] = solidCell;
				mask[x][y] = false;
			}
		}
	}

	private void configureExitOrEntrance(Level level, String id, String exit) throws CRLException {
		Position pos = new Position(0, 0);
		while (true) {
			setRandomHeightAndWidth(pos);
			if (level.isExitPlaceable(pos) && (!checkCorridor || !preLevel[pos.x][pos.y].equals(corridor))) {
				level.addExit(pos, id);
				level.getCells()[pos.z][pos.x][pos.y] = MapCellFactory.getMapCellFactory().getMapCell(exit);
				break;
			}
		}
	}

	private void setRandomHeightAndWidth(Position entrance) {
		entrance.x = Util.rand(1, getLevelWidth() - 2);
		entrance.y = Util.rand(1, getLevelHeight() - 2);
	}

	private static int setRandomDirection() {
		switch (Util.rand(1, 4)) {
		case 1:
			return Action.UP;
		case 2:
			return Action.DOWN;
		case 3:
			return Action.LEFT;
		default:
			return Action.RIGHT;
		}
	}

	private int getLevelWidth() {
		return preLevel.length;
	}

	private int getLevelHeight() {
		return preLevel[0].length;
	}

}

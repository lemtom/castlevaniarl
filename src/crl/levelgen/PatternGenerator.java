package crl.levelgen;

import java.util.*;

import sz.util.*;
import crl.level.*;
import crl.monster.*;
import crl.game.*;
import crl.feature.*;

public class PatternGenerator extends LevelGenerator {
	private static PatternGenerator singleton = new PatternGenerator();

	private Map<String, String> charMap;
	private final ArrayList<AssignedFeature> assignedFeatures = new ArrayList<>();
	private LevelFeature baseFeature;
	private boolean hasBoss;

	public void resetFeatures() {
		assignedFeatures.clear();
		baseFeature = null;
		hasBoss = false;
	}

	public void assignFeature(LevelFeature lf, Position where) {
		assignedFeatures.add(new AssignedFeature(where, lf));
	}

	private Feature endFeature;

	public static PatternGenerator getGenerator() {
		return singleton;
	}

	public Level createLevel() throws CRLException {
		Debug.enterMethod(this, "createLevel");
		// draw the base feature
		StaticGenerator sg = StaticGenerator.getGenerator();
		sg.reset();
		sg.setCharMap(charMap);
		sg.setLevel(baseFeature.getALayout());
		Level ret = sg.createLevel();

		Cell[][][] cmap = ret.getCells();
		for (AssignedFeature af : assignedFeatures) {
			drawFeature(af.getFeature(), af.getPosition(), ret);
		}

		ret.setCells(cmap);

		if (!hasBoss) {
			int keysOnLevel = placeKeys(ret);
			if (endFeature != null)
				endFeature.setKeyCost(keysOnLevel);
		} else if (endFeature != null)
			endFeature.setKeyCost(1);
		Debug.exitMethod(ret);
		return ret;
	}

	protected void drawFeature(LevelFeature what, Position where, Level level) {
		Cell[][][] canvas = level.getCells();
		String[][] map = what.getALayout();
		for (int z = 0; z < map.length; z++)
			for (int y = 0; y < map[0].length; y++) {
				for (int x = 0; x < map[0][0].length(); x++) {
					if (map[z][y].charAt(x) == ' ') {
						continue;
					}
					determineCmds(where, level, canvas, z, y, x, charMap.get(map[z][y].charAt(x) + ""));
				}
			}
	}

	private void determineCmds(Position where, Level level, Cell[][][] canvas, int z, int y, int x,
			String charMapValue) {
		String[] cmds = charMapValue.split(" ");
		if (!cmds[0].equals("NOTHING"))
			try {
				canvas[where.z + z][x + where.x][y + where.y] = MapCellFactory.getMapCellFactory().getMapCell(cmds[0]);
			} catch (CRLException crle) {
				Debug.byebye("Exception creating the level " + crle);
			}
		if (cmds.length > 1) {
			determineCmd(where, level, canvas, z, y, x, cmds);
		}
	}

	private void determineCmd(Position where, Level level, Cell[][][] canvas, int z, int y, int x, String[] cmds) {
		switch (cmds[1]) {
		case "FEATURE":
			if (cmds.length < 4 || Util.chance(Integer.parseInt(cmds[3]))) {
				Feature vFeature = FeatureFactory.getFactory().buildFeature(cmds[2]);
				vFeature.setPosition(x + where.x, y + where.y, where.z + z);
				if (cmds.length > 4 && cmds[4].equals("COST")) {
					vFeature.setKeyCost(Integer.parseInt(cmds[5]));
				}
				level.addFeature(vFeature);
			}
			break;
		case "COST":
			canvas[where.z + z][x + where.x][y + where.y].setKeyCost(Integer.parseInt(cmds[2]));
			break;
		case "REMOVE_FEATURE":
			level.destroyFeature(level.getFeatureAt(new Position(where.x + x, where.y + y, where.z + z)));
			break;
		case "MONSTER":
			Monster toAdd = MonsterFactory.getFactory().buildMonster(cmds[2]);
			toAdd.setPosition(x + where.x, y + where.y, z + where.z);
			level.addMonster(toAdd);
			break;
		case "EXIT":
			level.addExit(new Position(x + where.x, y + where.y, z + where.z), cmds[2]);
			break;
		case "EOL":
			level.addExit(new Position(x + where.x, y + where.y, z + where.z), "_NEXT");
			endFeature = FeatureFactory.getFactory().buildFeature(cmds[2]);
			endFeature.setPosition(x + where.x, y + where.y, where.z + z);
			if (cmds.length > 3 && cmds[3].equals("COST")) {
				endFeature.setKeyCost(Integer.parseInt(cmds[4]));
			}
			level.addFeature(endFeature);
			break;
		}
	}

	public void setCharMap(Map<String, String> value) {
		charMap = value;
	}

	public void setBaseFeature(LevelFeature value) {
		baseFeature = value;
	}

	public boolean hasBoss() {
		return hasBoss;
	}

	public void setHasBoss(boolean hasBoss) {
		this.hasBoss = hasBoss;
	}
}
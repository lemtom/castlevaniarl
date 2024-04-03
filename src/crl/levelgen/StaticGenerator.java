package crl.levelgen;

import java.util.*;

import crl.level.*;
import crl.monster.*;
import crl.game.*;
import crl.feature.*;
import crl.item.*;
import crl.npc.*;

import sz.util.*;

public class StaticGenerator {
	private static StaticGenerator singleton = new StaticGenerator();
	private Map<String, String> charMap;
	private Map<String, String> inhabitantsMap;
	private String[][] level;
	private String[][] inhabitants;

	public void reset() {
		charMap = null;
		level = null;
		inhabitantsMap = null;
		inhabitants = null;
	}

	public static StaticGenerator getGenerator() {
		return singleton;
	}

	public void renderOverLevel(Level l, String[] map, Map<String, String> table, Position where) throws CRLException {
		Cell[][][] cmap = l.getCells();
		for (int y = 0; y < map.length; y++)
			for (int x = 0; x < map[0].length(); x++) {
				if (map[y].charAt(x) == ' ') {
					cmap[where.z][where.x + x][where.y + y] = MapCellFactory.getMapCellFactory().getMapCell("AIR");
					continue;
				}
				String iconic = table.get(map[y].charAt(x) + "");
				if (iconic == null) {
					Game.crash("renderOverLevel: " + map[y].charAt(x) + " not found on the level charMap",
							new Exception());
				} else {
					handleCmds(l, cmap, where.z, where.y + y, where.x + x, iconic, true);
				}

			}
	}

	public void renderOverLevel(Level l, String[][] map, Map<String, String> table, Position where)
			throws CRLException {
		Position runner = new Position(where);
		runner.z = 0;
		for (String[] strings : map) {
			renderOverLevel(l, strings, table, runner);
			runner.z++;
		}
	}

	public Level createLevel() throws CRLException {
		Debug.enterMethod(this, "createLevel");
		Level ret = new Level();
		ret.setDispatcher(new Dispatcher());
		Cell[][][] cmap = new Cell[level.length][level[0][0].length()][level[0].length];
		ret.setCells(cmap);
		loopOverCMap(ret, cmap);
		if (checkForInhabitants()) {
			createInhabitants(ret);
		}

		Debug.exitMethod(ret);
		return ret;
	}

	private void loopOverCMap(Level ret, Cell[][][] cmap) throws CRLException {
		for (int z = 0; z < level.length; z++)
			for (int y = 0; y < level[0].length; y++)
				for (int x = 0; x < level[0][0].length(); x++) {
					if (level[z][y].charAt(x) == ' ') {
						cmap[z][x][y] = MapCellFactory.getMapCellFactory().getMapCell("AIR");
						continue;
					}
					String iconic = charMap.get(level[z][y].charAt(x) + "");
					if (iconic == null) {
						Game.crash("mapchar " + level[z][y].charAt(x) + " not found on the level charMap",
								new Exception());
					} else {
						handleCmds(ret, cmap, z, y, x, iconic, false);
					}
				}
	}

	private boolean checkForInhabitants() {
		return inhabitantsMap != null && inhabitants != null;
	}

	private void handleCmds(Level ret, Cell[][][] cmap, int z, int y, int x, String iconic, boolean doom)
			throws CRLException {
		String[] cmds = iconic.split(" ");
		if (!cmds[0].equals("NOTHING")) {
			cmap[z][x][y] = MapCellFactory.getMapCellFactory().getMapCell(cmds[0]);
		}
		if (cmds.length > 1) {
			handleCmd(ret, z, y, x, cmds, doom);
		}
	}

	private void handleCmd(Level ret, int z, int y, int x, String[] cmds, boolean doom) {
		switch (cmds[1]) {
		case "FEATURE":
			if (cmds.length < 4 || Util.chance(Integer.parseInt(cmds[3]))) {
				ret.addFeature(generateFeature(z, y, x, cmds, 2), doom);
			}
			break;
		case "ITEM": {
			Item vItem = ItemFactory.getItemFactory().createItem(cmds[2]);
			if (vItem != null) {
				ret.addItem(new Position(x, y, z), vItem);
			}
			break;
		}
		case "WEAPON": {
			if (!doom) {
				Item vItem = ItemFactory.getItemFactory().createWeapon(cmds[2], cmds[3]);
				ret.addItem(new Position(x, y, z), vItem);
			}
			break;
		}
		case "MONSTER": {
			generateMonster(ret, z, y, x, cmds[2]);
			break;
		}
		case "NPC": {
			generateNPC(ret, z, y, x, cmds[2]);
			break;
		}
		case "MERCHANT": {
			if (!doom) {
				NPC toAdd = NPCFactory.getFactory().buildMerchant(Integer.parseInt(cmds[2]));
				toAdd.setLevel(ret);
				toAdd.setPosition(x, y, z);
				ret.addActor(toAdd);
			}
			break;
		}
		case "EXIT":
			ret.addExit(new Position(x, y, z), cmds[2]);
			break;
		case "EXIT_FEATURE":
			ret.addExit(new Position(x, y, z), cmds[2]);
			ret.addFeature(generateFeature(z, y, x, cmds, 3));
			break;
		case "EOL":
			ret.addExit(new Position(x, y, z), "_NEXT");
			ret.addFeature(generateEndFeature(z, y, x, cmds));
			break;
		default:
			break;
		}
	}

	private Feature generateEndFeature(int z, int y, int x, String[] cmds) {
		Feature feature = FeatureFactory.getFactory().buildFeature(cmds[2]);
		feature.setPosition(x, y, z);
		if (cmds.length > 3 && cmds[3].equals("COST")) {
			feature.setKeyCost(Integer.parseInt(cmds[4]));
		}
		return feature;
	}

	private Feature generateFeature(int z, int y, int x, String[] cmds, int index) {
		Feature feature = FeatureFactory.getFactory().buildFeature(cmds[index]);
		feature.setPosition(x, y, z);
		if (cmds.length > 4 && cmds[index + 2].equals("COST")) {
			feature.setKeyCost(Integer.parseInt(cmds[index + 3]));
		}
		return feature;
	}

	private void createInhabitants(Level ret) {
		for (int z = 0; z < level.length; z++)
			for (int y = 0; y < level[0].length; y++)
				for (int x = 0; x < level[0][0].length(); x++) {
					if (level[z][y].charAt(x) == ' ' || inhabitantsMap.get(inhabitants[z][y].charAt(x) + "") == null)
						continue;
					String[] cmds = inhabitantsMap.get(inhabitants[z][y].charAt(x) + "").split(" ");
					switch (cmds[0]) {
					case "MONSTER": {
						generateMonster(ret, z, y, x, cmds[1]);
						break;
					}
					case "NPC": {
						generateNPC(ret, z, y, x, cmds[1]);
						break;
					}
					case "MERCHANT": {
						NPC toAdd = NPCFactory.getFactory().buildMerchant(Integer.parseInt(cmds[1]));
						toAdd.setPosition(x, y, z);
						ret.addActor(toAdd);
						break;
					}
					default:
						break;
					}
				}
	}

	private void generateNPC(Level ret, int z, int y, int x, String id) {
		NPC toAdd = NPCFactory.getFactory().buildNPC(id);
		toAdd.setPosition(x, y, z);
		toAdd.setLevel(ret);
		ret.addActor(toAdd);
	}

	private void generateMonster(Level ret, int z, int y, int x, String id) {
		Monster toAdd = MonsterFactory.getFactory().buildMonster(id);
		toAdd.setPosition(x, y, z);
		ret.addMonster(toAdd);
	}

	public void setCharMap(Map<String, String> value) {
		charMap = value;
	}

	public void setInhabitantsMap(Map<String, String> value) {
		inhabitantsMap = value;
	}

	public void setLevel(String[][] value) {
		level = value;
	}

	public void setInhabitants(String[][] value) {
		inhabitants = value;
	}

	public void setFlatLevel(String[] value) {
		level = new String[1][];
		level[0] = value;
	}

	/**
	 * Place the magic Keys
	 * 
	 */
	protected int placeKeys(Level ret) {
		Debug.enterMethod(this, "placeKeys");
		int keys = Util.rand(1, 4);
		Position tempPosition = new Position(0, 0);
		for (int i = 0; i < keys; i++) {
			int keyx = Util.rand(1, ret.getWidth() - 1);
			int keyy = Util.rand(1, ret.getHeight() - 1);
			int keyz = Util.rand(0, ret.getDepth() - 1);
			tempPosition.x = keyx;
			tempPosition.y = keyy;
			tempPosition.z = keyz;
			if (ret.isWalkable(tempPosition) && !ret.getMapCell(tempPosition).isWater()) {
				Feature keyf = FeatureFactory.getFactory().buildFeature("KEY");
				keyf.setPosition(tempPosition.x, tempPosition.y, tempPosition.z);
				ret.addFeature(keyf);
			} else {
				i--;
			}
		}
		Debug.exitMethod(keys);
		return keys;

	}

}
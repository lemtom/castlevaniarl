package crl.levelgen.patterns;

import java.util.HashMap;
import java.util.Map;

import crl.cuts.Unleasher;
import crl.levelgen.MonsterSpawnInfo;
import crl.levelgen.StaticGenerator;

public abstract class StaticPattern {
	protected HashMap<String, String> charMap = new HashMap<>();
	protected String[][] cellMap;
	protected HashMap<String, String> inhabitantsMap = new HashMap<>();
	protected String[][] inhabitants;
	protected MonsterSpawnInfo[] spawnInfo;
	protected Unleasher[] unleashers;

	public boolean isRutinary() {
		return false;
	}

	public Map<String, String> getCharMap() {
		return charMap;
	}

	public String[][] getCellMap() {
		return cellMap;
	}

	public Map<String, String> getInhabitantsMap() {
		return inhabitantsMap;
	}

	public String[][] getInhabitants() {
		return inhabitants;
	}

	public MonsterSpawnInfo[] getDwellers() {
		return null;
	}

	public abstract String getDescription();

	public abstract String getMusicKeyMorning();

	public abstract String getMusicKeyNoon();

	public abstract String getMapKey();

	public boolean isHaunted() {
		return false;
	}

	public String getBoss() {
		return null;
	}

	public sz.util.Position getBossPosition() {
		return null;
	}

	public MonsterSpawnInfo[] getSpawnInfo() {
		return spawnInfo;
	}

	public boolean isHostageSafe() {
		return false;
	}

	public void setup(StaticGenerator gen) {
		gen.reset();
		gen.setCharMap(getCharMap());
		gen.setLevel(getCellMap());
		gen.setInhabitantsMap(getInhabitantsMap());
		gen.setInhabitants(getInhabitants());
	}

	public Unleasher[] getUnleashers() {
		return unleashers;
	}
}

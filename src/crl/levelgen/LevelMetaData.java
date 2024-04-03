package crl.levelgen;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class LevelMetaData implements Serializable {
private static final long serialVersionUID = 1L;
	private String levelID;
	private int levelNumber = -1;
	private ArrayList<String> exits = new ArrayList<>();
	private HashMap<String, String> hexits = new HashMap<>();

	public List<String> getExits() {
		return exits;
	}

	public void addExits(String exit, String exitID) {
		exits.add(exit);
		hexits.put(exitID, exit);
	}

	public String getLevelID() {
		return levelID;
	}

	public void setLevelID(String levelID) {
		this.levelID = levelID;
	}

	public int getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}

	public String getExit(int number) {
		return exits.get(number);
	}

	public String getExit(String exitID) {
		return hexits.get(exitID);
	}
}

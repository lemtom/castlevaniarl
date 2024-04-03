package crl.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sz.csi.textcomponents.MenuItem;
import sz.util.Debug;
import sz.util.FileUtil;
import sz.util.SerializableChecker;
import crl.item.ItemDefinition;
import crl.player.Equipment;
import crl.player.GameSessionInfo;
import crl.player.HiScore;
import crl.player.MonsterDeath;
import crl.player.Player;
import crl.ui.UserInterface;

public class GameFiles {
	public static HiScore[] loadScores(String hiscorefile) {
		Debug.enterStaticMethod("GameFiles", "loadScores");
		HiScore[] ret = new HiScore[10];
		try {
			BufferedReader lectorArchivo = FileUtil.getReader(hiscorefile);
			for (int i = 0; i < 10; i++) {
				String line = lectorArchivo.readLine();
				String[] regs = line.split(";");
				if (regs == null) {
					Game.crash("Invalid or corrupt hiscore table");
				}
				HiScore x = new HiScore();
				x.setName(regs[0]);
				x.setPlayerClass(regs[1]);
				x.setScore(Integer.parseInt(regs[2]));
				x.setDate(regs[3]);
				x.setTurns(regs[4]);
				x.setDeathString(regs[5]);
				x.setDeathLevel(Integer.parseInt(regs[6]));
				ret[i] = x;
			}
			Debug.exitMethod(ret);
			return ret;
		} catch (IOException ioe) {
			Game.crash("Invalid or corrupt hiscore table", ioe);
		}
		return null;
	}

	public static void saveHiScore(Player player, String hiscoreFile) {
		Debug.enterStaticMethod("GameFiles", "saveHiscore");
		int score = player.getScore();
		String name = player.getName();
		String playerClass = "NONE";
		switch (player.getPlayerClass()) {
		case Player.CLASS_INVOKER:
			playerClass = "INV";
			break;
		case Player.CLASS_KNIGHT:
			playerClass = "KNG";
			break;
		case Player.CLASS_MANBEAST:
			playerClass = "MNB";
			break;
		case Player.CLASS_RENEGADE:
			playerClass = "RNG";
			break;
		case Player.CLASS_VAMPIREKILLER:
			playerClass = "VKL";
			break;
		case Player.CLASS_VANQUISHER:
			playerClass = "VAN";
			break;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String now = sdf.format(new Date());

		HiScore[] scores = loadScores(hiscoreFile);

		try {
			BufferedWriter fileWriter = FileUtil.getWriter(hiscoreFile);
			for (int i = 0; i < 10; i++) {
				if (score > scores[i].getScore()) {
					fileWriter.write(name + ";" + playerClass + ";" + score + ";" + now + ";"
							+ player.getGameSessionInfo().getTurns() + ";"
							+ player.getGameSessionInfo().getShortDeathString() + ";"
							+ player.getGameSessionInfo().getDeathLevel());
					fileWriter.newLine();
					score = -1;
					if (i == 9)
						break;
				}
				fileWriter.write(scores[i].getName() + ";" + scores[i].getPlayerClass() + ";" + scores[i].getScore()
						+ ";" + scores[i].getDate() + ";" + scores[i].getTurns() + ";" + scores[i].getDeathString()
						+ ";" + scores[i].getDeathLevel());
				fileWriter.newLine();
			}
			fileWriter.close();
			Debug.exitMethod();
		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
			Game.crash("Invalid or corrupt hiscore table", ioe);
		}
	}

	public static void saveMemorialFile(Player player) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			String now = sdf.format(new Date());
			BufferedWriter fileWriter = FileUtil.getWriter("memorials/" + player.getName() + "(" + now + ").life");
			GameSessionInfo gsi = player.getGameSessionInfo();
			gsi.setDeathLevelDescription(player.getLevel().getDescription());

			String heshe = player.getPronoun();

			fileWriter.write("/-----------------------------------");
			fileWriter.newLine();
			fileWriter.write(" CastlevaniaRL" + Game.getVersion() + " Post Mortem");
			fileWriter.newLine();
			fileWriter.write(" -----------------------------------/");
			fileWriter.newLine();
			fileWriter.newLine();
			fileWriter.newLine();
			fileWriter.write(player.getPlot() + ", " + player.getDescription() + " journeys to the cursed castle.");
			fileWriter.newLine();
			fileWriter.newLine();
			fileWriter.write(player.getName() + ", the " + player.getClassString() + ", " + gsi.getDeathString()
					+ " on the " + gsi.getDeathLevelDescription() + " (Level " + gsi.getDeathLevel() + ")...");
			fileWriter.newLine();
			fileWriter.write(heshe + " survived for " + gsi.getTurns() + " turns and scored " + player.getScore()
					+ " points, collecting a total of " + gsi.getGoldCount() + " gold.");
			fileWriter.newLine();
			fileWriter.newLine();
			fileWriter.write(heshe + " was able to use the following skills:");
			fileWriter.newLine();
			List<MenuItem> skills = player.getAvailableSkills();
			for (MenuItem skill : skills) {
				fileWriter.write(skill.getMenuDescription());
				fileWriter.newLine();
			}

			fileWriter.newLine();
			fileWriter.write(heshe + " had the following proficiences:");
			fileWriter.newLine();
			fileWriter.write("Hand to hand combat "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_UNARMED)]);
			fileWriter.newLine();
			fileWriter.write("Daggers             "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_DAGGERS)]);
			fileWriter.newLine();
			fileWriter.write("Swords              "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_SWORDS)]);
			fileWriter.newLine();
			fileWriter.write("Spears              "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_SPEARS)]);
			fileWriter.newLine();
			fileWriter.write(
					"Whips               " + UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_WHIPS)]);
			fileWriter.newLine();
			fileWriter.write(
					"Maces and Flails    " + UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_MACES)]);
			fileWriter.newLine();
			fileWriter.write("Pole Weapons        "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_STAVES)]);
			fileWriter.newLine();
			fileWriter.write(
					"Rings               " + UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_RINGS)]);
			fileWriter.newLine();
			fileWriter.write("Hand thrown items   "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_PROJECTILES)]);
			fileWriter.newLine();
			fileWriter.write(
					"Bows / XBows        " + UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_BOWS)]);
			fileWriter.newLine();
			fileWriter.write("Missile machinery   "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_PISTOLS)]);
			fileWriter.newLine();
			fileWriter.write("Shields             "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_SHIELD)]);
			fileWriter.newLine();

			fileWriter.newLine();
			List<String> history = gsi.getHistory();
			for (String s : history) {
				fileWriter.write(heshe + " " + s);
				fileWriter.newLine();
			}
			fileWriter.newLine();
			fileWriter.write(heshe + " took " + gsi.getTotalDeathCount() + " souls to the other world");
			fileWriter.newLine();

			int i = 0;
			for (Entry<String, MonsterDeath> entry : gsi.getDeathCount().entrySet()) {
				MonsterDeath mons = entry.getValue();
				fileWriter.write(mons.getTimes() + " " + mons.getMonsterDescription());
				fileWriter.newLine();
				i++;
			}
			fileWriter.newLine();
			fileWriter.write("-- Final Stats --");
			fileWriter.newLine();
			fileWriter.newLine();
			fileWriter.write(player.getName() + " the level " + player.getPlayerLevel() + " " + player.getClassString()
					+ " " + player.getStatusString());
			fileWriter.newLine();
			fileWriter.write("Sex: " + (player.getSex() == Player.MALE ? "M" : "F"));
			fileWriter.newLine();
			fileWriter.write("Hits: " + player.getHits() + "/" + player.getHitsMax() + " Hearts: " + player.getHearts()
					+ "/" + player.getHeartsMax() + " Gold: " + player.getGold() + " Keys: " + player.getKeys());
			fileWriter.newLine();
			fileWriter.write("Carrying: " + player.getItemCount() + "/" + player.getCarryMax());
			fileWriter.newLine();
			fileWriter.write("Attack: +" + player.getAttack());
			fileWriter.newLine();
			fileWriter.write("Soul Power: +" + player.getSoulPower());
			fileWriter.newLine();
			fileWriter.write("Evade: " + player.getEvadeChance() + "%");
			fileWriter.newLine();
			fileWriter.write("Combat: " + (50 - player.getAttackCost()));
			fileWriter.newLine();
			fileWriter.write("Invokation: " + (50 - player.getCastCost()));
			fileWriter.newLine();
			fileWriter.write("Movement: " + (50 - player.getWalkCost()));
			fileWriter.newLine();

			fileWriter.write("Experience: " + player.getXp() + "/" + player.getNextXP());
			fileWriter.newLine();
			fileWriter.newLine();

			List<MenuItem> inventory = player.getInventory();
			fileWriter.newLine();
			fileWriter.write("-- Inventory --");
			fileWriter.newLine();
			fileWriter.write("Weapon    " + player.getEquipedWeaponDescription());
			fileWriter.newLine();
			fileWriter.write("Secondary " + player.getSecondaryWeaponDescription());
			fileWriter.newLine();
			fileWriter.write("Armor     " + player.getArmorDescription());
			fileWriter.newLine();
			fileWriter.write("Shield    " + player.getAccDescription());
			fileWriter.newLine();
			fileWriter.newLine();

			for (MenuItem menuItem : inventory) {
				Equipment element = (Equipment) menuItem;
				fileWriter.write(element.getQuantity() + " - " + element.getMenuDescription());
				fileWriter.newLine();
			}
			fileWriter.newLine();
			fileWriter.write("-- Last Messages --");
			fileWriter.newLine();
			List<String> messages = UserInterface.getUI().getMessageBuffer();
			for (String message : messages) {
				fileWriter.write(message);
				fileWriter.newLine();
			}

			fileWriter.close();
		} catch (IOException ioe) {
			Game.crash("Error writing the memorial file", ioe);
		}

	}

	static class SaveFilenameFilter implements FilenameFilter {
		private Player p;

		public SaveFilenameFilter(Player p) {
			this.p = p;
		}

		public boolean accept(File dir, String name) {
			return name.startsWith(p.getName());
		}
	}

	public static void saveGame(Game g, Player p) {
		// Delete previous saves
		File saveDirectory = new File("savegame");
		File[] previousSaves = saveDirectory.listFiles(new SaveFilenameFilter(p));
		for (File file : previousSaves) {
			file.delete();
		}
		String filename = "savegame/" + p.getName() + ", a Lv" + p.getPlayerLevel() + " " + p.getClassString() + ".sav";
		p.setSelector(null);
		try (SerializableChecker sc = new SerializableChecker();
				ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(Paths.get(filename)))) {
			sc.writeObject(g);
			os.writeObject(g);
		} catch (IOException ioe) {
			Game.crash("Error saving the game", ioe);
		}

	}

	public static void permadeath(Player p) {
		File saveDirectory = new File("savegame");
		File[] previousSaves = saveDirectory.listFiles(new SaveFilenameFilter(p));
		for (File file : previousSaves) {
			file.delete();
		}

	}

	public static void saveChardump(Player player) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss");
			String now = sdf.format(new Date());
			BufferedWriter fileWriter = FileUtil
					.getWriter("memorials/" + player.getName() + " {Alive}(" + now + ").life");
			GameSessionInfo gsi = player.getGameSessionInfo();
			gsi.setDeathLevelDescription(player.getLevel().getDescription());

			String heshe = player.getPronoun();

			fileWriter.write("/-----------------------------------");
			fileWriter.newLine();
			fileWriter.write(" CastlevaniaRL" + Game.getVersion() + " Post Mortem");
			fileWriter.newLine();
			fileWriter.write(" -----------------------------------/");
			fileWriter.newLine();
			fileWriter.newLine();
			fileWriter.newLine();
			fileWriter.write(player.getPlot() + ", " + player.getDescription() + " journeys to the cursed castle.");
			fileWriter.newLine();
			fileWriter.newLine();
			fileWriter.write(player.getName() + ", the " + player.getClassString() + ", survives on the "
					+ player.getLevel().getDescription() + " (Level " + player.getLevel().getLevelNumber() + ")...");
			fileWriter.newLine();
			fileWriter.write(heshe + " has survived for " + gsi.getTurns() + " turns and has scored "
					+ player.getScore() + " points, collecting a total of " + gsi.getGoldCount() + " gold.");
			fileWriter.newLine();
			fileWriter.newLine();
			fileWriter.write(heshe + " is able to use the following skills:");
			fileWriter.newLine();
			List<MenuItem> skills = player.getAvailableSkills();
			for (MenuItem skill : skills) {
				fileWriter.write(skill.getMenuDescription());
				fileWriter.newLine();
			}

			fileWriter.newLine();
			fileWriter.write(heshe + " has the following profficiences:");
			fileWriter.newLine();
			fileWriter.write("Hand to hand combat "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_UNARMED)]);
			fileWriter.newLine();
			fileWriter.write("Daggers             "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_DAGGERS)]);
			fileWriter.newLine();
			fileWriter.write("Swords              "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_SWORDS)]);
			fileWriter.newLine();
			fileWriter.write("Spears              "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_SPEARS)]);
			fileWriter.newLine();
			fileWriter.write(
					"Whips               " + UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_WHIPS)]);
			fileWriter.newLine();
			fileWriter.write(
					"Maces and Flails    " + UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_MACES)]);
			fileWriter.newLine();
			fileWriter.write("Pole Weapons        "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_STAVES)]);
			fileWriter.newLine();
			fileWriter.write(
					"Rings               " + UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_RINGS)]);
			fileWriter.newLine();
			fileWriter.write("Hand thrown items   "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_PROJECTILES)]);
			fileWriter.newLine();
			fileWriter.write(
					"Bows / XBows        " + UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_BOWS)]);
			fileWriter.newLine();
			fileWriter.write("Missile machinery   "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_PISTOLS)]);
			fileWriter.newLine();
			fileWriter.write("Shields             "
					+ UserInterface.verboseSkills[player.weaponSkill(ItemDefinition.CAT_SHIELD)]);
			fileWriter.newLine();

			fileWriter.newLine();
			List<String> history = gsi.getHistory();
			for (String s : history) {
				fileWriter.write(heshe + " " + s);
				fileWriter.newLine();
			}
			fileWriter.newLine();
			fileWriter.write(heshe + " has taken " + gsi.getTotalDeathCount() + " souls to the other world");
			fileWriter.newLine();

			int i = 0;
			for (Entry<String, MonsterDeath> entry : gsi.getDeathCount().entrySet()) {
				MonsterDeath mons = entry.getValue();
				fileWriter.write(mons.getTimes() + " " + mons.getMonsterDescription());
				fileWriter.newLine();

				i++;
			}
			fileWriter.newLine();
			fileWriter.write("-- Current Stats --");
			fileWriter.newLine();
			fileWriter.newLine();
			fileWriter.write(player.getName() + " the level " + player.getPlayerLevel() + " " + player.getClassString()
					+ " " + player.getStatusString());
			fileWriter.newLine();
			fileWriter.write("Sex: " + (player.getSex() == Player.MALE ? "M" : "F"));
			fileWriter.newLine();
			fileWriter.write("Hits: " + player.getHits() + "/" + player.getHitsMax() + " Hearts: " + player.getHearts()
					+ "/" + player.getHeartsMax() + " Gold: " + player.getGold() + " Keys: " + player.getKeys());
			fileWriter.newLine();
			fileWriter.write("Carrying: " + player.getItemCount() + "/" + player.getCarryMax());
			fileWriter.newLine();
			fileWriter.write("Attack: +" + player.getAttack());
			fileWriter.newLine();
			fileWriter.write("Soul Power: +" + player.getSoulPower());
			fileWriter.newLine();
			fileWriter.write("Evade: " + player.getEvadeChance() + "%");
			fileWriter.newLine();
			fileWriter.write("Combat: " + (50 - player.getAttackCost()));
			fileWriter.newLine();
			fileWriter.write("Invokation: " + (50 - player.getCastCost()));
			fileWriter.newLine();
			fileWriter.write("Movement: " + (50 - player.getWalkCost()));
			fileWriter.newLine();

			fileWriter.write("Experience: " + player.getXp() + "/" + player.getNextXP());
			fileWriter.newLine();
			fileWriter.newLine();

			List<MenuItem> inventory = player.getInventory();
			fileWriter.newLine();
			fileWriter.write("-- Inventory --");
			fileWriter.newLine();
			fileWriter.write("Weapon " + player.getEquipedWeaponDescription());
			fileWriter.newLine();
			fileWriter.write("Secondary " + player.getSecondaryWeaponDescription());
			fileWriter.newLine();
			fileWriter.write("Armor     " + player.getArmorDescription());
			fileWriter.newLine();
			fileWriter.write("Shield    " + player.getAccDescription());
			fileWriter.newLine();
			fileWriter.newLine();

			for (MenuItem menuItem : inventory) {
				Equipment element = (Equipment) menuItem;
				fileWriter.write(element.getQuantity() + " - " + element.getMenuDescription());
				fileWriter.newLine();
			}
			fileWriter.newLine();
			fileWriter.write("-- Latest Messages --");
			fileWriter.newLine();
			List<String> messages = UserInterface.getUI().getMessageBuffer();
			for (String message : messages) {
				fileWriter.write(message);
				fileWriter.newLine();
			}

			fileWriter.close();
		} catch (IOException ioe) {
			Game.crash("Error writing the chardump", ioe);
		}
	}

	public static Map<String, MonsterRecord> getMonsterRecord() {
		HashMap<String, MonsterRecord> ret = new HashMap<>();
		try {
			BufferedReader lectorArchivo = FileUtil.getReader("graveyard");
			String line = lectorArchivo.readLine();
			while (line != null) {
				String[] regs = line.split(",");
				MonsterRecord x = new MonsterRecord();
				x.setMonsterID(regs[0]);
				x.setKilled(Integer.parseInt(regs[1]));
				x.setKillers(Integer.parseInt(regs[2]));
				ret.put(x.getMonsterID(), x);
				line = lectorArchivo.readLine();
			}
			return ret;
		} catch (IOException ioe) {
			Game.crash("Invalid or corrupt graveyard", ioe);
		} catch (NumberFormatException nfe) {
			Game.crash("Corrupt graveyard", nfe);
		}
		return null;
	}

	public static void updateGraveyard(Map<String, MonsterRecord> graveyard, GameSessionInfo gsi) {
		Map<String, MonsterDeath> session = gsi.getDeathCount();

		for (String monsterID : session.keySet()) {
			MonsterDeath deaths = gsi.getDeathCount().get(monsterID);
			MonsterRecord monsterRecord = graveyard.get(monsterID);
			if (monsterRecord == null) {
				monsterRecord = new MonsterRecord();
				monsterRecord.setMonsterID(monsterID);
				monsterRecord.setKilled(deaths.getTimes());
				graveyard.put(monsterID, monsterRecord);
			} else {
				monsterRecord.setKilled(monsterRecord.getKilled() + deaths.getTimes());
			}
		}
		if (gsi.getKillerMonster() != null) {
			MonsterRecord record = graveyard.get(gsi.getKillerMonster().getID());
			if (record == null) {
				record = new MonsterRecord();
				record.setMonsterID(gsi.getKillerMonster().getID());
				record.setKillers(1);
				graveyard.put(gsi.getKillerMonster().getID(), record);
			} else {
				record.setKillers(record.getKillers() + 1);
			}
		}

		// Save to file
		try {
			BufferedWriter fileWriter = FileUtil.getWriter("graveyard");
			for (Entry<String, MonsterRecord> entry : graveyard.entrySet()) {
				MonsterRecord monsterRecord = entry.getValue();
				fileWriter.write(monsterRecord.getMonsterID() + "," + monsterRecord.getKilled() + ","
						+ monsterRecord.getKillers());
				fileWriter.newLine();
			}
			fileWriter.close();
		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
			Game.crash("Invalid or corrupt graveyard", ioe);
		}

	}
}

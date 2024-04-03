package crl;

import static crl.Helper.i;
import static crl.Helper.readKeyString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import sz.SystemInterface;
import sz.csi.ConsoleSystemInterface;
import sz.csi.jcurses.JCursesConsoleInterface;
import sz.csi.wswing.WSwingConsoleInterface;
import sz.midi.STMidiPlayer;

import crl.action.*;
import crl.conf.console.data.CharCuts;
import crl.conf.console.data.CharEffects;
import crl.conf.gfx.data.GFXConfiguration;
import crl.conf.gfx.data.GFXCuts;
import crl.conf.gfx.data.GFXEffects;
import crl.game.CRLException;
import crl.game.Game;
import crl.game.GameFiles;
import crl.game.GameVersion;
import crl.game.MonsterRecord;
import crl.game.PlayerGenerator;
import crl.game.SFXManager;
import crl.game.STMusicManagerNew;
import crl.player.Player;
import crl.ui.CommandListener;
import crl.ui.Display;
import crl.ui.UISelector;
import crl.ui.UserAction;
import crl.ui.UserCommand;
import crl.ui.UserInterface;
import crl.ui.consoleUI.CharDisplay;
import crl.ui.consoleUI.CharPlayerGenerator;
import crl.ui.consoleUI.ConsoleUISelector;
import crl.ui.consoleUI.ConsoleUserInterface;
import crl.ui.consoleUI.effects.CharEffectFactory;
import crl.ui.effects.Effect;
import crl.ui.effects.EffectFactory;
import crl.ui.graphicsUI.GFXDisplay;
import crl.ui.graphicsUI.GFXPlayerGenerator;
import crl.ui.graphicsUI.GFXUISelector;
import crl.ui.graphicsUI.GFXUserInterface;
import crl.ui.graphicsUI.SwingSystemInterface;
import crl.ui.graphicsUI.effects.GFXEffectFactory;

public class Main {
	private static final int JCURSES_CONSOLE = 0;
	private static final int SWING_GFX = 1;
	private static final int SWING_CONSOLE = 2;
	private static UserInterface ui;
	private static UISelector uiSelector;

	private static Game currentGame;
	private static boolean createNew = true;
	private static int mode;

	public static String getConfigurationVal(String key) {
		return configuration.getProperty(key);
	}

	private static void init() {
		if (createNew) {
			System.out.println("CastlevaniaRL " + Game.getVersion());
			System.out.println("by slashie ~ 2005-2007, 2010, 2024");
			System.out.println("Reading configuration");
			readConfiguration();
			firstInitializations();
			STMusicManagerNew.initManager();
			if (configuration.getProperty("enableSound") != null
					&& configuration.getProperty("enableSound").equals("true")) { // Sound
				if (configuration.getProperty("enableMusic") == null
						|| !configuration.getProperty("enableMusic").equals("true")) { // Music
					STMusicManagerNew.thus.setEnabled(false);
				} else {
					System.out.println("Initializing Midi Sequencer");
					try {
						STMidiPlayer.sequencer = MidiSystem.getSequencer();
						STMidiPlayer.sequencer.open();

					} catch (MidiUnavailableException mue) {
						Game.addReport("Midi device unavailable");
						System.out.println("Midi Device Unavailable");
						STMusicManagerNew.thus.setEnabled(false);
						return;
					}
					initializeMusicManager();
				}
				SFXManager.setEnabled(configuration.getProperty("enableSFX") != null
						&& configuration.getProperty("enableSFX").equals("true"));
			}
			Player.initializeWhips("LEATHER_WHIP", "CHAIN_WHIP", "VKILLERW", "THORN_WHIP", "FLAME_WHIP", "LIT_WHIP");

			checkForUpdates();
			createNew = false;
		}
	}

	private static void initializeMusicManager() {
		System.out.println("Initializing Music Manager");

		Enumeration<Object> keys = configuration.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith("mus_")) {
				String music = key.substring(4);
				STMusicManagerNew.thus.addMusic(music, configuration.getProperty(key));
			}
		}
		STMusicManagerNew.thus.setEnabled(true);
	}

	private static void firstInitializations() {
		GFXConfiguration gfxConfiguration = null;
		try {

			switch (mode) {
			case SWING_GFX:
				gfxConfiguration = new GFXConfiguration();
				gfxConfiguration.LoadConfiguration(uiConfiguration);
				System.out.println("Initializing Graphics Appearances");
				Helper.initializeGAppearances(gfxConfiguration);
				break;
			case JCURSES_CONSOLE:
			case SWING_CONSOLE:
				System.out.println("Initializing Char Appearances");
				Helper.initializeCAppearances();
				break;
			}
			System.out.println("Initializing Action Objects");
			Helper.initializeActions();
			Helper.initializeSelectors();
			System.out.println("Loading Data");
			Helper.initializeCells();
			Helper.initializeItems();
			Helper.initializeMonsters();
			Helper.initializeNPCs();
			Helper.initializeFeatures();
			Helper.initializeSmartFeatures();
			switch (mode) {
			case SWING_GFX:
				initializeSwing(gfxConfiguration);
				break;
			case JCURSES_CONSOLE:
				initializeJcursesConsole();
				break;
			case SWING_CONSOLE:
				initializeSwingConsole();
			}

		} catch (CRLException crle) {
			crash("Error initializing", crle);
		}
	}

	private static void checkForUpdates() {
		try {
			GameVersion latestVersion = GameVersion.getLatestVersion();
			if (latestVersion == null) {
				ui.showVersionDialog("Error checking for updates.", true);
				System.err.println("null latest version");
			} else if (latestVersion.equals(GameVersion.getCurrentVersion())) {
				ui.showVersionDialog("You are using the latest available version", false);
			} else {
				ui.showVersionDialog("A newer version, " + latestVersion.getCode() + " from "
						+ latestVersion.getFormattedDate() + " is available!", true);
			}
		} catch (IOException ex) {
			ui.showVersionDialog("Error checking for updates.", true);
			ex.printStackTrace();
		}
	}

	private static void initializeJcursesConsole() {
		System.out.println("Initializing JCurses System Interface");
		initializeConsole(setJcursesCsi());
	}

	private static void initializeSwingConsole() {
		System.out.println("Initializing Swing Console System Interface");
		initializeConsole(new WSwingConsoleInterface());
	}

	private static void initializeConsole(ConsoleSystemInterface csi) {
		System.out.println("Initializing Console User Interface");
		UserInterface.setSingleton(new ConsoleUserInterface());
		CharCuts.initializeSingleton();
		setGenerators(csi, new CharDisplay(csi), new CharPlayerGenerator(csi), new CharEffects().getEffects(), false);
	}

	private static void initializeSwing(GFXConfiguration gfxConfiguration) {
		System.out.println("Initializing Swing GFX System Interface");
		SwingSystemInterface si = new SwingSystemInterface(gfxConfiguration);
		System.out.println("Initializing Swing GFX User Interface");
		UserInterface.setSingleton(new GFXUserInterface(gfxConfiguration));
		GFXCuts.initializeSingleton();
		setGenerators(si, new GFXDisplay(si, uiConfiguration, gfxConfiguration),
				new GFXPlayerGenerator(si, gfxConfiguration), new GFXEffects(gfxConfiguration).getEffects(), true);
	}

	private static void setGenerators(SystemInterface si, Display display, PlayerGenerator playerGenerator,
			Effect[] effects, boolean isGraphical) {
		Display.thus = display;
		PlayerGenerator.thus = playerGenerator;
		if (isGraphical) {
			EffectFactory.setSingleton(new GFXEffectFactory());
			((GFXEffectFactory) EffectFactory.getSingleton()).setEffects(effects);
		} else {
			EffectFactory.setSingleton(new CharEffectFactory());
			((CharEffectFactory) EffectFactory.getSingleton()).setEffects(effects);
		}
		ui = UserInterface.getUI();
		initializeUI(si);
	}

	private static ConsoleSystemInterface setJcursesCsi() {
		ConsoleSystemInterface csi = null;
		try {
			csi = new JCursesConsoleInterface();
		} catch (ExceptionInInitializerError eiie) {
			crash("Fatal Error Initializing JCurses", eiie);
			eiie.printStackTrace();
			System.exit(-1);
		}
		return csi;
	}

	private static Properties configuration;
	private static Properties uiConfiguration;
	private static String uiFile;

	private static void readConfiguration() {
		configuration = new Properties();
		try (InputStream newInputStream = Files.newInputStream(Paths.get("cvrl.cfg"))) {
			configuration.load(newInputStream);
		} catch (IOException e) {
			System.out.println("Error loading configuration file, please confirm existence of cvrl.cfg");
			System.exit(-1);
		}

		if (mode == SWING_GFX) {
			uiConfiguration = new Properties();
			try (InputStream newInputStream = Files.newInputStream(Paths.get(uiFile))) {
				uiConfiguration.load(newInputStream);
			} catch (IOException e) {
				System.out.println("Error loading configuration file, please confirm existence of " + uiFile);
				System.exit(-1);
			}
		}

	}

	private static void title() {
		STMusicManagerNew.thus.playKey("TITLE");
		int choice = Display.thus.showTitleScreen();
		switch (choice) {
		case 0:
			newGame();
			break;
		case 1:
			loadGame();
			break;
		case 2:
			prologue();
			break;
		case 3:
			training();
			break;
		case 4:
			arena();
			break;
		case 5:
			Display.thus.showHiscores(GameFiles.loadScores("hiscore.tbl"));
			Display.thus.showHiscores(GameFiles.loadScores("arena.tbl"));
			title();
			break;

		case 6:
			System.out.println("CastlevaniaRL v" + Game.getVersion() + ", clean Exit");
			System.out.println("Thank you for playing!");
			System.exit(0);
			break;

		}

	}

	private static void prologue() {
		if (currentGame != null) {
			ui.removeCommandListener(currentGame);
		}
		currentGame = new Game();
		currentGame.setCanSave(false);
		currentGame.setInterfaces(ui, uiSelector);
		setMonsterRecord(GameFiles.getMonsterRecord());
		currentGame.prologue();
		title();
	}

	private static void arena() {
		if (currentGame != null) {
			ui.removeCommandListener(currentGame);
		}
		currentGame = new Game();
		currentGame.setCanSave(false);
		currentGame.setInterfaces(ui, uiSelector);
		setMonsterRecord(GameFiles.getMonsterRecord());
		currentGame.arena();
		title();
	}

	private static void training() {
		if (currentGame != null) {
			ui.removeCommandListener(currentGame);
		}
		currentGame = new Game();
		currentGame.setCanSave(false);
		currentGame.setInterfaces(ui, uiSelector);
		setMonsterRecord(GameFiles.getMonsterRecord());
		currentGame.training();
		title();
	}

	private static void loadGame() {
		File saveDirectory = new File("savegame");
		File[] saves = saveDirectory.listFiles(new SaveGameFilenameFilter());

		int index = Display.thus.showSavedGames(saves);
		if (index == -1)
			title();
		try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(saves[index].toPath()))) {
			currentGame = (Game) ois.readObject();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			crash("Invalid savefile or wrong version", new CRLException("Invalid savefile or wrong version"));
		}
		currentGame.setInterfaces(ui, uiSelector);
		if (currentGame.getPlayer().getLevel() == null) {
			crash("Player wasnt loaded", new Exception("Player wasnt loaded"));
		}
		currentGame.setPlayer(currentGame.getPlayer());
		ui.setPlayer(currentGame.getPlayer());
		uiSelector.setPlayer(currentGame.getPlayer());
		setMonsterRecord(GameFiles.getMonsterRecord());
		currentGame.resume();

		title();
	}

	private static void newGame() {
		if (currentGame != null) {
			ui.removeCommandListener(currentGame);
		}
		currentGame = new Game();
		currentGame.setCanSave(true);
		currentGame.setInterfaces(ui, uiSelector);
		setMonsterRecord(GameFiles.getMonsterRecord());
		currentGame.newGame();

		title();
	}

	private static void initializeUI(Object si) {
		Action walkAction = new Walk();
		Action jump = new Jump();
		Action thrown = new Throw();
		Action use = new Use();
		Action equip = new Equip();
		Action unequip = new Unequip();
		Action attack = new Attack();
		Action reload = new Reload();
		Action target = new TargetPS();
		Action switchWeapons = new SwitchWeapons();
		Action get = new Get();
		Action drop = new Drop();
		Action dive = new Dive();

		UserAction[] userActions = null;
		UserCommand[] userCommands = null;
		Properties keyBindings = null;
		try (FileInputStream inStream = new FileInputStream("keys.cfg")) {
			Properties keyConfig = new Properties();

			keyConfig.load(inStream);

			keyBindings = new Properties();
			keyBindings.put("WEAPON_KEY", readKeyString(keyConfig, "weapon"));
			keyBindings.put("DONOTHING1_KEY", readKeyString(keyConfig, "doNothing"));
			keyBindings.put("DONOTHING2_KEY", readKeyString(keyConfig, "doNothing2"));
			keyBindings.put("UP1_KEY", readKeyString(keyConfig, "up"));
			keyBindings.put("UP2_KEY", readKeyString(keyConfig, "up2"));
			keyBindings.put("LEFT1_KEY", readKeyString(keyConfig, "left"));
			keyBindings.put("LEFT2_KEY", readKeyString(keyConfig, "left2"));
			keyBindings.put("RIGHT1_KEY", readKeyString(keyConfig, "right"));
			keyBindings.put("RIGHT2_KEY", readKeyString(keyConfig, "right2"));
			keyBindings.put("DOWN1_KEY", readKeyString(keyConfig, "down"));
			keyBindings.put("DOWN2_KEY", readKeyString(keyConfig, "down2"));
			keyBindings.put("UPRIGHT1_KEY", readKeyString(keyConfig, "upRight"));
			keyBindings.put("UPRIGHT2_KEY", readKeyString(keyConfig, "upRight2"));
			keyBindings.put("UPLEFT1_KEY", readKeyString(keyConfig, "upLeft"));
			keyBindings.put("UPLEFT2_KEY", readKeyString(keyConfig, "upLeft2"));
			keyBindings.put("DOWNLEFT1_KEY", readKeyString(keyConfig, "downLeft"));
			keyBindings.put("DOWNLEFT2_KEY", readKeyString(keyConfig, "downLeft2"));
			keyBindings.put("DOWNRIGHT1_KEY", readKeyString(keyConfig, "downRight"));
			keyBindings.put("DOWNRIGHT2_KEY", readKeyString(keyConfig, "downRight2"));
			keyBindings.put("SELF1_KEY", readKeyString(keyConfig, "self"));
			keyBindings.put("SELF2_KEY", readKeyString(keyConfig, "self2"));
			keyBindings.put("ATTACK1_KEY", readKeyString(keyConfig, "attack1"));
			keyBindings.put("ATTACK2_KEY", readKeyString(keyConfig, "attack2"));
			keyBindings.put("JUMP_KEY", readKeyString(keyConfig, "jump"));
			keyBindings.put("THROW_KEY", readKeyString(keyConfig, "throw"));
			keyBindings.put("EQUIP_KEY", readKeyString(keyConfig, "equip"));
			keyBindings.put("UNEQUIP_KEY", readKeyString(keyConfig, "unequip"));
			keyBindings.put("RELOAD_KEY", readKeyString(keyConfig, "reload"));
			keyBindings.put("USE_KEY", readKeyString(keyConfig, "use"));
			keyBindings.put("GET_KEY", readKeyString(keyConfig, "get"));
			keyBindings.put("GET2_KEY", readKeyString(keyConfig, "get2"));
			keyBindings.put("DROP_KEY", readKeyString(keyConfig, "drop"));
			keyBindings.put("DIVE_KEY", readKeyString(keyConfig, "dive"));
			keyBindings.put("TARGET_KEY", readKeyString(keyConfig, "target"));
			keyBindings.put("SWITCH_WEAPONS_KEY", readKeyString(keyConfig, "switchWeapons"));
			keyBindings.put("QUIT_KEY", readKeyString(keyConfig, "PROMPTQUIT"));
			keyBindings.put("HELP1_KEY", readKeyString(keyConfig, "HELP1"));
			keyBindings.put("HELP2_KEY", readKeyString(keyConfig, "HELP2"));
			keyBindings.put("LOOK_KEY", readKeyString(keyConfig, "LOOK"));
			keyBindings.put("PROMPT_SAVE_KEY", readKeyString(keyConfig, "PROMPTSAVE"));
			keyBindings.put("SHOW_SKILLS_KEY", readKeyString(keyConfig, "SHOWSKILLS"));
			keyBindings.put("SHOW_INVENTORY_KEY", readKeyString(keyConfig, "SHOWINVEN"));
			keyBindings.put("SHOW_STATS_KEY", readKeyString(keyConfig, "SHOWSTATS"));
			keyBindings.put("CHARDUMP_KEY", readKeyString(keyConfig, "CHARDUMP"));
			keyBindings.put("SHOW_MESSAGE_HISTORY_KEY", readKeyString(keyConfig, "SHOWMESSAGEHISTORY"));
			keyBindings.put("SHOW_MAP_KEY", readKeyString(keyConfig, "SHOWMAP"));
			keyBindings.put("EXAMINE_LEVEL_MAP_KEY", readKeyString(keyConfig, "EXAMINELEVELMAP"));
			keyBindings.put("SWITCH_MUSIC_KEY", readKeyString(keyConfig, "SWITCHMUSIC"));

			Display.setKeyBindings(keyBindings);

			userActions = new UserAction[] { new UserAction(attack, i(keyBindings.getProperty("ATTACK1_KEY"))),
					new UserAction(attack, i(keyBindings.getProperty("ATTACK2_KEY"))),
					new UserAction(jump, i(keyBindings.getProperty("JUMP_KEY"))),
					new UserAction(thrown, i(keyBindings.getProperty("THROW_KEY"))),
					new UserAction(equip, i(keyBindings.getProperty("EQUIP_KEY"))),
					new UserAction(unequip, i(keyBindings.getProperty("UNEQUIP_KEY"))),
					new UserAction(reload, i(keyBindings.getProperty("RELOAD_KEY"))),
					new UserAction(use, i(keyBindings.getProperty("USE_KEY"))),
					new UserAction(get, i(keyBindings.getProperty("GET_KEY"))),
					new UserAction(drop, i(keyBindings.getProperty("DROP_KEY"))),
					new UserAction(dive, i(keyBindings.getProperty("DIVE_KEY"))),
					new UserAction(target, i(keyBindings.getProperty("TARGET_KEY"))),
					new UserAction(switchWeapons, i(keyBindings.getProperty("SWITCH_WEAPONS_KEY"))),
					new UserAction(get, i(keyBindings.getProperty("GET2_KEY"))), };

			userCommands = new UserCommand[] {
					new UserCommand(CommandListener.PROMPTQUIT, i(keyBindings.getProperty("QUIT_KEY"))),
					new UserCommand(CommandListener.HELP, i(keyBindings.getProperty("HELP1_KEY"))),
					new UserCommand(CommandListener.LOOK, i(keyBindings.getProperty("LOOK_KEY"))),
					new UserCommand(CommandListener.PROMPTSAVE, i(keyBindings.getProperty("PROMPT_SAVE_KEY"))),
					new UserCommand(CommandListener.SHOWSKILLS, i(keyBindings.getProperty("SHOW_SKILLS_KEY"))),
					new UserCommand(CommandListener.HELP, i(keyBindings.getProperty("HELP2_KEY"))),
					new UserCommand(CommandListener.SHOWINVEN, i(keyBindings.getProperty("SHOW_INVENTORY_KEY"))),
					new UserCommand(CommandListener.SHOWSTATS, i(keyBindings.getProperty("SHOW_STATS_KEY"))),
					new UserCommand(CommandListener.CHARDUMP, i(keyBindings.getProperty("CHARDUMP_KEY"))),
					new UserCommand(CommandListener.SHOWMESSAGEHISTORY,
							i(keyBindings.getProperty("SHOW_MESSAGE_HISTORY_KEY"))),
					new UserCommand(CommandListener.SHOWMAP, i(keyBindings.getProperty("SHOW_MAP_KEY"))),
					new UserCommand(CommandListener.EXAMINELEVELMAP,
							i(keyBindings.getProperty("EXAMINE_LEVEL_MAP_KEY"))),
					new UserCommand(CommandListener.SWITCHMUSIC, i(keyBindings.getProperty("SWITCH_MUSIC_KEY"))), };

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("keys.cfg config file not found");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem reading keys.cfg config file");
		}

		switch (mode) {
		case SWING_GFX:
			((GFXUserInterface) ui).init((SwingSystemInterface) si, userCommands, target);
			uiSelector = new GFXUISelector();
			((GFXUISelector) uiSelector).init((SwingSystemInterface) si, userActions, uiConfiguration, walkAction,
					target, attack, (GFXUserInterface) ui, keyBindings);
			break;
		case JCURSES_CONSOLE:
			((ConsoleUserInterface) ui).init((ConsoleSystemInterface) si, userCommands, target);
			uiSelector = new ConsoleUISelector();
			((ConsoleUISelector) uiSelector).init((ConsoleSystemInterface) si, userActions, walkAction, target, attack,
					(ConsoleUserInterface) ui, keyBindings);
			break;
		case SWING_CONSOLE:
			break;
		}
	}

	public static void main(String[] args) {
		mode = SWING_GFX;
		// mode = SWING_CONSOLE;
		uiFile = "slash-barrett.ui";
		if (args != null && args.length > 0) {
			if (args[0].equalsIgnoreCase("sgfx")) {
				mode = SWING_GFX;
				if (args.length > 1)
					uiFile = args[1];
				else
					uiFile = "slash-barrett.ui";
			} else if (args[0].equalsIgnoreCase("jc"))
				mode = JCURSES_CONSOLE;
			else if (args[0].equalsIgnoreCase("sc"))
				mode = SWING_CONSOLE;
		}

		init();
		System.out.println("Launching game");
		try {
			title();
		} catch (Exception e) {
			Game.crash("Unrecoverable Exception [Press Space]", e);
			try {
				System.in.read();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void crash(String message, Throwable exception) {
		System.out.println("CastlevaniaRL " + Game.getVersion() + ": Error");
		System.out.println();
		System.out.println("Unrecoverable error: " + message);
		exception.printStackTrace();
		if (currentGame != null) {
			System.out.println("Trying to save game");
			GameFiles.saveGame(currentGame, currentGame.getPlayer());
		}
		System.exit(-1);
	}

	private static Map<String, MonsterRecord> monsterRecord;

	public static MonsterRecord getMonsterRecordFor(String monsterID) {
		return monsterRecord.get(monsterID);
	}

	public static void setMonsterRecord(Map<String, MonsterRecord> monsterRecord) {
		Main.monsterRecord = monsterRecord;
	}

	public static Map<String, MonsterRecord> getMonsterRecord() {
		return monsterRecord;
	}

}

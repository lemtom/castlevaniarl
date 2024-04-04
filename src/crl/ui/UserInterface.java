package crl.ui;

import crl.Visible;
import crl.action.Action;
import crl.actor.Actor;
import crl.actor.Message;
import crl.feature.Feature;
import crl.game.GameFiles;
import crl.game.STMusicManagerNew;
import crl.item.Item;
import crl.item.Merchant;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.npc.NPC;
import crl.player.Player;
import crl.ui.effects.Effect;
import sz.csi.CharKey;
import sz.csi.textcomponents.ListItem;
import sz.csi.textcomponents.MenuItem;
import sz.util.Debug;
import sz.util.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Shows the level Informs the Actions and Commands of the player. Must be
 * listening to a System Interface
 */
public abstract class UserInterface implements CommandListener/* , Runnable */ {
	// Attributes
	protected int xrange;
	protected int yrange;
	protected Monster lockedMonster;

	// private String[] quitMessages;
	protected String[] quitMessages = new String[] { "Do you really want to abandon Transylvania?",
			"Quit now, and let the evil count roam the world free?",
			"Leave now, and lose this unique chance to fight for freedom?", "Abandon the people of Transylvania?",
			"Deceive everybody who trusted you?", "Throw your weapons away and live a 'peaceful' life?"

	};

	// Status
	protected ArrayList<ListItem> monstersOnSight = new ArrayList<>();
	protected ArrayList<ListItem> featuresOnSight = new ArrayList<>();
	protected ArrayList<ListItem> itemsOnSight = new ArrayList<>();
	protected Action actionSelectedByCommand;

	// Components

	protected boolean eraseOnArrival; // Erase the buffer upon the arrival of a new msg

	protected String lastMessage;
	protected Level level;
	// Relations
	protected Player player;

	// Setters
	/**
	 * Sets the object which will be informed of the player commands. this
	 * corresponds to the Game object
	 */

	// Getters
	public Player getPlayer() {
		return player;
	}

	// Smart Getters

	// Final attributes

	public static final String[] verboseSkills = new String[] { "Unskilled", "Mediocre(1)", "Mediocre(2)",
			"Mediocre(3)", "Trained(1)", "Trained(2)", "Trained(3)", "Skilled(1)", "Skilled(2)", "Skilled(3)",
			"Master" };

	private boolean[][] FOVMask;

	// Interactive Methods
	public abstract void doLook();

	public abstract void launchMerchant(Merchant who);

	public abstract void chat(NPC who);

	public abstract boolean promptChat(NPC who);

	// Drawing Methods
	public abstract void drawEffect(Effect what);

	public boolean isOnFOVMask(int x, int y) {
		return FOVMask[x][y];
	}

	public abstract void addMessage(Message message);

	public abstract List<String> getMessageBuffer();

	public void setPlayer(Player pPlayer) {
		player = pPlayer;
		level = player.getLevel();
	}

	public void init(UserCommand[] gameCommands) {
		FOVMask = new boolean[80][25];
		for (UserCommand gameCommand : gameCommands)
			this.gameCommands.put(gameCommand.getKeyCode() + "", gameCommand);
		addCommandListener(this);
	}

	protected int getRelatedCommand(int keyCode) {
		Debug.enterMethod(this, "getRelatedCommand", keyCode + "");
		UserCommand uc = gameCommands.get(keyCode + "");
		if (uc == null) {
			Debug.exitMethod(CommandListener.NONE);
			return CommandListener.NONE;
		}

		int ret = uc.getCommand();
		Debug.exitMethod(ret + "");
		return ret;
	}

	public abstract boolean isDisplaying(Actor who);

	public void levelChange() {
		level = player.getLevel();
	}

	protected void informPlayerCommand(int command) {
		Debug.enterMethod(this, "informPlayerCommand", command + "");
		for (CommandListener commandListener : commandListeners) {
			commandListener.commandSelected(command);
		}
		Debug.exitMethod();
	}

	public void addCommandListener(CommandListener pCl) {
		commandListeners.add(pCl);
	}

	public void removeCommandListener(CommandListener pCl) {
		commandListeners.remove(pCl);
	}

	protected HashMap<String, UserCommand> gameCommands = new HashMap<>();
	private ArrayList<CommandListener> commandListeners = new ArrayList<>(5); // Class CommandListener

	/**
	 * Prompts for Yes or NO
	 */
	public abstract boolean prompt();

	public abstract void refresh();

	/**
	 * This method can be invoked from any thread and won't cause rendering issues.
	 * Provided mostly so that the Swing implementation can refresh the UI from non
	 * UI threads safely.
	 */
	public abstract void safeRefresh();

	/**
	 * Shows a message immediately; useful for system messages.
	 * 
	 * @param x the message to be shown
	 */
	public abstract void showMessage(String x);

	public abstract void showImportantMessage(String x);

	/**
	 * Shows a message immediately; useful for system messages. Waits for a key
	 * press or something.
	 * 
	 * @param x the message to be shown
	 */
	public abstract void showSystemMessage(String x);

	/* Shows a level was won, lets pick a random spirit */
	public abstract void levelUp();

	public abstract void processQuit();

	public abstract void processSave();

	public abstract void showPlayerStats();

	public abstract Action showInventory() throws ActionCancelException;

	public abstract Action showSkills() throws ActionCancelException;

	private boolean gameOver;

	public void setGameOver(boolean bal) {

		gameOver = bal;
	}

	public boolean gameOver() {
		return gameOver;
	}

	// Singleton
	private static UserInterface singleton;

	public static void setSingleton(UserInterface ui) {
		singleton = ui;
	}

	public static UserInterface getUI() {
		return singleton;
	}

	public abstract void setTargets(Action a) throws ActionCancelException;

	public abstract void showMessageHistory();

	public abstract void setPersistantMessage(String description);

	public abstract void showVersionDialog(String description, boolean stop);

	protected abstract Item pickItem(String prompt) throws ActionCancelException;

	protected abstract Position pickPosition(String prompt, int fireKeyCode) throws ActionCancelException;

	protected abstract int pickDirection(String prompt) throws ActionCancelException;

	protected abstract Item pickEquipedItem(String prompt) throws ActionCancelException;

	protected abstract ArrayList<MenuItem> pickMultiItems(String prompt) throws ActionCancelException;

	protected abstract Item pickUnderlyingItem(String prompt) throws ActionCancelException;

	protected void setActionTargets(Action a, Action target) throws ActionCancelException {
		if (a.needsItem())
			a.setItem(pickItem(a.getPromptItem()));
		if (a.needsDirection()) {
			a.setDirection(pickDirection(a.getPromptDirection()));
		}
		if (a.needsPosition()) {
			if (a == target)
				a.setPosition(pickPosition(a.getPromptPosition(), CharKey.f));
			else
				a.setPosition(pickPosition(a.getPromptPosition(), CharKey.SPACE));
		}
		if (a.needsEquipedItem())
			a.setEquipedItem(pickEquipedItem(a.getPromptEquipedItem()));
		if (a.needsMultiItems()) {
			a.setMultiItems(pickMultiItems(a.getPromptMultiItems()));
		}
		if (a.needsSpirits()) {
			a.setMultiItems(pickSpirits(a));
		}
		if (a.needsUnderlyingItem()) {
			a.setItem(pickUnderlyingItem(a.getPrompUnderlyingItem()));
		}
	}

	protected abstract List<MenuItem> pickSpirits(Action a) throws ActionCancelException;

	protected static int calculate(int hits) {
		return (hits - 1) / 20 + 1;
	}

	protected static int calculateRest(int hits) {
		return (hits - 1) % 20 + 1;
	}

	protected static boolean checkVisible(Visible visibleThing) {
		return visibleThing != null && visibleThing.isVisible();
	}

	protected void adjustOffset(Position offset) {
		if (offset.x >= getXrange())
			offset.x = getXrange();
		if (offset.x <= -getXrange())
			offset.x = -getXrange();
		if (offset.y >= getYrange())
			offset.y = getYrange();
		if (offset.y <= -getYrange())
			offset.y = -getYrange();
	}

	protected int getXrange() {
		return xrange;
	}

	protected int getYrange() {
		return yrange;
	}

	protected void switchMusic() {
		boolean enabled = STMusicManagerNew.thus.isEnabled();
		if (enabled) {
			showMessage("Turn off music");
			STMusicManagerNew.thus.stopMusic();
			STMusicManagerNew.thus.setEnabled(false);
		} else {
			showMessage("Turn on music");
			STMusicManagerNew.thus.setEnabled(true);
			if (!level.isDay() && level.hasNoonMusic())
				STMusicManagerNew.thus.playKey(level.getMusicKeyNoon());
			else
				STMusicManagerNew.thus.playKey(level.getMusicKeyMorning());
		}
	}

	protected abstract void examineLevelMap();

	public void commandSelected(int commandCode) {
		switch (commandCode) {
		case CommandListener.PROMPTQUIT:
			processQuit();
			break;
		case CommandListener.PROMPTSAVE:
			processSave();
			break;
		case CommandListener.HELP:
			doHelp();
			break;
		case CommandListener.LOOK:
			doLook();
			break;
		case CommandListener.SHOWSTATS:
			showPlayerStats();
			break;
		case CommandListener.SHOWINVEN:
			try {
				actionSelectedByCommand = showInventory();
			} catch (ActionCancelException ace) {
				doShowInventory();
			}
			break;
		case CommandListener.SHOWSKILLS:
			try {
				if (!player.isSwimming()) {
					actionSelectedByCommand = showSkills();
				} else {
					player.getLevel().addMessage("You can't do that!");
					throw new ActionCancelException();
				}
			} catch (ActionCancelException ace) {
				doShowSkills();
			}
			break;
		case CommandListener.SHOWMESSAGEHISTORY:
			showMessageHistory();
			break;
		case CommandListener.SHOWMAP:
			Display.thus.showMap(level.getMapLocationKey(), level.getDescription());
			break;
		case CommandListener.SWITCHMUSIC:
			switchMusic();
			break;
		case CommandListener.EXAMINELEVELMAP:
			examineLevelMap();
			break;
		case CommandListener.CHARDUMP:
			GameFiles.saveChardump(player);
			showMessage("Character File Dumped.");
			break;
		}
	}

	protected abstract void doShowSkills();

	protected void doShowInventory() {
		// Do nothing
	}

	protected void doHelp() {
		Display.thus.showHelp();
	}

	protected Position establishDefaultTarget(Position nearest) {
		Position defaultTarget;
		defaultTarget = nearest;
		if (lockedMonster != null) {
			if (!player.sees(lockedMonster) || lockedMonster.isDead()) {
				lockedMonster = null;
			} else {
				defaultTarget = new Position(lockedMonster.getPosition());
			}
		}
		return defaultTarget;
	}
	
	protected Position establishOffset(Position defaultTarget, int pcX, int pcY) {
		Position offset = new Position(0, 0);
		if (defaultTarget != null) {
			offset = new Position(defaultTarget.x - player.getPosition().x, defaultTarget.y - player.getPosition().y);
		}
		if (!insideViewPort(pcX + offset.x, pcY + offset.y)) {
			offset = new Position(0, 0);
		}
		return offset;
	}
	
	protected String establishLooked(Position browser, Cell choosen, Feature feat, Item item, Actor actor) {
		String looked = "";
		if (choosen != null)
			looked += choosen.getDescription();
		if (level.getBloodAt(browser) != null)
			looked += "{bloody}";
		if (feat != null)
			looked += ", " + feat.getDescription();
		if (actor != null)
			looked += ", " + actor.getDescription();
		if (item != null)
			looked += ", " + item.getDescription();
		return looked;
	}

	public abstract boolean insideViewPort(int i, int j);
}
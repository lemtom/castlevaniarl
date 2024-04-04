package crl.ui.consoleUI;

import crl.action.*;
import crl.actor.Actor;
import crl.actor.Message;
import crl.feature.Feature;
import crl.feature.SmartFeature;
import crl.item.Item;
import crl.item.ItemDefinition;
import crl.item.Merchant;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.monster.VMonster;
import crl.npc.NPC;
import crl.player.*;
import crl.player.advancements.Advancement;
import crl.ui.*;
import crl.ui.consoleUI.effects.CharEffect;
import crl.ui.effects.Effect;
import sz.csi.CharKey;
import sz.csi.ConsoleSystemInterface;
import sz.csi.textcomponents.*;
import sz.util.Debug;
import sz.util.Line;
import sz.util.Position;
import sz.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows the level using characters. Informs the Actions and Commands of the
 * player. Must be listening to a System Interface
 */
public class ConsoleUserInterface extends UserInterface implements CommandListener, Runnable {
	// Attributes
	protected int xrange = 25;
	protected int yrange = 9;

	// Components
	private TextInformBox messageBox;
	private TextBox persistantMessageBox;
	public boolean showPersistantMessageBox = false;
	private ListBox idList;

	private boolean eraseOnArrival; // Erase the buffer upon the arrival of a new msg

	private Map<String, BasicListItem> sightListItems = new HashMap<>();
	// Relations

	private transient ConsoleSystemInterface si;

	// Smart Getters
	public Position getAbsolutePosition(Position insideLevel) {
		Position relative = Position.subs(insideLevel, player.getPosition());
		return Position.add(PC_POS, relative);
	}

	public final Position VP_START = new Position(1, 3), VP_END = new Position(51, 21), PC_POS = new Position(25, 12);

	private boolean[][] FOVMask;

	// Interactive Methods
	public void doLook() {
		Position offset = new Position(0, 0);
		messageBox.setForeColor(ConsoleSystemInterface.RED);
		si.saveBuffer();
		Monster lookedMonster = null;
		while (true) {
			lookedMonster = look(offset, lookedMonster);
			CharKey x = new CharKey(CharKey.NONE);
			while (x.code != CharKey.SPACE && x.code != CharKey.m && x.code != CharKey.ESC && !x.isArrow())
				x = si.inkey();
			if (x.code == CharKey.SPACE || x.code == CharKey.ESC) {
				si.restore();
				break;
			}
			if (x.code == CharKey.m) {
				if (lookedMonster != null)
					Display.thus.showMonsterScreen(lookedMonster, player);
			} else {
				offset.add(Action.directionToVariation(Action.toIntDirection(x)));
				adjustOffset(offset);
			}
		}
		messageBox.setText("Look mode off");
		refresh();
	}

	private Monster look(Position offset, Monster lookedMonster) {
		Position browser = Position.add(player.getPosition(), offset);
		String looked = "";
		si.restore();
		if (FOVMask[PC_POS.x + offset.x][PC_POS.y + offset.y]) {
			Cell choosen = level.getMapCell(browser);
			Feature feat = level.getFeatureAt(browser);
			List<MenuItem> items = level.getItemsAt(browser);
			looked += ConsoleUtils.determineLooked(choosen, feat, items, level.getBloodAt(browser));
			Actor actor = level.getActorAt(browser);
			if (actor != null) {
				if (actor instanceof Monster) {
					looked += ", " + actor.getDescription() + " ['m' for extended info]";
					lookedMonster = (Monster) actor;
				} else {
					looked += ", " + actor.getDescription();
				}
			}
		}
		messageBox.setText(looked);
		messageBox.draw();
		si.print(PC_POS.x + offset.x, PC_POS.y + offset.y, '_', ConsoleSystemInterface.RED);
		si.refresh();
		return lookedMonster;
	}

	public void launchMerchant(Merchant who) {
		Debug.enterMethod(this, "launchMerchant", who);
		si.saveBuffer();

		List<MenuItem> merchandise = who.getMerchandiseFor(player);
		if (merchandise == null || merchandise.isEmpty()) {
			chat(who);
			return;
		}
		Equipment.eqMode = true;
		Item.shopMode = true;
		MenuBox menuBox = new MenuBox(si);
		menuBox.setHeight(24);
		menuBox.setWidth(79);
		menuBox.setPosition(0, 0);
		menuBox.setMenuItems(merchandise);
		menuBox.setPromptSize(5);
		menuBox.setPrompt("Greetings " + player.getName() + "... I am " + who.getName() + ", the "
				+ who.getMerchandiseTypeDesc() + " merchant. May I interest you in an item?");
		menuBox.setForeColor(ConsoleSystemInterface.RED);
		menuBox.setBorder(true);
		while (true) {
			menuBox.setTitle(who.getName() + " (Gold:" + player.getGold() + ")");
			Item choice = (Item) menuBox.getSelection();
			if (choice == null)
				break;

			menuBox.setPrompt("The " + choice.getDescription() + ", " + choice.getShopDescription() + "; it costs "
					+ choice.getGoldPrice() + ", Do you want to buy it? (Y/n)");
			menuBox.draw();
			if (prompt())
				if (player.getGold() >= choice.getGoldPrice()) {
					player.reduceGold(choice.getGoldPrice());
					if (player.canCarry())
						player.addItem(choice);
					else
						level.addItem(player.getPosition(), choice);
					menuBox.setPrompt("Thanks!, May I interest you in something else?");
				} else {
					menuBox.setPrompt("I am afraid you don't have enough gold");
				}
			else {
				menuBox.setPrompt("Too bad... May I interest you in something else?");
			}
		}
		Equipment.eqMode = false;
		Item.shopMode = false;
		si.restore();
		Debug.exitMethod();
	}

	public void chat(NPC who) {
		si.saveBuffer();
		Debug.enterMethod(this, "chat", who);
		TextBox chatBox = new TextBox(si);
		chatBox.setHeight(7);
		chatBox.setWidth(33);
		chatBox.setPosition(28, 3);
		chatBox.setBorder(true);
		chatBox.setForeColor(ConsoleSystemInterface.WHITE);
		chatBox.setBorderColor(ConsoleSystemInterface.WHITE);
		chatBox.setText(who.getTalkMessage());
		chatBox.setTitle(who.getDescription());
		chatBox.draw();
		si.refresh();
		waitKey();
		si.restore();
		Debug.exitMethod();
	}

	public boolean promptChat(NPC who) {
		si.saveBuffer();
		Debug.enterMethod(this, "chat", who);
		TextBox chatBox = new TextBox(si);
		chatBox.setHeight(7);
		chatBox.setWidth(33);
		chatBox.setPosition(28, 3);
		chatBox.setBorder(true);
		chatBox.setForeColor(ConsoleSystemInterface.WHITE);
		chatBox.setBorderColor(ConsoleSystemInterface.WHITE);
		chatBox.setText(who.getTalkMessage());
		chatBox.draw();
		si.refresh();
		boolean ret = prompt();
		si.restore();
		Debug.exitMethod();
		return ret;

	}

	// Drawing Methods
	public void drawEffect(Effect what) {
		if (what == null)
			return;
		if (insideViewPort(getAbsolutePosition(what.getPosition()))) {
			si.refresh();
			si.setAutoRefresh(true);
			((CharEffect) what).drawEffect(this, si);
			si.setAutoRefresh(false);
		}
	}

	@Override
	public boolean isOnFOVMask(int x, int y) {
		return FOVMask[x][y];
	}

	private void drawLevel() {
		Debug.enterMethod(this, "drawLevel");
		Cell[][] rcells = level.getMemoryCellsAround(player.getPosition().x, player.getPosition().y,
				player.getPosition().z, xrange, yrange);
		Cell[][] vcells = level.getVisibleCellsAround(player.getPosition().x, player.getPosition().y,
				player.getPosition().z, xrange, yrange);

		Position runner = new Position(player.getPosition().x - xrange, player.getPosition().y - yrange,
				player.getPosition().z);

		loopOverRCells(rcells, vcells, runner);

		runner.x = player.getPosition().x - xrange;
		runner.y = player.getPosition().y - yrange;

		monstersOnSight.clear();
		featuresOnSight.clear();
		itemsOnSight.clear();

		loopOverVCells(vcells, runner);

		idList.clear();

		if (player.hasHostage()) {
			BasicListItem li = sightListItems.get(player.getHostage().getDescription());
			if (li == null) {
				CharAppearance hostageApp = (CharAppearance) player.getHostage().getAppearance();
				Debug.say("Adding " + hostageApp.getID() + " to the HashMap");
				sightListItems.put(player.getHostage().getDescription(), new BasicListItem(hostageApp.getChar(),
						hostageApp.getColor(), player.getHostage().getDescription()));
				li = sightListItems.get(player.getHostage().getDescription());
			}
			idList.addElement(li);
		}
		idList.addElements(monstersOnSight);
		idList.addElements(itemsOnSight);
		idList.addElements(featuresOnSight);

		Debug.exitMethod();
	}

	private void loopOverVCells(Cell[][] vcells, Position runner) {
		for (int x = 0; x < vcells.length; x++) {
			for (int y = 0; y < vcells[0].length; y++) {
				FOVMask[PC_POS.x - xrange + x][PC_POS.y - yrange + y] = false;
				if (vcells[x][y] != null) {
					FOVMask[PC_POS.x - xrange + x][PC_POS.y - yrange + y] = true;
					determineColors(vcells, runner, x, y);
					determineFeature(runner, x, y);

					determineSmartFeature(runner, x, y);

					determineItems(runner, x, y);

					determineMonsters(runner, x, y);

					if (!player.isInvisible()) {
						si.print(PC_POS.x, PC_POS.y, ((CharAppearance) player.getAppearance()).getChar(),
								((CharAppearance) player.getAppearance()).getColor());
					} else {

						si.print(PC_POS.x, PC_POS.y,
								((CharAppearance) AppearanceFactory.getAppearanceFactory().getAppearance("SHADOW"))
										.getChar(),
								((CharAppearance) AppearanceFactory.getAppearanceFactory().getAppearance("SHADOW"))
										.getColor());
					}

				}
				runner.y++;
			}
			runner.y = player.getPosition().y - yrange;
			runner.x++;
		}
	}

	private void determineSmartFeature(Position runner, int x, int y) {
		SmartFeature sfeat = level.getSmartFeature(runner);
		if (checkVisible(sfeat)) {
			CharAppearance featApp = (CharAppearance) sfeat.getAppearance();
			si.print(PC_POS.x - xrange + x, PC_POS.y - yrange + y, featApp.getChar(), featApp.getColor());
		}
	}

	private void determineFeature(Position runner, int x, int y) {
		Feature feat = level.getFeatureAt(runner);
		if (checkVisible(feat)) {
			BasicListItem li = sightListItems.get(feat.getID());
			if (li == null) {
				Debug.say("Adding " + feat.getID() + " to the HashMap");
				sightListItems.put(feat.getID(), new BasicListItem(((CharAppearance) feat.getAppearance()).getChar(),
						((CharAppearance) feat.getAppearance()).getColor(), feat.getDescription()));
				li = sightListItems.get(feat.getID());
			}
			if (feat.isRelevant() && !featuresOnSight.contains(li))
				featuresOnSight.add(li);
			CharAppearance featApp = (CharAppearance) feat.getAppearance();
			si.print(PC_POS.x - xrange + x, PC_POS.y - yrange + y, featApp.getChar(), featApp.getColor());
		}
	}

	private void determineColors(Cell[][] vcells, Position runner, int x, int y) {
		String bloodLevel = level.getBloodAt(runner);
		CharAppearance cellApp = (CharAppearance) vcells[x][y].getAppearance();
		int cellColor = cellApp.getColor();
		if (!level.isDay()) {
			cellColor = ConsoleSystemInterface.DARK_BLUE;
		}
		if (bloodLevel != null) {
			cellColor = ConsoleUtils.determineCellColorByBloodLevel(bloodLevel, cellColor, !level.isDay());
		}
		if (vcells[x][y].isWater()) {
			cellColor = ConsoleUtils.determineCellColorByWater(level.canFloatUpward(runner));
		}

		char cellChar = cellApp.getChar();
		if (level.getFrostAt(runner) != 0) {
			cellChar = '#';
			cellColor = ConsoleSystemInterface.CYAN;
		}
		if (level.getDepthFromPlayer(player.getPosition().x - xrange + x, player.getPosition().y - yrange + y) != 0) {
			cellColor = ConsoleSystemInterface.TEAL;
		}

		if (player.isInvisible() || x != xrange || y != yrange)
			si.print(PC_POS.x - xrange + x, PC_POS.y - yrange + y, cellChar, cellColor);
	}

	private void loopOverRCells(Cell[][] rcells, Cell[][] vcells, Position runner) {
		for (int x = 0; x < rcells.length; x++) {
			for (int y = 0; y < rcells[0].length; y++) {
				if (rcells[x][y] != null && !rcells[x][y].getAppearance().getID().equals("NOTHING")) {
					CharAppearance app = (CharAppearance) rcells[x][y].getAppearance();
					char cellChar = app.getChar();
					if (level.getFrostAt(runner) != 0) {
						cellChar = '#';
					}
					if (vcells[x][y] == null)
						si.print(PC_POS.x - xrange + x, PC_POS.y - yrange + y, cellChar, ConsoleSystemInterface.GRAY);
				} else if (vcells[x][y] == null || vcells[x][y].getID().equals("AIR"))
					si.print(PC_POS.x - xrange + x, PC_POS.y - yrange + y, CharAppearance.getVoidAppearance().getChar(),
							CharAppearance.BLACK);
				runner.y++;
			}
			runner.y = player.getPosition().y - yrange;
			runner.x++;
		}
	}

	private void determineMonsters(Position runner, int x, int y) {
		Monster monster = level.getMonsterAt(runner);
		if (checkVisible(monster)) {
			BasicListItem li = null;
			if (monster instanceof NPC) {
				li = sightListItems.get(monster.getDescription());
				if (li == null) {
					CharAppearance monsterApp = (CharAppearance) monster.getAppearance();
					Debug.say("Adding " + monster.getID() + " to the HashMap");
					sightListItems.put(monster.getDescription(),
							new BasicListItem(monsterApp.getChar(), monsterApp.getColor(), monster.getDescription()));
					li = sightListItems.get(monster.getDescription());
				}
			} else {
				li = sightListItems.get(monster.getID());
				if (li == null) {
					CharAppearance monsterApp = (CharAppearance) monster.getAppearance();
					Debug.say("Adding " + monster.getID() + " to the HashMap");
					sightListItems.put(monster.getID(),
							new BasicListItem(monsterApp.getChar(), monsterApp.getColor(), monster.getDescription()));
					li = sightListItems.get(monster.getID());
				}
			}
			if (!monstersOnSight.contains(li))
				monstersOnSight.add(li);

			CharAppearance monsterApp = (CharAppearance) monster.getAppearance();
			if (monster.canSwim() && level.getMapCell(runner) != null && level.getMapCell(runner).isShallowWater())
				si.print(PC_POS.x - xrange + x, PC_POS.y - yrange + y, '~', monsterApp.getColor());
			else if (monster.hasCounter(Consts.C_MONSTER_FREEZE))
				si.print(PC_POS.x - xrange + x, PC_POS.y - yrange + y, monsterApp.getChar(),
						ConsoleSystemInterface.CYAN);
			else
				si.print(PC_POS.x - xrange + x, PC_POS.y - yrange + y, monsterApp.getChar(), monsterApp.getColor());
		}
	}

	private void determineItems(Position runner, int x, int y) {
		List<MenuItem> items = level.getItemsAt(runner);
		Item item = null;
		if (items != null) {
			item = (Item) items.get(0);
		}
		if (checkVisible(item)) {
			CharAppearance itemApp = (CharAppearance) item.getAppearance();
			si.print(PC_POS.x - xrange + x, PC_POS.y - yrange + y, itemApp.getChar(), itemApp.getColor());
			BasicListItem li = sightListItems.get(item.getDefinition().getID());
			if (li == null) {
				sightListItems.put(item.getDefinition().getID(),
						new BasicListItem(((CharAppearance) item.getAppearance()).getChar(),
								((CharAppearance) item.getAppearance()).getColor(),
								item.getDefinition().getDescription()));
				li = sightListItems.get(item.getDefinition().getID());
			}
			if (!itemsOnSight.contains(li))
				itemsOnSight.add(li);

		}
	}

	@Override
	public void setPersistantMessage(String description) {
		persistantMessageBox.setText(description);
		showPersistantMessageBox = true;
	}

	private ArrayList<String> messageHistory = new ArrayList<>();

	public void addMessage(Message message) {
		Debug.enterMethod(this, "addMessage", message);
		if (eraseOnArrival) {
			messageBox.clear();
			messageBox.setForeColor(ConsoleSystemInterface.RED);
			eraseOnArrival = false;
		}
		if ((player != null && player.getPosition() != null && message.getLocation().z != player.getPosition().z)
				|| (message.getLocation() != null && !insideViewPort(getAbsolutePosition(message.getLocation())))) {
			Debug.exitMethod();
			return;
		}
		messageHistory.add(message.getText());
		if (messageHistory.size() > 100)
			messageHistory.remove(0);
		messageBox.addText(message.getText());

		messageBox.draw();
		Debug.exitMethod();

	}

	class ForeBackColorTuple {
		private final int foreColor;
		private final int backColor;

		public ForeBackColorTuple(int foreColor, int backColor) {
			this.foreColor = foreColor;
			this.backColor = backColor;
		}

		public int getForeColor() {
			return foreColor;
		}

		public int getBackColor() {
			return backColor;
		}
	}

	private ForeBackColorTuple determinePlayerHitColor() {
		switch (calculate(player.getHits())) {
		case 1:
			return new ForeBackColorTuple(ConsoleSystemInterface.RED, ConsoleSystemInterface.WHITE);
		case 2:
			return new ForeBackColorTuple(ConsoleSystemInterface.DARK_RED, ConsoleSystemInterface.RED);
		default:
			return new ForeBackColorTuple(ConsoleSystemInterface.MAGENTA, ConsoleSystemInterface.DARK_RED);
		}
	}

	private ForeBackColorTuple determineBossColor(int sixthiedBossHits) {
		switch (calculate(sixthiedBossHits)) {
		case 1:
			return new ForeBackColorTuple(ConsoleSystemInterface.YELLOW, ConsoleSystemInterface.WHITE);
		case 2:
			return new ForeBackColorTuple(ConsoleSystemInterface.BROWN, ConsoleSystemInterface.YELLOW);
		default:
			return new ForeBackColorTuple(ConsoleSystemInterface.PURPLE, ConsoleSystemInterface.BROWN);
		}
	}

	private void drawPlayerStatus() {
		Debug.enterMethod(this, "drawPlayerStatus");

		ForeBackColorTuple colors = determinePlayerHitColor();
		int foreColor = colors.getForeColor();
		int backColor = colors.getBackColor();
		String timeTile = "";
		int timeColor = ConsoleSystemInterface.YELLOW;
		switch (level.getDayTime()) {
		case Level.MORNING:
			timeTile = "O__";
			timeColor = ConsoleSystemInterface.BROWN;
			break;
		case Level.NOON:
			timeTile = "_O_";
			timeColor = ConsoleSystemInterface.YELLOW;
			break;
		case Level.AFTERNOON:
			timeTile = "__O";
			timeColor = ConsoleSystemInterface.RED;
			break;
		case Level.DUSK:
			timeTile = "(__";
			timeColor = ConsoleSystemInterface.BLUE;
			break;
		case Level.NIGHT:
			timeTile = "_O_";
			timeColor = ConsoleSystemInterface.BLUE;
			break;
		case Level.DAWN:
			timeTile = "__)";
			timeColor = ConsoleSystemInterface.BLUE;
			break;
		}

		String shot = "   ";
		if (player.getShotLevel() == 1)
			shot = "II ";
		if (player.getShotLevel() == 2)
			shot = "III";

		int rest = calculateRest(player.getHits());

		si.print(0, 0, "SCORE    " + player.getScore());
		si.print(0, 1, "PLAYER   ");

		for (int i = 0; i < 20; i++)
			if (i + 1 <= rest)
				si.print(i + 9, 1, 'I', foreColor);
			else
				si.print(i + 9, 1, 'I', backColor);

		si.print(0, 2, "ENEMY    ");
		if (player.getLevel().getBoss() != null) {
			int sixthiedBossHits = (int) Math
					.ceil((player.getLevel().getBoss().getHits() * 60.0) / player.getLevel().getBoss().getMaxHits());
			int foreColorB = 0;
			int backColorB = 0;
			ForeBackColorTuple bossColors = determineBossColor(sixthiedBossHits);
			foreColorB = bossColors.getForeColor();
			backColorB = bossColors.getBackColor();

			int restB = calculateRest(sixthiedBossHits);

			for (int i = 0; i < 20; i++)
				if (i + 1 <= restB)
					si.print(i + 9, 2, 'I', foreColorB);
				else
					si.print(i + 9, 2, 'I', backColorB);
		} else
			si.print(9, 2, "IIIIIIIIIIIIIIIIIIII", ConsoleSystemInterface.WHITE);

		si.print(31, 2, fill(player.getWeaponDescription() + " " + shot, 40));

		if (player.getLevel().getLevelNumber() == -1)
			si.print(43, 0, fill(player.getLevel().getDescription(), 35));
		else
			si.print(43, 0, fill(
					"STAGE   " + player.getLevel().getLevelNumber() + " " + player.getLevel().getDescription(), 35));

		si.print(31, 1, "v       ", ConsoleSystemInterface.RED);
		si.print(33, 1, "- " + player.getHearts());
		si.print(39, 1, "k     ", ConsoleSystemInterface.YELLOW);
		si.print(41, 1, "- " + player.getKeys());

		si.print(47, 1, "$            ", ConsoleSystemInterface.YELLOW);
		si.print(49, 1, "- " + player.getGold());

		si.print(60, 1, "TIME - ");
		si.print(67, 1, timeTile, timeColor);

		si.print(71, 1, "     ", ConsoleSystemInterface.WHITE);
		if (player.getFlag(Consts.ENV_FOG))
			si.print(71, 1, "FOG", ConsoleSystemInterface.TEAL);
		if (player.getFlag(Consts.ENV_RAIN))
			si.print(71, 1, "RAIN", ConsoleSystemInterface.BLUE);
		if (player.getFlag(Consts.ENV_SUNNY))
			si.print(71, 1, "SUNNY", ConsoleSystemInterface.YELLOW);
		if (player.getFlag(Consts.ENV_THUNDERSTORM))
			si.print(71, 1, "STORM", ConsoleSystemInterface.WHITE);

		si.print(1, 24, fill(player.getName() + ", the Lv" + player.getPlayerLevel() + " " + player.getClassString()
				+ " " + player.getStatusString(), 70));

		Debug.exitMethod();
	}

	private String fill(String str, int limit) {
		if (str.length() > limit)
			return str.substring(0, limit);
		else
			return str + spaces(limit - str.length());
	}

	private Map<Object, String> hashSpaces = new HashMap<>();

	private String spaces(int n) {
		StringBuilder ret = new StringBuilder(hashSpaces.get(n + ""));
		if (ret != null)
			return ret.toString();
		ret = new StringBuilder();
		for (int i = 0; i < n; i++)
			ret.append(" ");
		hashSpaces.put(n + "", ret.toString());
		return ret.toString();
	}

	private Action target;

	public void init(ConsoleSystemInterface psi, UserCommand[] gameCommands, Action target) {
		Debug.enterMethod(this, "init");
		this.target = target;
		super.init(gameCommands);
		messageBox = new TextInformBox(psi);
		idList = new ListBox(psi);

		messageBox.setPosition(1, 22);
		messageBox.setWidth(78);
		messageBox.setHeight(2);
		messageBox.setForeColor(ConsoleSystemInterface.RED);

		persistantMessageBox = new TextBox(psi);
		persistantMessageBox.setBounds(40, 5, 38, 14);
		persistantMessageBox.setBorder(true);
		persistantMessageBox.setBorderColor(ConsoleSystemInterface.RED);

		persistantMessageBox.setForeColor(ConsoleSystemInterface.WHITE);
		persistantMessageBox.setTitle("Tutorial");

		idList.setPosition(52, 4);
		idList.setWidth(27);
		idList.setHeight(18);
		si = psi;
		FOVMask = new boolean[80][25];
		Debug.exitMethod();
	}

	/**
	 * Checks if the point, relative to the console coordinates, is inside the
	 * ViewPort
	 */
	public boolean insideViewPort(int x, int y) {
		return (x >= 0 && x < FOVMask.length && y >= 0 && y < FOVMask[0].length) && FOVMask[x][y];
	}

	public boolean insideViewPort(Position what) {
		return insideViewPort(what.x, what.y);
	}

	public boolean isDisplaying(Actor who) {
		return insideViewPort(getAbsolutePosition(who.getPosition()));
	}

	protected Position pickPosition(String prompt, int fireKeyCode) throws ActionCancelException {
		Debug.enterMethod(this, "pickPosition");
		messageBox.setForeColor(ConsoleSystemInterface.BLUE);
		messageBox.setText(prompt);
		messageBox.draw();
		si.refresh();
		si.saveBuffer();

		Position defaultTarget = null;
		Position nearest = getNearestMonsterPosition();

		Position browser = null;
		defaultTarget = establishDefaultTarget(nearest);
		Position offset = establishOffset(defaultTarget, PC_POS.x, PC_POS.y);

		while (true) {
			si.restore();
			String looked = "";
			browser = Position.add(player.getPosition(), offset);

			if (FOVMask[PC_POS.x + offset.x][PC_POS.y + offset.y]) {
				Cell choosen = level.getMapCell(browser);
				Feature feat = level.getFeatureAt(browser);
				List<MenuItem> items = level.getItemsAt(browser);
				Item item = null;
				if (items != null) {
					item = (Item) items.get(0);
				}
				Actor actor = level.getActorAt(browser);
				si.restore();

				looked = establishLooked(browser, choosen, feat, item, actor);
			}
			messageBox.setText(prompt + " " + looked);
			messageBox.draw();
			drawLineTo(PC_POS.x + offset.x, PC_POS.y + offset.y, '*', ConsoleSystemInterface.DARK_BLUE);
			si.print(PC_POS.x + offset.x, PC_POS.y + offset.y, 'X', ConsoleSystemInterface.BLUE);
			si.refresh();
			CharKey x = new CharKey(CharKey.NONE);
			while (x.code != CharKey.SPACE && x.code != CharKey.ESC && x.code != fireKeyCode && !x.isArrow())
				x = si.inkey();
			if (x.code == CharKey.ESC) {
				si.restore();
				throw new ActionCancelException();
			}
			if (x.code == CharKey.SPACE || x.code == fireKeyCode) {
				si.restore();
				if (level.getMonsterAt(browser) != null)
					lockedMonster = level.getMonsterAt(browser);
				return browser;
			}
			offset.add(Action.directionToVariation(Action.toIntDirection(x)));
			adjustOffset(offset);
		}

	}

	protected int pickDirection(String prompt) throws ActionCancelException {
		Debug.enterMethod(this, "pickDirection");
		messageBox.setText(prompt);
		messageBox.draw();
		si.refresh();

		CharKey x = new CharKey(CharKey.NONE);
		while (x.code == CharKey.NONE)
			x = si.inkey();
		int ret = UISelector.toIntDirection(x);
		if (ret != -1) {
			Debug.exitMethod(ret);
			return ret;
		} else {
			ActionCancelException ace = new ActionCancelException();
			Debug.exitExceptionally(ace);
			si.refresh();
			throw ace;
		}
	}

	protected Item pickEquipedItem(String prompt) throws ActionCancelException {
		Debug.enterMethod(this, "pickEquipedItem");
		ArrayList<MenuItem> equipped = new ArrayList<>();
		if (player.getArmor() != null)
			equipped.add(player.getArmor());
		if (player.getWeapon() != null)
			equipped.add(player.getWeapon());
		if (player.getShield() != null)
			equipped.add(player.getShield());
		MenuBox menuBox = new MenuBox(si);
		menuBox.setBounds(10, 3, 60, 18);
		menuBox.setPromptSize(2);
		menuBox.setMenuItems(equipped);
		menuBox.setPrompt(prompt);
		menuBox.setForeColor(ConsoleSystemInterface.RED);
		menuBox.setBorder(true);
		si.saveBuffer();
		menuBox.draw();
		Item equiped = (Item) menuBox.getSelection();
		si.restore();
		if (equiped == null) {
			ActionCancelException ret = new ActionCancelException();
			Debug.exitExceptionally(ret);
			throw ret;
		}
		return equiped;
	}

	protected Item pickItem(String prompt) throws ActionCancelException {
		Debug.enterMethod(this, "pickItem");
		List<MenuItem> inventory = player.getInventory();
		MenuBox menuBox = new MenuBox(si);
		menuBox.setBounds(10, 3, 60, 18);
		menuBox.setPromptSize(2);
		menuBox.setMenuItems(inventory);
		menuBox.setPrompt(prompt);
		menuBox.setForeColor(ConsoleSystemInterface.RED);
		menuBox.setBorder(true);
		si.saveBuffer();
		menuBox.draw();
		Equipment equipment = (Equipment) menuBox.getSelection();
		si.restore();
		if (equipment == null) {
			ActionCancelException ret = new ActionCancelException();
			Debug.exitExceptionally(ret);
			throw ret;
		}
		return equipment.getItem();
	}

	protected Item pickUnderlyingItem(String prompt) throws ActionCancelException {
		Debug.enterMethod(this, "pickUnderlyingItem");
		List<MenuItem> items = level.getItemsAt(player.getPosition());
		if (items == null)
			return null;
		if (items.size() == 1)
			return (Item) items.get(0);
		MenuBox menuBox = new MenuBox(si);
		menuBox.setBounds(10, 3, 60, 18);
		menuBox.setPromptSize(2);
		menuBox.setMenuItems(items);
		menuBox.setPrompt(prompt);
		menuBox.setForeColor(ConsoleSystemInterface.RED);
		menuBox.setBorder(true);
		si.saveBuffer();
		menuBox.draw();
		Item item = (Item) menuBox.getSelection();
		si.restore();
		if (item == null) {
			ActionCancelException ret = new ActionCancelException();
			Debug.exitExceptionally(ret);
			throw ret;
		}
		return item;
	}

	protected ArrayList<MenuItem> pickMultiItems(String prompt) {
		Equipment.eqMode = true;
		List<MenuItem> inventory = player.getInventory();
		MenuBox menuBox = new MenuBox(si);
		menuBox.setBounds(25, 3, 40, 18);
		menuBox.setPromptSize(2);
		menuBox.setMenuItems(inventory);
		menuBox.setPrompt(prompt);
		menuBox.setForeColor(ConsoleSystemInterface.RED);
		menuBox.setBorder(true);
		ArrayList<MenuItem> ret = new ArrayList<>();
		MenuBox selectedBox = new MenuBox(si);
		selectedBox.setBounds(5, 3, 20, 18);
		selectedBox.setPromptSize(2);
		selectedBox.setPrompt("Selected Items");
		selectedBox.setMenuItems(ret);
		selectedBox.setForeColor(ConsoleSystemInterface.RED);
		selectedBox.setBorder(true);

		si.saveBuffer();

		while (true) {
			selectedBox.draw();
			menuBox.draw();

			Equipment equipment = (Equipment) menuBox.getSelection();
			if (equipment == null)
				break;
			if (!ret.contains(equipment.getItem()))
				ret.add(equipment.getItem());
		}
		si.restore();
		Equipment.eqMode = false;
		return ret;
	}

	protected ArrayList<MenuItem> pickSpirits(Action a) {
		List<MenuItem> originalInventory = player.getInventory();
		List<MenuItem> inventory = new ArrayList<>();
		for (MenuItem menuItem : originalInventory) {
			Equipment testEq = (Equipment) menuItem;
			if (testEq.getItem().getDefinition().getID().endsWith("_SPIRIT")) {
				inventory.add(testEq);
			}
		}

		MenuBox menuBox = new MenuBox(si);
		menuBox.setBounds(25, 3, 40, 18);
		menuBox.setPromptSize(2);
		menuBox.setMenuItems(inventory);
		menuBox.setPrompt("Select the spirits to fusion");
		menuBox.setForeColor(ConsoleSystemInterface.RED);
		menuBox.setBorder(true);

		ArrayList<MenuItem> ret = new ArrayList<>();
		MenuBox selectedBox = new MenuBox(si);
		selectedBox.setBounds(5, 3, 20, 18);
		selectedBox.setPromptSize(2);
		selectedBox.setPrompt("Selected Spirits");
		selectedBox.setMenuItems(ret);
		selectedBox.setForeColor(ConsoleSystemInterface.RED);
		selectedBox.setBorder(true);

		si.saveBuffer();

		while (true) {
			selectedBox.draw();
			menuBox.draw();

			Equipment equipment = (Equipment) menuBox.getSelection();
			if (equipment == null)
				break;
			if (!ret.contains(equipment.getItem()))
				ret.add(equipment.getItem());
		}
		si.restore();
		return ret;
	}

	public void processQuit() {
		messageBox.setForeColor(ConsoleSystemInterface.RED);
		messageBox.setText(quitMessages[Util.rand(0, quitMessages.length - 1)] + " (y/n)");
		messageBox.draw();
		si.refresh();
		if (prompt()) {
			messageBox.setText("Go away, and let the world flood in darkness... [Press Space to continue]");
			messageBox.draw();
			si.refresh();
			si.waitKey(CharKey.SPACE);
			player.getGameSessionInfo().setDeathCause(GameSessionInfo.QUIT);
			player.getGameSessionInfo().setDeathLevel(level.getLevelNumber());
			informPlayerCommand(CommandListener.QUIT);
		}
		messageBox.draw();
		messageBox.clear();
		si.refresh();
	}

	public void processSave() {
		if (!player.getGame().canSave()) {
			level.addMessage("You cannot save your game here!");
			return;
		}
		messageBox.setForeColor(ConsoleSystemInterface.RED);
		messageBox.setText("Save your game? (y/n)");
		messageBox.draw();
		si.refresh();
		if (prompt()) {
			messageBox.setText("Saving... I will await your return.. [Press Space to continue]");
			messageBox.draw();
			si.refresh();
			si.waitKey(CharKey.SPACE);
			informPlayerCommand(CommandListener.SAVE);
		}
		messageBox.draw();
		messageBox.clear();
		si.refresh();
	}

	public boolean prompt() {
		CharKey x = new CharKey(CharKey.NONE);
		while (x.code != CharKey.Y && x.code != CharKey.y && x.code != CharKey.N && x.code != CharKey.n)
			x = si.inkey();
		return x.code == CharKey.Y || x.code == CharKey.y;
	}

	@Override
	public void showVersionDialog(String description, boolean stop) {
		si.print(2, 20, description, ConsoleSystemInterface.WHITE);
		si.refresh();
		if (stop) {
			si.waitKey(CharKey.SPACE);
		}
	}

	public void safeRefresh() {
		// For the Console UI we just do a normal refresh
		refresh();
	}

	public void refresh() {
		drawPlayerStatus();
		drawLevel();
		if (showPersistantMessageBox) {
			persistantMessageBox.draw();
		} else {
			idList.draw();
		}

		si.refresh();
		messageBox.draw();
		messageBox.setForeColor(ConsoleSystemInterface.DARK_RED);
		if (!player.getFlag("KEEPMESSAGES"))
			eraseOnArrival = true;

	}

	public void setTargets(Action a) throws ActionCancelException {
		setActionTargets(a, target);
	}

	private ArrayList<MenuItem> vecItemUsageChoices = new ArrayList<>();
	{
		vecItemUsageChoices.add(new SimpleMenuItem('*', "(u)se", 1));
		vecItemUsageChoices.add(new SimpleMenuItem('*', "(e)quip", 2));
		vecItemUsageChoices.add(new SimpleMenuItem('*', "(d)rop", 3));
		vecItemUsageChoices.add(new SimpleMenuItem('*', "(t)hrow", 4));
		vecItemUsageChoices.add(new SimpleMenuItem('*', "( ) Cancel", 5));

	}

	private int[] additionalKeys = new int[] { CharKey.N1, CharKey.N2, CharKey.N3, CharKey.N4, };

	private int[] itemUsageAdditionalKeys = new int[] { CharKey.u, CharKey.e, CharKey.d, CharKey.t, };

	public Action showInventory() throws ActionCancelException {
		Equipment.eqMode = true;
		List<MenuItem> inventory = player.getInventory();
		int xpos = 1;
		int ypos = 0;
		MenuBox menuBox = new MenuBox(si);
		menuBox.setHeight(11);
		menuBox.setWidth(50);
		menuBox.setPosition(1, 8);
		menuBox.setBorder(false);
		menuBox.setMenuItems(inventory);

		MenuBox itemUsageChoices = new MenuBox(si);
		itemUsageChoices.setHeight(9);
		itemUsageChoices.setWidth(20);
		itemUsageChoices.setPosition(52, 15);
		itemUsageChoices.setBorder(false);
		itemUsageChoices.setMenuItems(vecItemUsageChoices);
		itemUsageChoices.clearBox();

		TextBox itemDescription = new TextBox(si);
		itemDescription.setBounds(52, 9, 25, 5);
		si.saveBuffer();
		si.cls();
		si.print(xpos, ypos, "------------------------------------------------------------------------",
				ConsoleSystemInterface.DARK_RED);
		si.print(xpos, ypos + 1, "Inventory", ConsoleSystemInterface.RED);
		si.print(xpos, ypos + 2, "------------------------------------------------------------------------",
				ConsoleSystemInterface.DARK_RED);
		si.print(xpos + 2, ypos + 3, "1. Weapon:    " + player.getEquipedWeaponDescription());
		si.print(xpos + 2, ypos + 4, "2. Readied:   " + player.getSecondaryWeaponDescription());
		si.print(xpos + 2, ypos + 5, "3. Armor:     " + player.getArmorDescription());
		si.print(xpos + 2, ypos + 6, "4. Shield:    " + player.getAccDescription());
		si.print(xpos, ypos + 7, "------------------------------------------------------------------------",
				ConsoleSystemInterface.DARK_RED);
		menuBox.draw();
		si.print(xpos, 24, "[Space] to continue, Up and Down to browse");
		si.refresh();
		Item selected = null;

		Action selectedAction = null;
		do {
			try {
				Equipment eqs = (Equipment) menuBox.getSelectionAKS(additionalKeys);
				if (eqs == null)
					break;
				selected = eqs.getItem();
			} catch (AdditionalKeysSignal aks) {
				switch (aks.getKeyCode()) {
				case CharKey.N1: // Unequip Weapon
					if (player.getWeapon() != null) {
						selectedAction = new Unequip();
						selectedAction.setPerformer(player);
						selectedAction.setEquipedItem(player.getWeapon());
						si.restore();
						return selectedAction;
					} else {
						continue;
					}
				case CharKey.N2: // Unequip Secondary Weapon
					if (player.getSecondaryWeapon() != null) {
						selectedAction = new Unequip();
						selectedAction.setPerformer(player);
						selectedAction.setEquipedItem(player.getSecondaryWeapon());
						si.restore();
						return selectedAction;
					} else {
						continue;
					}
				case CharKey.N3: // Unequip Armor
					if (player.getArmor() != null) {
						selectedAction = new Unequip();
						selectedAction.setPerformer(player);
						selectedAction.setEquipedItem(player.getArmor());
						si.restore();
						return selectedAction;
					} else {
						continue;
					}
				case CharKey.N4: // Unequip Shield
					if (player.getShield() != null) {
						selectedAction = new Unequip();
						selectedAction.setPerformer(player);
						selectedAction.setEquipedItem(player.getShield());
						si.restore();
						return selectedAction;
					} else {
						continue;
					}
				}
			}
			if (selected == null) {
				break;
			}
			si.print(52, 8, fill(selected.getDescription(), 25), ConsoleSystemInterface.RED);
			itemDescription.clear();
			itemDescription.setText(selected.getDefinition().getMenuDescription());

			itemUsageChoices.draw();
			itemDescription.draw();
			SimpleMenuItem choice = determineChoice(itemUsageChoices);

			if (choice != null) {
				switch (choice.getValue()) {
				case 1: // Use
					Use use = new Use();
					use.setPerformer(player);
					use.setItem(selected);
					si.restore();
					return use;
				case 2: // Equip
					Equip equip = new Equip();
					equip.setPerformer(player);
					equip.setItem(selected);
					si.restore();
					return equip;
				case 3: // Drop
					Drop drop = new Drop();
					drop.setPerformer(player);
					drop.setItem(selected);
					si.restore();
					return drop;
				case 4: // Throw
					Throw throwx = new Throw();
					throwx.setPerformer(player);
					throwx.setItem(selected);
					si.restore();
					throwx.setPosition(pickPosition("Throw where?", CharKey.SPACE));
					return throwx;
				default: // Cancel
					break;
				}
			}
			si.print(52, 8, fill("", 25));
			itemUsageChoices.clearBox();
			itemDescription.clearBox();

		} while (selected != null);
		si.restore();
		Equipment.eqMode = false;
		return null;
	}

	private SimpleMenuItem determineChoice(MenuBox itemUsageChoices) {
		SimpleMenuItem choice = null;
		try {
			choice = (SimpleMenuItem) itemUsageChoices.getNullifiedSelection(itemUsageAdditionalKeys);
		} catch (AdditionalKeysSignal aks) {
			switch (aks.getKeyCode()) {
			case CharKey.u:
				choice = (SimpleMenuItem) vecItemUsageChoices.get(0);
				break;
			case CharKey.e:
				choice = (SimpleMenuItem) vecItemUsageChoices.get(1);
				break;
			case CharKey.d:
				choice = (SimpleMenuItem) vecItemUsageChoices.get(2);
				break;
			case CharKey.t:
				choice = (SimpleMenuItem) vecItemUsageChoices.get(3);
				break;
			}
		}
		return choice;
	}

	/**
	 * Shows a message immediately; useful for system messages.
	 * 
	 * @param x the message to be shown
	 */
	public void showMessage(String x) {
		messageBox.setForeColor(ConsoleSystemInterface.RED);
		messageBox.addText(x);
		messageBox.draw();
		si.refresh();
	}

	public void showImportantMessage(String x) {
		showMessage(x);
		si.waitKey(CharKey.SPACE);
	}

	public void showSystemMessage(String x) {
		messageBox.setForeColor(ConsoleSystemInterface.RED);
		messageBox.setText(x);
		messageBox.draw();
		si.refresh();
		si.waitKey(CharKey.SPACE);
	}

	public void showMessageHistory() {
		si.saveBuffer();
		si.cls();
		si.print(1, 0, "Message Buffer", CharAppearance.DARK_RED);
		for (int i = 0; i < 22; i++) {
			if (i >= messageHistory.size())
				break;
			si.print(1, i + 2, messageHistory.get(messageHistory.size() - 1 - i), CharAppearance.RED);
		}

		si.print(55, 24, "[ Space to Continue ]");
		si.waitKey(CharKey.SPACE);
		si.restore();
	}

	public void showPlayerStats() {
		si.saveBuffer();
		si.cls();
		si.print(1, 0, player.getName() + " the level " + player.getPlayerLevel() + " " + player.getClassString() + " "
				+ player.getStatusString(), ConsoleSystemInterface.RED);
		si.print(1, 1, "Sex: " + (player.getSex() == Player.MALE ? "M" : "F"));
		si.print(1, 2, "Hits: " + player.getHits() + "/" + player.getHitsMax() + " Hearts: " + player.getHearts() + "/"
				+ player.getHeartsMax() + " Gold: " + player.getGold() + " Keys: " + player.getKeys());
		si.print(1, 3, "Carrying: " + player.getItemCount() + "/" + player.getCarryMax());
		si.print(1, 5, "Attack      +" + player.getAttack());
		si.print(1, 6, "Soul Power  +" + player.getSoulPower());
		si.print(1, 7, "Evade       " + player.getEvadeChance() + "%");
		si.print(1, 8, "Combat      " + (50 - player.getAttackCost()));
		si.print(1, 9, "Invocation  " + (50 - player.getCastCost()));
		si.print(1, 10, "Movement    " + (50 - player.getWalkCost()));

		si.print(1, 11, "Experience  " + player.getXp() + "/" + player.getNextXP());

		si.print(1, 13, "Weapon Profficiences", ConsoleSystemInterface.RED);
		si.print(1, 14, "Hand to hand             Whips                    Projectiles", ConsoleSystemInterface.RED);
		si.print(1, 15, "Daggers                  Maces                    Bows/Xbows", ConsoleSystemInterface.RED);
		si.print(1, 16, "Swords                   Pole                     Machinery", ConsoleSystemInterface.RED);
		si.print(1, 17, "Spears                   Rings                    Shields", ConsoleSystemInterface.RED);

		String[] wskills = ItemDefinition.CATS;
		int cont = 0;
		for (int i = 0; i < wskills.length; i++) {
			if (i % 4 == 0)
				cont++;
			si.print((cont - 1) * 25 + 14, 14 + i - ((cont - 1) * 4), verboseSkills[player.weaponSkill(wskills[i])]);
		}

		si.print(1, 19, "Attack Damage  ", ConsoleSystemInterface.RED);
		si.print(1, 20, "Actual Defense ", ConsoleSystemInterface.RED);
		si.print(1, 21, "Shield Rates   ", ConsoleSystemInterface.RED);

		si.print(16, 19, "" + player.getWeaponAttack(), ConsoleSystemInterface.WHITE);
		si.print(16, 20,
				player.getArmorDefense() + (player.getDefenseBonus() != 0 ? "+" + player.getDefenseBonus() : ""),
				ConsoleSystemInterface.WHITE);
		si.print(16, 21,
				"Block " + player.getShieldBlockChance() + "% Coverage " + player.getShieldCoverageChance() + "%",
				ConsoleSystemInterface.WHITE);

		si.print(1, 23, "[ Press Space to continue ]");
		si.refresh();
		si.waitKey(CharKey.SPACE);
		si.restore();
	}

	public Action showSkills() throws ActionCancelException {
		Debug.enterMethod(this, "showSkills");
		si.saveBuffer();
		List<MenuItem> skills = player.getAvailableSkills();
		MenuBox menuBox = new MenuBox(si);
		menuBox.setHeight(14);
		menuBox.setWidth(33);
		menuBox.setBorder(true);
		menuBox.setForeColor(ConsoleSystemInterface.RED);
		menuBox.setPosition(24, 4);
		menuBox.setMenuItems(skills);
		menuBox.setTitle("Skills");
		menuBox.setPromptSize(0);
		menuBox.draw();
		si.refresh();
		Skill selectedSkill = (Skill) menuBox.getSelection();
		if (selectedSkill == null) {
			si.restore();
			Debug.exitMethod("null");
			return null;
		}
		si.restore();
		if (selectedSkill.isSymbolic()) {
			Debug.exitMethod("null");
			return null;
		}

		Action selectedAction = selectedSkill.getAction();
		selectedAction.setPerformer(player);
		if (selectedAction.canPerform(player))
			setTargets(selectedAction);
		else
			level.addMessage(selectedAction.getInvalidationMessage());

		Debug.exitMethod(selectedAction);
		return selectedAction;
	}

	public void levelUp() {
		showMessage("You gained a level!, [Press Space to continue]");
		si.waitKey(CharKey.SPACE);
		if (player.deservesAdvancement(player.getPlayerLevel())) {
			List<Advancement> advancements = player.getAvailableAdvancements();
			if (!advancements.isEmpty()) {
				Advancement playerChoice = Display.thus.showLevelUp(advancements);
				playerChoice.advance(player);
				player.getGameSessionInfo().addHistoryItem("went for " + playerChoice.getName());
			}
		}
		if (player.deservesStatAdvancement(player.getPlayerLevel())) {
			List<Advancement> advancements = player.getAvailableStatAdvancements();
			if (!advancements.isEmpty()) {
				Advancement playerChoice = Display.thus.showLevelUp(advancements);
				playerChoice.advance(player);
				player.getGameSessionInfo().addHistoryItem("went for " + playerChoice.getName());
			}
		}
		si.saveBuffer();
		((CharDisplay) Display.thus).showBoxedMessage("LEVEL UP!", player.getLastIncrementString(), 3, 4, 30, 10);
		player.resetLastIncrements();
		si.restore();
		si.refresh();
	}

	public Action selectCommand(CharKey input) {
		Debug.enterMethod(this, "selectCommand", input);
		int com = getRelatedCommand(input.code);
		informPlayerCommand(com);
		Action ret = actionSelectedByCommand;
		actionSelectedByCommand = null;
		Debug.exitMethod(ret);
		return ret;
	}

	protected void doShowSkills() {
		addMessage(new Message("- Cancelled", player.getPosition()));
		eraseOnArrival = true;
		si.refresh();
		actionSelectedByCommand = null;
	}

	protected void doHelp() {
		si.saveBuffer();
		Display.thus.showHelp();
		si.restore();
	}

//	Runnable interface
	public void run() {
		// Void
	}

//	IO Utility    
	public void waitKey() {
		CharKey x = new CharKey(CharKey.NONE);
		while (x.code == CharKey.NONE)
			x = si.inkey();
	}

	private void drawLineTo(int x, int y, char icon, int color) {
		Position target = new Position(x, y);
		Line line = new Line(PC_POS, target);
		Position tmp = line.next();
		while (!tmp.equals(target)) {
			tmp = line.next();
			si.print(tmp.x, tmp.y, icon, color);
		}

	}

	private Position getNearestMonsterPosition() {
		VMonster monsters = level.getMonsters();
		Monster nearMonster = null;
		int minDist = 150;
		int maxDist = 15;
		for (int i = 0; i < monsters.size(); i++) {
			Monster monster = monsters.get(i);
			if (monster.getPosition().z() != level.getPlayer().getPosition().z())
				continue;
			int distance = Position.flatDistance(level.getPlayer().getPosition(), monster.getPosition());
			if (distance < maxDist && distance < minDist && player.sees(monster)) {
				minDist = distance;
				nearMonster = monster;
			}
		}
		if (nearMonster != null)
			return nearMonster.getPosition();
		else
			return null;
	}

	public ArrayList<String> getMessageBuffer() {
		if (messageHistory.size() > 20)
			return new ArrayList<>(messageHistory.subList(messageHistory.size() - 21, messageHistory.size()));
		else
			return messageHistory;
	}

	protected void examineLevelMap() {
		si.saveBuffer();
		si.cls();
		int lw = level.getWidth();
		int lh = level.getHeight();
		int remnantx = (int) ((80 - (lw)) / 2.0d);

		int pages = (lh - 1) / 23 + 1;
		int cellColor = 0;
		Position runner = new Position(0, 0, player.getPosition().z);
		for (int i = 1; i <= pages; i++) {
			si.cls();
			for (int ii = 0; ii < 23; ii++) {
				int y = (i - 1) * 23 + ii;
				if (y >= level.getHeight())
					break;
				runner.y = y;
				runner.x = 0;
				for (int x = 0; x < level.getWidth(); x++, runner.x++) {
					cellColor = findCellColor(runner, y, x);
					si.safeprint(remnantx + x, ii, '.', cellColor);
				}
			}
			si.print(5, 24, "Page " + i, ConsoleSystemInterface.RED);
			si.refresh();
			si.waitKey(CharKey.SPACE);
		}

		si.restore();
		si.refresh();

	}

	private int findCellColor(Position runner, int y, int x) {
		if (!level.remembers(x, y))
			return ConsoleSystemInterface.BLACK;
		else {
			Cell current = level.getMapCell(x, y, player.getPosition().z);
			Feature currentF = level.getFeatureAt(x, y, player.getPosition().z);
			int cellColor = ConsoleUtils.findCurrentCellColor(current, currentF, level.isVisible(x, y),
					level.getExitOn(runner));
			if (player.getPosition().x == x && player.getPosition().y == y)
				cellColor = ConsoleSystemInterface.RED;
			return cellColor;
		}
	}

	@Override
	protected int getXrange() {
		return xrange;
	}

	@Override
	protected int getYrange() {
		return yrange;
	}

}

package crl.ui.graphicsUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import sz.csi.CharKey;
import sz.csi.textcomponents.MenuItem;
import sz.gadgets.*;
import sz.util.*;
import crl.action.*;
import crl.action.Action;
import crl.ui.effects.*;
import crl.ui.graphicsUI.components.GFXButton;
import crl.ui.graphicsUI.effects.GFXEffect;
import crl.player.*;
import crl.player.advancements.Advancement;
import crl.item.*;
import crl.level.*;
import crl.npc.*;
import crl.monster.*;
import crl.feature.*;
import crl.game.*;
import crl.actor.*;
import crl.conf.gfx.data.GFXConfiguration;
import crl.ui.*;

/**
 * Shows the level using characters. Informs the Actions and Commands of the
 * player. Must be listening to a System Interface
 */

public class GFXUserInterface extends UserInterface implements Runnable {
	private static final String BORDERS_FILE = "gfx/barrett-interface.gif"; // TODO: Move to GFXConfiguration
	private static final int BORDERS_SCALE = 1; // TODO: Move to GFXConfiguration
	private static final int BORDERS_SIZE = 32; // TODO: Move to GFXConfiguration

	private int STANDARD_WIDTH;
	// Attributes
	private int xrange;
	private int yrange;

	// Components
	public SwingInformBox messageBox;
	public AddornedBorderTextArea persistantMessageBox;
	private MerchantBox merchantBox;

	private MultiItemsBox multiItemsBox;
	private HelpBox helpBox;
	private Monster lockedMonster;
	private Action target;

	private boolean eraseOnArrival; // Erase the buffer upon the arrival of a new msg
	private boolean flipFacing;
	private Vector<String> messageHistory = new Vector<String>(10);

	// Relations

	private transient SwingSystemInterface si;

	private Font FNT_MESSAGEBOX;
	private Font FNT_PERSISTANTMESSAGEBOX;

	private BufferedImage HEALTH_RED;
	private BufferedImage HEALTH_DARK_RED;
	private BufferedImage HEALTH_MAGENTA;
	private BufferedImage HEALTH_WHITE;
	private BufferedImage HEALTH_YELLOW;
	private BufferedImage HEALTH_BROWN;
	private BufferedImage HEALTH_PURPLE;
	private BufferedImage HEART_TILE;
	private BufferedImage GOLD_TILE;
	private BufferedImage KEY_TILE;
	private BufferedImage TILE_MORNING_TIME;
	private BufferedImage TILE_NOON_TIME;
	private BufferedImage TILE_AFTERNOON_TIME;
	private BufferedImage TILE_DUSK_TIME;
	private BufferedImage TILE_NIGHT_TIME;
	private BufferedImage TILE_DAWN_TIME;
	private BufferedImage TILE_NO_SHOT;
	private BufferedImage TILE_SHOT_II;
	private BufferedImage TILE_SHOT_III;
	private BufferedImage TILE_LINE_STEPS;
	private BufferedImage TILE_LINE_AIM;
	private BufferedImage TILE_SCAN;
	private BufferedImage TILE_WEAPON_BACK;
	private BufferedImage TILE_HEALTH_BACK;
	private BufferedImage TILE_TIME_BACK;
	private BufferedImage IMG_STATUSSCR_BGROUND;
	private BufferedImage BORDER1;
	private BufferedImage BORDER2;
	private BufferedImage BORDER3;
	private BufferedImage BORDER4;
	private BufferedImage IMG_AXE;
	private BufferedImage IMG_BIBLE;
	private BufferedImage IMG_CROSS;
	private BufferedImage IMG_DAGGER;
	private BufferedImage IMG_HOLY;
	private BufferedImage IMG_CRYSTAL;
	private BufferedImage IMG_FIST;
	private BufferedImage IMG_STOPWATCH;
	private BufferedImage BLOOD1;
	private BufferedImage BLOOD2;
	private BufferedImage IMG_EXIT_BTN;
	private BufferedImage IMG_OK_BTN;
	private BufferedImage IMG_BUY_BTN;
	private BufferedImage IMG_YES_BTN;
	private BufferedImage IMG_NO_BTN;
	private BufferedImage IMG_ICON;
	private Color COLOR_BORDER_OUT, COLOR_BORDER_IN, COLOR_WINDOW_BACKGROUND;
	private Color COLOR_LAST_MESSAGE = Color.WHITE, COLOR_OLD_MESSAGE = Color.GRAY;
	private static final Color WATERCOLOR_BLOCKED = new Color(0, 50, 100, 200), WATERCOLOR = new Color(0, 70, 120, 200),
			RAINCOLOR = new Color(180, 200, 250, 100), THUNDERCOLOR = new Color(180, 200, 200, 150),
			FOGCOLOR = new Color(200, 200, 200, 200);

	protected GFXConfiguration configuration;

	/**
	 * Default constructor
	 * 
	 * @param configuration Configuration for this user interface
	 */
	public GFXUserInterface(GFXConfiguration configuration) {
		this.configuration = configuration;
	}

	// Setters
	/**
	 * Sets the object which will be informed of the player commands. this
	 * corresponds to the Game object
	 */

	// Getters

	// Smart Getters
	public Position getAbsolutePosition(Position insideLevel) {
		Position relative = Position.subs(insideLevel, player.getPosition());
		return Position.add(PC_POS, relative);
	}

	/*
	 * public Position VP_START = new Position(0,0), VP_END = new Position (31,18),
	 * PC_POS = new Position (12,9);
	 */

	public Position VP_START = new Position(0, 0), VP_END = new Position(5, 5), PC_POS = new Position(3, 3);

	private Position CAMERA = new Position(-32, -32); // TODO: Read from configuration
	private int cameraScale = 2; // TODO: Read from configuration

	public void setFlipFacing(boolean val) {
		flipFacing = val;
	}

	private boolean[][] FOVMask;

	private Image getImageForMystic(int mysticID) {
		switch (mysticID) {
		case Player.AXE:
			return IMG_AXE;
		case Player.BIBLE:
			return IMG_BIBLE;
		case Player.CROSS:
			return IMG_CROSS;
		case Player.DAGGER:
			return IMG_DAGGER;
		case Player.HOLY:
			return IMG_HOLY;
		case Player.SACRED_CRYSTAL:
			return IMG_CRYSTAL;
		case Player.SACRED_FIST:
			return IMG_FIST;
		case Player.STOPWATCH:
			return IMG_STOPWATCH;
		}
		return null;
	}

	private Color TRANSPARENT_GRAY = new Color(20, 20, 20, 180);
	private Color MAP_NOSOLID_LOS = new Color(204, 182, 116);
	private Color MAP_NOSOLID = new Color(148, 122, 60);
	private Color MAP_SOLID = new Color(180, 154, 68);

	private void examineLevelMap() {
		messageBox.setVisible(false);
		isCursorEnabled = false;
		si.saveBuffer();
		// si.drawImage(GFXDisplay.IMG_FRAME);
		int lw = level.getWidth();
		int lh = level.getHeight();
		int sw = this.configuration.getScreenWidth();
		int sh = this.configuration.getScreenHeight();
		int remnantx = (int) ((sw - 60 - (lw * 3)) / 2.0d);
		int remnanty = (int) ((sh - 120 - (lh * 3)) / 2.0d);
		Graphics2D g = si.getGraphics2D();
		g.setColor(TRANSPARENT_GRAY);
		g.fillRect(0, 0, sw, sh);
		Color cellColor = null;
		Position runner = new Position(0, 0, player.getPosition().z);
		for (int x = 0; x < level.getWidth(); x++, runner.x++, runner.y = 0)
			for (int y = 0; y < level.getHeight(); y++, runner.y++) {
				if (!level.remembers(x, y))
					// cellColor = Color.BLACK;
					continue;
				else {
					Cell current = level.getMapCell(runner);
					Feature currentF = level.getFeatureAt(runner);
					if (level.isVisible(x, y)) {
						if (current == null)
							// cellColor = Color.BLACK;
							continue;
						else if (level.getExitOn(runner) != null)
							cellColor = Color.RED;
						else if (current.isSolid() || (currentF != null && currentF.isSolid()))
							cellColor = MAP_SOLID;
						else
							cellColor = MAP_NOSOLID_LOS;

					} else {
						if (current == null)
							// cellColor = Color.BLACK;
							continue;
						else if (level.getExitOn(runner) != null)
							cellColor = Color.RED;
						else if (current.isSolid() || (currentF != null && currentF.isSolid()))
							cellColor = MAP_SOLID;
						else
							cellColor = MAP_NOSOLID;
					}
					if (player.getPosition().x == x && player.getPosition().y == y)
						cellColor = Color.RED;
				}
				g.setColor(cellColor);
				// g.fillOval(30+remnantx+x*5, 30+remnanty+y*5, 5,5);
				g.fillRect(30 + remnantx + x * 3, 30 + remnanty + y * 3, 3, 3);
			}
		si.refresh();

		si.waitKey(CharKey.SPACE);
		messageBox.setVisible(true);
		isCursorEnabled = true;
		si.restore();
		si.refresh();

	}

	/*
	 * Expensively render the minimap as part of the HUD instead of being a separete
	 * mode.
	 */
	private void renderMiniMap() {
		int lw = level.getWidth();
		int lh = level.getHeight();
		int sw = this.configuration.getScreenWidth();
		int sh = this.configuration.getScreenHeight();
		int mapX = sw - 60 - (lw * 3);
		int mapY = sh - 60 - (lh * 3);
		Graphics2D g = si.getGraphics2D();
		Color cellColor = null;
		Position runner = new Position(0, 0, player.getPosition().z);
		for (int x = 0; x < level.getWidth(); x++, runner.x++, runner.y = 0) {
			for (int y = 0; y < level.getHeight(); y++, runner.y++) {
				if (player.getPosition().x == x && player.getPosition().y == y) {
					cellColor = Color.RED;
				} else if (!level.remembers(x, y)) {
					continue;
				} else {
					Cell current = level.getMapCell(runner);
					if (current == null)
						continue;
					if (level.getExitOn(runner) != null) {
						cellColor = Color.RED;
					} else {
						Feature currentF = level.getFeatureAt(runner);
						if (current.isSolid() || (currentF != null && currentF.isSolid())) {
							cellColor = MAP_SOLID;
						} else if (level.isVisible(x, y)) {
							cellColor = MAP_NOSOLID_LOS;
						} else {
							cellColor = MAP_NOSOLID;
						}
					}
				}
				g.setColor(cellColor);
				g.fillRect(mapX + x * 3, mapY + y * 3, 3, 3);
			}
		}
	}

	private void enterScreen() {
		messageBox.setVisible(false);
		isCursorEnabled = false;
	}

	private void leaveScreen() {
		messageBox.setVisible(true);
		isCursorEnabled = true;
	}

	public void showMessageHistory() {
		enterScreen();
		si.saveBuffer();
		si.drawImage(IMG_STATUSSCR_BGROUND);
		si.print(1, 1, "Message Buffer", GFXDisplay.COLOR_BOLD);
		for (int i = 0; i < 22; i++) {
			if (i >= messageHistory.size())
				break;
			si.print(1, i + 2, messageHistory.elementAt(messageHistory.size() - 1 - i), Color.WHITE);
		}

		si.print(55, 24, "[ Space to Continue ]", Color.WHITE);
		si.refresh();
		si.waitKey(CharKey.SPACE);
		si.restore();
		si.refresh();
		leaveScreen();
	}

	// Interactive Methods
	public void doLook() {
		Position offset = new Position(0, 0);

		messageBox.setForeground(COLOR_LAST_MESSAGE);
		si.saveBuffer();
		Monster lookedMonster = null;
		while (true) {
			int cellHeight = 0;
			Position browser = Position.add(player.getPosition(), offset);
			String looked = "";
			si.restore();
			if (FOVMask[PC_POS.x + offset.x][PC_POS.y + offset.y]) {
				Cell choosen = level.getMapCell(browser);
				if (choosen != null)
					cellHeight = choosen.getHeight();
				Feature feat = level.getFeatureAt(browser);
				Vector<MenuItem> items = level.getItemsAt(browser);
				Item item = null;
				if (items != null) {
					item = (Item) items.elementAt(0);
				}
				lookedMonster = null;
				Actor actor = level.getActorAt(browser);
				if (choosen != null)
					looked += choosen.getDescription();
				if (level.getBloodAt(browser) != null)
					looked += "{bloody}";
				if (feat != null)
					looked += ", " + feat.getDescription();
				if (item != null)
					if (items.size() == 1)
						looked += ", " + item.getDescription();
					else
						looked += ", " + item.getDescription() + " and some items";
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
			drawImageVP((PC_POS.x + offset.x) * 32 - 2, (PC_POS.y + offset.y) * 32 - 2 - 4 * cellHeight, TILE_SCAN);
			si.refresh();
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
				offset.add(Action.directionToVariation(UISelector.toIntDirection(x)));

				if (offset.x >= xrange)
					offset.x = xrange;
				if (offset.x <= -xrange)
					offset.x = -xrange;
				if (offset.y >= yrange)
					offset.y = yrange;
				if (offset.y <= -yrange)
					offset.y = -yrange;
			}
		}
		messageBox.setText("Look mode off");
		si.restore();
		si.refresh();

	}

	public synchronized void launchMerchant(Merchant who) {
		Debug.enterMethod(this, "launchMerchant", who);
		Equipment.eqMode = true;
		Item.shopMode = true;
		Vector<MenuItem> merchandise = who.getMerchandiseFor(player);
		if (merchandise == null || merchandise.isEmpty()) {
			chat(who);
			Debug.exitMethod();
			return;
		}
		merchantBox.setMerchandise(merchandise);
		merchantBox.setVisible(true);
		merchantBox.setPrompt("Greetings " + player.getName() + "... I am " + who.getName() + ", the "
				+ who.getMerchandiseTypeDesc() + " merchant. May I interest you in an item?");
		while (true) {
			merchantBox.setGold(player.getGold());
			merchantBox.informChoice(Thread.currentThread());
			try {
				this.wait();
			} catch (InterruptedException ie) {

			}
			Item choice = merchantBox.getSelection();
			if (choice == null)
				break;

			if (player.getGold() >= choice.getGoldPrice()) {
				player.reduceGold(choice.getGoldPrice());
				if (player.canCarry())
					player.addItem(choice);
				else
					level.addItem(player.getPosition(), choice);
				merchantBox.setPrompt("Thanks!, May I interest you in something else?");
			} else {
				merchantBox.setPrompt("I am afraid you don't have enough gold");
			}

		}
		merchantBox.setVisible(false);
		si.recoverFocus();
		Equipment.eqMode = false;
		Item.shopMode = false;
		Debug.exitMethod();
	}

	public void chat(NPC who) {
		Debug.enterMethod(this, "chat", who);
		si.saveBuffer();
		((GFXDisplay) Display.thus).showTextBox(who.getDescription() + " says: \n   \"" + who.getTalkMessage() + "\"",
				280, 30, 330, 170);
		si.refresh();
		// waitKey();
		si.restore();
		Debug.exitMethod();
	}

	public boolean promptChat(NPC who) {
		si.saveBuffer();
		boolean ret = ((GFXDisplay) Display.thus).showTextBoxPrompt(who.getTalkMessage(), 280, 30, 330, 170);
		si.refresh();
		// waitKey();
		si.restore();
		return ret;
	}

	// Drawing Methods
	public void drawEffect(Effect what) {
		if (what == null)
			return;
		if (insideViewPort(getAbsolutePosition(what.getPosition()))) {
			((GFXEffect) what).drawEffect(this, si);
		}
	}

	@Override
	public boolean isOnFOVMask(int x, int y) {
		return FOVMask[x][y];
	}

	private void drawLevel() {
		Debug.enterMethod(this, "drawLevel");
		// Cell[] [] cells =
		// level.getCellsAround(player.getPosition().x,player.getPosition().y,
		// player.getPosition().z, range);
		Cell[][] rcells = level.getMemoryCellsAround(player.getPosition().x, player.getPosition().y,
				player.getPosition().z, xrange, yrange);
		Cell[][] vcells = level.getVisibleCellsAround(player.getPosition().x, player.getPosition().y,
				player.getPosition().z, xrange, yrange);

		Position runner = new Position(player.getPosition().x - xrange, player.getPosition().y - yrange,
				player.getPosition().z);

		monstersOnSight.removeAllElements();
		featuresOnSight.removeAllElements();
		itemsOnSight.removeAllElements();

		/*
		 * for (int x = 0; x < vcells.length; x++){ for (int y=0; y<vcells[0].length;
		 * y++){
		 */
		for (int y = 0; y < vcells[0].length; y++) {
			for (int x = 0; x < vcells.length; x++) {
				FOVMask[PC_POS.x - xrange + x][PC_POS.y - yrange + y] = false;
				int cellHeight = 0;
				if (vcells[x][y] == null || vcells[x][y].getID().equals("AIR")) {
					if (rcells[x][y] != null && !rcells[x][y].getAppearance().getID().equals("NOTHING")) {
						GFXAppearance app = (GFXAppearance) rcells[x][y].getAppearance();
						try {
							Image cellImage;
							if (level.isDay())
								cellImage = app.getDarkImage();
							else
								cellImage = app.getDarkniteImage();
							drawImageVP((PC_POS.x - xrange + x) * 32,
									(PC_POS.y - yrange + y) * 32 - 17 - app.getSuperHeight(), cellImage);
						} catch (NullPointerException npe) {
							Color c = si.getGraphics2D().getColor();
							si.getGraphics2D().setColor(Color.RED);
							si.getGraphics2D().fillRect((PC_POS.x - xrange + x) * STANDARD_WIDTH,
									(PC_POS.y - yrange + y) * STANDARD_WIDTH - 17 - app.getSuperHeight(),
									STANDARD_WIDTH, 49);
							si.getGraphics2D().setColor(c);
						}
					} else {
						// Draw nothing
						// si.drawImage((PC_POS.x-xrange+x)*32,(PC_POS.y-yrange+y)*32-17,
						// "gfx/black.gif");
						// si.print(PC_POS.x-xrange+x,PC_POS.y-yrange+y,
						// CharAppearance.getVoidAppearance().getChar(),
						// CharAppearance.getVoidAppearance().BLACK);
					}
				} else {
					cellHeight = vcells[x][y].getHeight();
					FOVMask[PC_POS.x - xrange + x][PC_POS.y - yrange + y] = true;
					String bloodLevel = level.getBloodAt(runner);
					GFXAppearance cellApp = (GFXAppearance) vcells[x][y].getAppearance();

					boolean frosty = false;
					if (level.getFrostAt(runner) != 0) {
						frosty = true;
						// TODO: Apply a blue tint
					}
					int depthFromPlayer = level.getDepthFromPlayer(player.getPosition().x - xrange + x,
							player.getPosition().y - yrange + y);
					if (depthFromPlayer != 0) {
						drawImageVP((PC_POS.x - xrange + x) * 32,
								(PC_POS.y - yrange + y) * 32 + depthFromPlayer * 10 - 17, cellApp.getDarkImage());
					} else {
						Image img;
						if (level.isDay())
							img = cellApp.getImage();
						else
							img = cellApp.getNiteImage();
						drawImageVP((PC_POS.x - xrange + x) * 32,
								(PC_POS.y - yrange + y) * 32 - 17 - cellApp.getSuperHeight(), img);
					}
					if (bloodLevel != null) {
						Image img = null;
						switch (Integer.parseInt(bloodLevel)) {
						case 0:
							img = BLOOD1;
							break;
						case 1:
							img = BLOOD2;
							break;
						}
						if (img != null) {
							drawImageVP((PC_POS.x - xrange + x) * 32,
									(PC_POS.y - yrange + y) * 32 - 4 * cellHeight - cellApp.getSuperHeight(), img);
						}
					}
				}
				runner.x++;
			}
			runner.x = player.getPosition().x - xrange;
			for (int x = 0; x < vcells.length; x++) {
				int cellHeight = 0;
				if (vcells[x][y] != null) {
					cellHeight = vcells[x][y].getHeight();
					Feature feat = level.getFeatureAt(runner);
					if (feat != null) {
						if (feat.isVisible()) {
							GFXAppearance featApp = (GFXAppearance) feat.getAppearance();
							drawImageVP((PC_POS.x - xrange + x) * 32 - featApp.getSuperWidth(),
									(PC_POS.y - yrange + y) * 32 - 4 * cellHeight - featApp.getSuperHeight(),
									featApp.getImage());
						}
					}

					SmartFeature sfeat = level.getSmartFeature(runner);
					if (sfeat != null) {
						if (sfeat.isVisible()) {
							GFXAppearance featApp = (GFXAppearance) sfeat.getAppearance();
							drawImageVP((PC_POS.x - xrange + x) * 32 - featApp.getSuperWidth(),
									(PC_POS.y - yrange + y) * 32 - 4 * cellHeight - featApp.getSuperHeight(),
									featApp.getImage());
						}
					}

					Vector<MenuItem> items = level.getItemsAt(runner);
					Item item = null;
					if (items != null) {
						item = (Item) items.elementAt(0);
					}
					if (item != null) {
						if (item.isVisible()) {
							GFXAppearance itemApp = (GFXAppearance) item.getAppearance();
							drawImageVP((PC_POS.x - xrange + x) * 32 - itemApp.getSuperWidth(),
									(PC_POS.y - yrange + y) * 32 - 4 * cellHeight - itemApp.getSuperHeight(),
									itemApp.getImage());
						}
					}

					if (yrange == y && x == xrange) {
						if (player.isInvisible()) {
							drawImageVP(PC_POS.x * 32, PC_POS.y * 32 - 4 * cellHeight,
									((GFXAppearance) AppearanceFactory.getAppearanceFactory().getAppearance("SHADOW"))
											.getImage());
						} else {
							GFXAppearance playerAppearance = (GFXAppearance) player.getAppearance();
							BufferedImage playerImage = (BufferedImage) playerAppearance.getImage();
							if (flipFacing) {
								playerImage = ImageUtils.vFlip(playerImage);
								// flipFacing = false;
							}
							int waterBonus = (level.getMapCell(player.getPosition()) != null
									&& level.getMapCell(player.getPosition()).isShallowWater()) ? 16 : 0;
							drawImageVP(PC_POS.x * 32 - playerAppearance.getSuperWidth(), PC_POS.y * 32
									- 4 * player.getStandingHeight() - playerAppearance.getSuperHeight() + waterBonus,
									playerImage);
						}
					}
					Monster monster = level.getMonsterAt(runner);

					if (monster != null && monster.isVisible()) {
						GFXAppearance monsterApp = (GFXAppearance) monster.getAppearance();
						int swimBonus = (monster.canSwim() && level.getMapCell(runner) != null
								&& level.getMapCell(runner).isShallowWater()) ? 16 : 0; // TODO: Overlap water on the
																						// monster, draw it lowly
						drawImageVP((PC_POS.x - xrange + x) * 32 - monsterApp.getSuperWidth(),
								(PC_POS.y - yrange + y) * 32 - 4 * cellHeight - monsterApp.getSuperHeight() + swimBonus,
								monsterApp.getImage());
					}
					// Draw Masks
					Color mask = null;

					// Water
					if (vcells[x][y].isWater()) {
						if (level.canFloatUpward(runner)) {
							mask = WATERCOLOR;
						} else {
							mask = WATERCOLOR_BLOCKED;

						}
					}
					if (mask != null) {
						si.getGraphics2D().setColor(mask);
						si.getGraphics2D().fillRect((PC_POS.x - xrange + x) * STANDARD_WIDTH + CAMERA.x,
								(PC_POS.y - yrange + y) * STANDARD_WIDTH + CAMERA.y, STANDARD_WIDTH, STANDARD_WIDTH);
					}
				}
				// runner.y++;
				runner.x++;
			}
			/*
			 * runner.y = player.getPosition().y-yrange; runner.x ++;
			 */
			runner.x = player.getPosition().x - xrange;
			runner.y++;
		}

		// Overlay
		// Draw Masks
		/*
		 * Color mask = null; //Rain if (player.getFlag(Consts.ENV_RAIN)) mask =
		 * RAINCOLOR;
		 * 
		 * //Thunderstorm if (player.getFlag(Consts.ENV_THUNDERSTORM)) mask =
		 * THUNDERCOLOR; //Fog if (player.getFlag(Consts.ENV_FOG)) mask = FOGCOLOR;
		 * 
		 * if (mask != null){ si.getGraphics2D().setColor(mask);
		 * si.getGraphics2D().fillRect(0,0, this.configuration.getScreenWidth(),
		 * this.configuration.getScreenHeight()); }
		 */

		Debug.exitMethod();
	}

	public void addMessage(Message message) {
		Debug.enterMethod(this, "addMessage", message);
		if (eraseOnArrival) {
			messageBox.clear();
			messageBox.setForeground(COLOR_LAST_MESSAGE);
			eraseOnArrival = false;
		}
		if (message.getLocation().z != player.getPosition().z
				|| !insideViewPort(getAbsolutePosition(message.getLocation()))) {
			Debug.exitMethod();
			return;
		}
		messageHistory.add(message.getText());
		if (messageHistory.size() > 500)
			messageHistory.removeElementAt(0);
		messageBox.addText(message.getText());
		dimMsg = 0;
		Debug.exitMethod();
	}

	/*
	 * private void drawCursor(){ /*if (isCursorEnabled){ si.restore(); Cell
	 * underlying = player.getLevel().getMapCell(tempCursorPosition);
	 * drawImageVP((PC_POS.x+tempCursorPositionScr.x)*32,(PC_POS.y+
	 * tempCursorPositionScr.y)*32-4*underlying.getHeight(), TILE_SCAN);
	 * si.refresh(); } }
	 */

	private boolean isCursorEnabled = false;

	private void drawPlayerStatus() {
		Debug.enterMethod(this, "drawPlayerStatus");
		Image foreColor;
		Image backColor;
		switch (((player.getHits() - 1) / 20) + 1) {
		case 1:
			foreColor = HEALTH_RED;
			backColor = HEALTH_WHITE;
			break;
		case 2:
			foreColor = HEALTH_DARK_RED;
			backColor = HEALTH_RED;
			break;
		default:
			foreColor = HEALTH_MAGENTA;
			backColor = HEALTH_DARK_RED;
			break;
		}

		Image timeTile = null;
		switch (level.getDayTime()) {
		case Level.MORNING:
			timeTile = TILE_MORNING_TIME;
			break;
		case Level.NOON:
			timeTile = TILE_NOON_TIME;
			break;
		case Level.AFTERNOON:
			timeTile = TILE_AFTERNOON_TIME;
			break;
		case Level.DUSK:
			timeTile = TILE_DUSK_TIME;
			break;
		case Level.NIGHT:
			timeTile = TILE_NIGHT_TIME;
			break;
		case Level.DAWN:
			timeTile = TILE_DAWN_TIME;
			break;
		}

		Image shotTile = TILE_NO_SHOT;
		if (player.getShotLevel() == 1)
			shotTile = TILE_SHOT_II;
		if (player.getShotLevel() == 2)
			shotTile = TILE_SHOT_III;
		if (shotTile != null)
			si.drawImage(18, 80, shotTile);
		int rest = ((player.getHits() - 1) % 20) + 1;

		si.printAtPixel(14, 30, player.getName() + ", the Lv" + player.getPlayerLevel() + " " + player.getClassString()
				+ " " + player.getScore() + " " + player.getStatusString(), Color.WHITE);
		si.drawImage(14, 35, TILE_WEAPON_BACK);
		si.drawImage(38, 35, TILE_HEALTH_BACK);

		for (int i = 0; i < 20; i++)
			if (i + 1 <= rest)
				si.drawImage(41 + (i * 6), 40, foreColor);
			else
				si.drawImage(41 + (i * 6), 40, backColor);

		if (player.getLevel().getBoss() != null) {
			int sixthiedBossHits = (int) Math.ceil(
					(player.getLevel().getBoss().getHits() * 60.0) / player.getLevel().getBoss().getMaxHits());
			Image foreColorB;
			Image backColorB;
			// switch (((player.getLevel().getBoss().getHits()-1) / 20) + 1){
			switch (((sixthiedBossHits - 1) / 20) + 1) {
			case 1:
				foreColorB = HEALTH_YELLOW;
				backColorB = HEALTH_WHITE;
				break;
			case 2:
				foreColorB = HEALTH_BROWN;
				backColorB = HEALTH_YELLOW;
				break;
			default:
				foreColorB = HEALTH_PURPLE;
				backColorB = HEALTH_BROWN;
				break;
			}

			int restB = ((sixthiedBossHits - 1) % 20) + 1;

			for (int i = 0; i < 20; i++) {
				si.drawImage(this.configuration.getScreenWidth() - 135 + (i * 6),
						this.configuration.getScreenHeight() - 60, i + 1 <= restB ? foreColorB : backColorB);
			}
		}

		// TODO: Add the background
		if (player.getPlayerClass() == Player.CLASS_VAMPIREKILLER) {
			if (player.getMysticWeapon() != -1)
				si.drawImage(18, 38, getImageForMystic(player.getMysticWeapon()));
		} else if (player.getWeapon() != null) {
			si.drawImage(18, 38, ((GFXAppearance) player.getWeapon().getAppearance()).getIconImage());
		}
		if (player.getLevel().getLevelNumber() != -1)
			si.printAtPixel(this.configuration.getScreenWidth() - 276, 50,
					"STAGE  " + player.getLevel().getLevelNumber() + " " + player.getLevel().getDescription(),
					Color.WHITE);
		else
			si.printAtPixel(this.configuration.getScreenWidth() - 276, 50, player.getLevel().getDescription(),
					Color.WHITE);

		// si.drawImage(759, 35, TILE_TIME_BACK);
		int timeTilePosition = this.configuration.getScreenWidth() - 77;
		si.drawImage(timeTilePosition, 38, timeTile);
		if (player.getFlag(Consts.ENV_FOG))
			si.printAtPixel(timeTilePosition, 30, "FOG", Color.GRAY);
		if (player.getFlag(Consts.ENV_RAIN))
			si.printAtPixel(timeTilePosition, 30, "RAIN", Color.BLUE);
		if (player.getFlag(Consts.ENV_SUNNY))
			si.printAtPixel(timeTilePosition, 30, "SUNNY", Color.YELLOW);
		if (player.getFlag(Consts.ENV_THUNDERSTORM))
			si.printAtPixel(timeTilePosition, 30, "STORM", Color.WHITE);

		si.drawImage(166, 42, HEART_TILE);
		si.printAtPixel(182, 51, "" + player.getHearts(), Color.WHITE);
		si.drawImage(206, 42, GOLD_TILE);
		si.printAtPixel(219, 51, "" + player.getGold(), Color.WHITE);
		si.drawImage(249, 42, KEY_TILE);
		si.printAtPixel(269, 51, "" + player.getKeys(), Color.WHITE);
		if (player.getHostage() != null) {
			Hostage h = player.getHostage();
			si.drawImage(18, 64, ((GFXAppearance) h.getAppearance()).getImage());
		}

		renderMiniMap();

		// si.printAtPixel(18,80,""+player.getHoverHeight(), Color.WHITE);
		Debug.exitMethod();
	}

	private void initProperties() {
		STANDARD_WIDTH = this.configuration.getNormalTileWidth();

		xrange = this.configuration.getScreenWidthInTiles();
		yrange = this.configuration.getScreenHeightInTiles();

		PC_POS = this.configuration.getPlayerLocationOnScreen();
		COLOR_WINDOW_BACKGROUND = this.configuration.getWindowBackgroundColour();
		COLOR_BORDER_IN = this.configuration.getBorderColourIn();
		COLOR_BORDER_OUT = this.configuration.getBorderColourOut();
		FNT_MESSAGEBOX = this.configuration.getMessageBoxFont();
		FNT_PERSISTANTMESSAGEBOX = this.configuration.getPersistantMessageBoxFont();
		IMG_STATUSSCR_BGROUND = this.configuration.getStatusScreenBackground();
	}

	public void init(SwingSystemInterface psi, UserCommand[] gameCommands, Action target) {
		Debug.enterMethod(this, "init");
		super.init(gameCommands);
		this.target = target;
		initProperties();
		// GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setDisplayMode(new
		// DisplayMode(800,600,8, DisplayMode.REFRESH_RATE_UNKNOWN));

		/*-- Assign values */
		si = psi;
		FOVMask = new boolean[80][25];
		si.getGraphics2D().setColor(Color.BLACK);
		si.getGraphics2D().fillRect(0, 0, 800, 600);
		si.refresh();

		/*-- Load Fonts */
		try {
			FNT_MESSAGEBOX = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("res/v5easter.ttf"))
					.deriveFont(Font.PLAIN, 15);
		} catch (FontFormatException ffe) {
			Game.crash("Error loading the font", ffe);
		} catch (IOException ioe) {
			Game.crash("Error loading the font", ioe);
		}

		/*-- Load UI Images */
		try {
			BufferedImage userInterfaceTileset = this.configuration.getImageConfiguration().getUserInterfaceTileset();
			BufferedImage viewportUserInterfaceTileset = this.configuration.getImageConfiguration()
					.getViewportUserInterfaceTileset();
			int viewportUserInterfaceScale = this.configuration.getViewportUserInterfaceScale();
			HEALTH_WHITE = ImageUtils.crearImagen(userInterfaceTileset, 198, 1, 5, 16);
			/* HEALTH_BLUE? unneeded */
			HEALTH_RED = ImageUtils.crearImagen(userInterfaceTileset, 210, 1, 5, 16);
			HEALTH_DARK_RED = ImageUtils.crearImagen(userInterfaceTileset, 216, 1, 5, 16);
			HEALTH_MAGENTA = ImageUtils.crearImagen(userInterfaceTileset, 222, 1, 5, 16);

			HEALTH_YELLOW = ImageUtils.crearImagen(userInterfaceTileset, 228, 1, 5, 16);
			HEALTH_BROWN = ImageUtils.crearImagen(userInterfaceTileset, 234, 1, 5, 16);
			HEALTH_PURPLE = ImageUtils.crearImagen(userInterfaceTileset, 240, 1, 5, 16);

			HEART_TILE = ImageUtils.crearImagen(userInterfaceTileset, 199, 20, 14, 12);
			GOLD_TILE = ImageUtils.crearImagen(userInterfaceTileset, 214, 19, 9, 13);
			KEY_TILE = ImageUtils.crearImagen(userInterfaceTileset, 224, 20, 13, 13);

			TILE_MORNING_TIME = ImageUtils.crearImagen(userInterfaceTileset, 1, 109, 49, 24);
			TILE_NOON_TIME = ImageUtils.crearImagen(userInterfaceTileset, 52, 109, 49, 24);
			TILE_AFTERNOON_TIME = ImageUtils.crearImagen(userInterfaceTileset, 103, 109, 49, 24);
			TILE_DUSK_TIME = ImageUtils.crearImagen(userInterfaceTileset, 154, 109, 49, 24);
			TILE_NIGHT_TIME = ImageUtils.crearImagen(userInterfaceTileset, 205, 109, 49, 24);
			TILE_DAWN_TIME = ImageUtils.crearImagen(userInterfaceTileset, 256, 109, 49, 24);

			// TILE_NO_SHO;
			TILE_SHOT_II = ImageUtils.crearImagen(userInterfaceTileset, 300, 3, 16, 16);
			TILE_SHOT_III = ImageUtils.crearImagen(userInterfaceTileset, 300, 20, 16, 16);

			TILE_LINE_STEPS = ImageUtils.crearImagen(viewportUserInterfaceTileset, 280 * viewportUserInterfaceScale,
					25 * viewportUserInterfaceScale, 6 * viewportUserInterfaceScale, 5 * viewportUserInterfaceScale);
			TILE_LINE_AIM = ImageUtils.crearImagen(viewportUserInterfaceTileset, 265 * viewportUserInterfaceScale,
					37 * viewportUserInterfaceScale, 36 * viewportUserInterfaceScale, 36 * viewportUserInterfaceScale);
			TILE_SCAN = ImageUtils.crearImagen(viewportUserInterfaceTileset, 302 * viewportUserInterfaceScale,
					37 * viewportUserInterfaceScale, 36 * viewportUserInterfaceScale, 36 * viewportUserInterfaceScale);

			TILE_WEAPON_BACK = ImageUtils.crearImagen(userInterfaceTileset, 173, 1, 24, 24);
			TILE_HEALTH_BACK = ImageUtils.crearImagen(userInterfaceTileset, 3, 34, 261, 24);
			TILE_TIME_BACK = ImageUtils.crearImagen(userInterfaceTileset, 246, 1, 22, 21);

			IMG_STATUSSCR_BGROUND = configuration.getUserInterfaceBackgroundImage();
			// ImageUtils.createImage("gfx/barrett-moon_2x.gif");

			BORDER1 = ImageUtils.crearImagen(BORDERS_FILE, 34 * BORDERS_SCALE, BORDERS_SCALE, BORDERS_SIZE,
					BORDERS_SIZE);
			BORDER2 = ImageUtils.crearImagen(BORDERS_FILE, BORDERS_SCALE, BORDERS_SCALE, BORDERS_SIZE,
					BORDERS_SIZE);
			BORDER3 = ImageUtils.crearImagen(BORDERS_FILE, 100 * BORDERS_SCALE, BORDERS_SCALE, BORDERS_SIZE,
					BORDERS_SIZE);
			BORDER4 = ImageUtils.crearImagen(BORDERS_FILE, 67 * BORDERS_SCALE, BORDERS_SCALE, BORDERS_SIZE,
					BORDERS_SIZE);

			IMG_AXE = ImageUtils.crearImagen("gfx/crl_features.gif", 48, 0, 16, 16);
			IMG_BIBLE = ImageUtils.crearImagen("gfx/crl_features.gif", 96, 0, 16, 16);
			IMG_CROSS = ImageUtils.crearImagen("gfx/crl_features.gif", 64, 0, 16, 16);
			IMG_DAGGER = ImageUtils.crearImagen("gfx/crl_features.gif", 32, 0, 16, 16);
			IMG_HOLY = ImageUtils.crearImagen("gfx/crl_features.gif", 112, 0, 16, 16);
			IMG_CRYSTAL = ImageUtils.crearImagen("gfx/crl_features.gif", 128, 0, 16, 16);
			IMG_FIST = ImageUtils.crearImagen("gfx/crl_features.gif", 136, 0, 16, 16);
			IMG_STOPWATCH = ImageUtils.crearImagen("gfx/crl_features.gif", 80, 0, 16, 16);

			/*
			 * COLOR_BORDER_IN = new Color(187,161,80); COLOR_BORDER_OUT = new
			 * Color(92,78,36);
			 */

			BLOOD1 = (BufferedImage) ((GFXAppearance) AppearanceFactory.getAppearanceFactory().getAppearance("BLOOD1"))
					.getImage();
			BLOOD2 = (BufferedImage) ((GFXAppearance) AppearanceFactory.getAppearanceFactory().getAppearance("BLOOD2"))
					.getImage();

			IMG_EXIT_BTN = ImageUtils.crearImagen(userInterfaceTileset, 65, 81, 60, 26);
			IMG_OK_BTN = ImageUtils.crearImagen(userInterfaceTileset, 2, 81, 60, 26);
			IMG_BUY_BTN = ImageUtils.crearImagen(userInterfaceTileset, 128, 81, 60, 26);
			IMG_YES_BTN = ImageUtils.crearImagen(userInterfaceTileset, 191, 81, 60, 26);
			IMG_NO_BTN = ImageUtils.crearImagen(userInterfaceTileset, 254, 81, 60, 26);

			IMG_ICON = ImageUtils.createImage("res/crl_icon.png");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.byebye(e.getMessage());
		}

		si.setIcon(IMG_ICON);
		si.setTitle("CastlevaniaRL v" + Game.getVersion());
		/*-- Init Components*/
		messageBox = new SwingInformBox();
		/* idList = new ListBox(psi); */
		messageBox.setBounds(16, this.configuration.getScreenHeight() - 10 * 24,
				this.configuration.getScreenWidth() - 32, 10 * 24);
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		messageBox.setBackground(Color.BLACK);
		messageBox.setFont(FNT_MESSAGEBOX);
		messageBox.setEditable(false);
		messageBox.setVisible(false);
		messageBox.setOpaque(false);
		messageBox.setLineWrap(true);
		messageBox.setWrapStyleWord(true);

		psi.add(messageBox);

		merchantBox = new MerchantBox(BORDER1, BORDER2, BORDER3, BORDER4, COLOR_BORDER_IN, COLOR_BORDER_OUT,
				BORDERS_SIZE, BORDERS_SIZE);
		merchantBox.setBounds(150, 60, 500, 410);
		merchantBox.setVisible(false);
		psi.add(merchantBox);

		multiItemsBox = new MultiItemsBox(BORDER1, BORDER2, BORDER3, BORDER4, COLOR_BORDER_IN, COLOR_BORDER_OUT,
				BORDERS_SIZE, BORDERS_SIZE);
		multiItemsBox.setBounds(250, 235, 300, 260);
		multiItemsBox.setVisible(false);
		psi.add(multiItemsBox);

		helpBox = new HelpBox(BORDER1, BORDER2, BORDER3, BORDER4, COLOR_BORDER_IN, COLOR_BORDER_OUT, BORDERS_SIZE,
				BORDERS_SIZE);
		helpBox.setBounds(12, 32, 770, 450);
		helpBox.setVisible(false);
		psi.add(helpBox);

		persistantMessageBox = new AddornedBorderTextArea(BORDER1, BORDER2, BORDER3, BORDER4, COLOR_BORDER_IN,
				COLOR_BORDER_OUT, BORDERS_SIZE, BORDERS_SIZE);
		persistantMessageBox.setBounds(this.configuration.getScreenWidth() - 280, 90, 260, 400);
		persistantMessageBox.setVisible(false);
		persistantMessageBox.setFont(FNT_PERSISTANTMESSAGEBOX);
		persistantMessageBox.setForeground(Color.WHITE);
		psi.add(persistantMessageBox);

		si.setVisible(true);

		Debug.exitMethod();
	}

	@Override
	public void setPersistantMessage(String description) {
		persistantMessageBox.setText(description);
		persistantMessageBox.setVisible(true);
	}

	/**
	 * Checks if the point, relative to the console coordinates, is inside the
	 * ViewPort
	 */
	public boolean insideViewPort(int x, int y) {
		// return (x>=VP_START.x && x <= VP_END.x && y >= VP_START.y && y <= VP_END.y);
		return (x >= 0 && x < FOVMask.length && y >= 0 && y < FOVMask[0].length) && FOVMask[x][y];
	}

	public boolean insideViewPort(Position what) {
		return insideViewPort(what.x, what.y);
	}

	public boolean isDisplaying(Actor who) {
		return insideViewPort(getAbsolutePosition(who.getPosition()));
	}

	private Position getNearestMonsterPosition() {
		VMonster monsters = level.getMonsters();
		Monster nearMonster = null;
		int minDist = 150;
		int maxDist = 15;
		for (int i = 0; i < monsters.size(); i++) {
			Monster monster = monsters.elementAt(i);
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

	private Position pickPosition(String prompt, int fireKeyCode) throws ActionCancelException {
		Debug.enterMethod(this, "pickPosition");
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		messageBox.setText(prompt);
		Position defaultTarget = null;
		Position nearest = getNearestMonsterPosition();
		if (nearest != null) {
			defaultTarget = nearest;
		} else {
			defaultTarget = null;
		}

		Position browser = null;
		Position offset = new Position(0, 0);
		if (lockedMonster != null) {
			if (!player.sees(lockedMonster) || lockedMonster.isDead()) {
				lockedMonster = null;
			} else
				defaultTarget = new Position(lockedMonster.getPosition());
		}

		if (defaultTarget == null) {
			offset = new Position(0, 0);
		} else {
			offset = new Position(defaultTarget.x - player.getPosition().x, defaultTarget.y - player.getPosition().y);
		}

		if (!insideViewPort(PC_POS.x + offset.x, PC_POS.y + offset.y)) {
			offset = new Position(0, 0);
		}

		/*
		 * if (!insideViewPort(offset)) offset = new Position (0,0);
		 */

		si.refresh();
		si.saveBuffer();

		while (true) {
			si.restore();
			int cellHeight = 0;
			browser = Position.add(player.getPosition(), offset);
			String looked = "";

			if (FOVMask[PC_POS.x + offset.x][PC_POS.y + offset.y]) {
				Cell choosen = level.getMapCell(browser);
				Feature feat = level.getFeatureAt(browser);
				Vector<MenuItem> items = level.getItemsAt(browser);
				if (choosen != null)
					cellHeight = choosen.getHeight();
				Item item = null;
				if (items != null) {
					item = (Item) items.elementAt(0);
				}
				Actor actor = level.getActorAt(browser);
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
			}
			messageBox.setText(prompt + " " + looked);
			// si.print(PC_POS.x + offset.x, PC_POS.y + offset.y, '_',
			// ConsoleSystemInterface.RED);
			drawStepsTo(PC_POS.x + offset.x, (PC_POS.y + offset.y), TILE_LINE_STEPS, cellHeight);

			drawImageVP((PC_POS.x + offset.x) * 32 - 2, (PC_POS.y + offset.y) * 32 - 2 - 4 * cellHeight, TILE_LINE_AIM);

			si.refresh();
			CharKey x = new CharKey(CharKey.NONE);
			while (x.code != CharKey.SPACE && x.code != CharKey.ESC && x.code != fireKeyCode && !x.isArrow())
				x = si.inkey();
			if (x.code == CharKey.ESC) {
				si.restore();
				si.refresh();
				throw new ActionCancelException();
			}
			if (x.code == CharKey.SPACE || x.code == fireKeyCode) {
				si.restore();
				if (level.getMonsterAt(browser) != null)
					lockedMonster = level.getMonsterAt(browser);
				return browser;
			}
			offset.add(Action.directionToVariation(UISelector.toIntDirection(x)));

			if (offset.x >= xrange)
				offset.x = xrange;
			if (offset.x <= -xrange)
				offset.x = -xrange;
			if (offset.y >= yrange)
				offset.y = yrange;
			if (offset.y <= -yrange)
				offset.y = -yrange;
		}

	}

	private int pickDirection(String prompt) throws ActionCancelException {
		Debug.enterMethod(this, "pickDirection");
		// refresh();
		leaveScreen();
		messageBox.setText(prompt);
		// si.refresh();
		// refresh();

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

	private Item pickEquipedItem(String prompt) throws ActionCancelException {
		enterScreen();

		Vector<MenuItem> equipped = new Vector<MenuItem>();
		if (player.getArmor() != null)
			equipped.add(player.getArmor());
		if (player.getWeapon() != null)
			equipped.add(player.getWeapon());
		if (player.getShield() != null)
			equipped.add(player.getShield());
		if (player.getSecondaryWeapon() != null)
			equipped.add(player.getSecondaryWeapon());

		if (equipped.isEmpty()) {
			level.addMessage("Nothing equipped");
			ActionCancelException ret = new ActionCancelException();
			Debug.exitExceptionally(ret);
			throw ret;
		}

		BorderedMenuBox menuBox = GetMenuBox();
		menuBox.setGap(35);

		// menuBox.setBounds(26,6,30,11);
		menuBox.setBounds(6, 4, 70, 12);
		menuBox.setMenuItems(equipped);
		menuBox.setTitle(prompt);
		si.saveBuffer();
		// menuBox.draw();
		Item equiped = (Item) menuBox.getSelection();
		if (equiped == null) {
			ActionCancelException ret = new ActionCancelException();
			Debug.exitExceptionally(ret);
			si.restore();
			si.refresh();
			throw ret;
		}
		si.restore();
		si.refresh();
		leaveScreen();
		return equiped;
	}

	private Item pickItem(String prompt) throws ActionCancelException {
		enterScreen();
		Vector<MenuItem> inventory = player.getInventory();
		BorderedMenuBox menuBox = GetMenuBox();
		menuBox.setGap(35);
		menuBox.setPosition(6, 4);
		menuBox.setWidth(70);
		menuBox.setItemsPerPage(12);
		menuBox.setMenuItems(inventory);
		menuBox.setTitle(prompt);
		si.saveBuffer();
		// menuBox.draw();
		Equipment equipment = (Equipment) menuBox.getSelection();
		si.restore();
		if (equipment == null) {
			ActionCancelException ret = new ActionCancelException();
			Debug.exitExceptionally(ret);
			si.restore();
			si.refresh();
			leaveScreen();
			throw ret;
		}
		si.restore();
		si.refresh();
		leaveScreen();
		return equipment.getItem();
	}

	private Vector<MenuItem> pickMultiItems(String prompt) throws ActionCancelException {
		// Equipment.eqMode = true;
		Vector<MenuItem> inventory = player.getInventory();
		BorderedMenuBox menuBox = GetMenuBox();
		menuBox.setBounds(25, 3, 40, 18);
		// menuBox.setPromptSize(2);
		menuBox.setMenuItems(inventory);
		menuBox.setTitle(prompt);
		// menuBox.setForeColor(ConsoleSystemInterface.RED);
		// menuBox.setBorder(true);
		Vector<MenuItem> ret = new Vector<MenuItem>();
		BorderedMenuBox selectedBox = GetMenuBox();
		selectedBox.setBounds(5, 3, 20, 18);
		// selectedBox.setPromptSize(2);
		selectedBox.setTitle("Selected Items");
		selectedBox.setMenuItems(ret);
		// selectedBox.setForeColor(ConsoleSystemInterface.RED);

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
		// Equipment.eqMode = false;
		return ret;
	}

	public void processQuit() {
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		messageBox.setText(quitMessages[Util.rand(0, quitMessages.length - 1)] + " (y/n)");
		si.refresh();
		if (prompt()) {
			messageBox.setText("Go away, and let the world flood in darkness... [Press Space to continue]");
			si.refresh();
			si.waitKey(CharKey.SPACE);
			enterScreen();
			// si.refresh();
			player.getGameSessionInfo().setDeathCause(GameSessionInfo.QUIT);
			informPlayerCommand(CommandListener.QUIT);
		}
		messageBox.clear();
		si.refresh();
	}

	public void processSave() {
		if (!player.getGame().canSave()) {
			level.addMessage("You cannot save your game here!");
			return;
		}
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		messageBox.setText("Save your game? (y/n)");
		si.refresh();
		if (prompt()) {
			messageBox.setText("Saving... I will await your return.. [Press Space to continue]");
			si.refresh();
			si.waitKey(CharKey.SPACE);
			enterScreen();
			informPlayerCommand(CommandListener.SAVE);
		}
		messageBox.clear();
		si.refresh();
	}

	public boolean prompt() {

		CharKey x = new CharKey(CharKey.NONE);
		while (x.code != CharKey.Y && x.code != CharKey.y && x.code != CharKey.N && x.code != CharKey.n)
			x = si.inkey();
		return (x.code == CharKey.Y || x.code == CharKey.y);
	}

	private int dimMsg = 0;

	@Override
	public void safeRefresh() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refresh();
			}
		});
	}

	public void refresh() {
		si.cls();
		// messageBox.setVisible(true);
		/*
		 * if (useMouse) drawCursor();
		 */
		drawLevel();
		drawPlayerStatus();
		si.refresh();
		leaveScreen();
		if (dimMsg == 3) {
			messageBox.setForeground(COLOR_OLD_MESSAGE);
			dimMsg = 0;
		}
		dimMsg++;
		if (!player.getFlag("KEEPMESSAGES"))
			eraseOnArrival = true;
		si.saveBuffer(); // sz040507

	}

	public void setTargets(Action a) throws ActionCancelException {
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
			// a.setMultiItems(pickSpirits());
			a.setMultiItems(pickMultiItems(a.getPromptMultiItems()));
		}
		if (a.needsUnderlyingItem()) {
			a.setItem(pickUnderlyingItem(a.getPrompUnderlyingItem()));
		}
	}

	private Item pickUnderlyingItem(String prompt) throws ActionCancelException {
		enterScreen();
		Vector<MenuItem> items = level.getItemsAt(player.getPosition());
		if (items == null)
			return null;
		if (items.size() == 1)
			return (Item) items.elementAt(0);
		BorderedMenuBox menuBox = GetMenuBox();
		menuBox.setGap(35);
		menuBox.setBounds(6, 4, 70, 12);
		menuBox.setMenuItems(items);
		menuBox.setTitle(prompt);
		si.saveBuffer();
		// menuBox.draw();
		Item item = (Item) menuBox.getSelection();

		if (item == null) {
			ActionCancelException ret = new ActionCancelException();
			Debug.exitExceptionally(ret);
			si.restore();
			si.refresh();
			leaveScreen();
			throw ret;
		}
		si.restore();
		si.refresh();
		leaveScreen();
		return item;
	}

	private Vector<MenuItem> vecItemUsageChoices = new Vector<MenuItem>();
	{
		vecItemUsageChoices.add(new SimpleGFXMenuItem("[u]se", 1));
		vecItemUsageChoices.add(new SimpleGFXMenuItem("[e]quip", 2));
		vecItemUsageChoices.add(new SimpleGFXMenuItem("[t]hrow", 4));
		vecItemUsageChoices.add(new SimpleGFXMenuItem("[d]rop", 3));
		vecItemUsageChoices.add(new SimpleGFXMenuItem("[ ] Cancel", 5));

	}

	private int[] additionalKeys = new int[] { CharKey.N1, CharKey.N2, CharKey.N3, CharKey.N4, };

	private int[] itemUsageKeys = new int[] { CharKey.u, CharKey.e, CharKey.d, CharKey.t, };

	public Action showInventory() throws ActionCancelException {
		enterScreen();
		Equipment.menuDetail = true;
		Vector<MenuItem> inventory = player.getInventory();
		int xpos = 1, ypos = 0;
		BorderedMenuBox menuBox = GetMenuBox();
		menuBox.setGap(35);
		menuBox.setItemsPerPage(10);
		menuBox.setWidth(75);
		menuBox.setPosition(3, 8);
		menuBox.setTitle("Items");
		menuBox.setMenuItems(inventory);

		MenuBox itemUsageChoices = new MenuBox(si, this.configuration, null);
		itemUsageChoices.setItemsPerPage(6);
		itemUsageChoices.setWidth(20);
		itemUsageChoices.setPosition(52, 15);
		itemUsageChoices.setMenuItems(vecItemUsageChoices);
		si.saveBuffer(1);
		// si.saveBuffer();

		JTextArea itemDescription = GFXDisplay.createTempArea(509, 201, 202, 122);
		itemDescription.setVisible(true);
		si.add(itemDescription);
		// si.cls();

		int xx = 17;
		int yy = 22;
		int ww = 750;
		int hh = 141;
		si.getGraphics2D().setColor(COLOR_WINDOW_BACKGROUND);
		si.getGraphics2D().fillRect(xx + 6, yy + 6, ww - 14, hh - 14);
		si.getGraphics2D().setColor(COLOR_BORDER_OUT);
		si.getGraphics2D().drawRect(xx + 6, yy + 6, ww - 14, hh - 14);
		si.getGraphics2D().setColor(COLOR_BORDER_IN);
		si.getGraphics2D().drawRect(xx + 8, yy + 8, ww - 18, hh - 18);

		si.print(xpos + 2, ypos + 2, "Inventory", GFXDisplay.COLOR_BOLD);
		si.print(xpos + 2, ypos + 3, "1. Weapon:", Color.WHITE);
		si.print(xpos + 2, ypos + 4, "2. Readied", Color.WHITE);
		si.print(xpos + 2, ypos + 5, "3. Armor:", Color.WHITE);
		si.print(xpos + 2, ypos + 6, "4. Shield:", Color.WHITE);

		si.print(xpos + 10, ypos + 3, player.getEquipedWeaponDescription(), Color.WHITE);
		si.print(xpos + 10, ypos + 4, player.getSecondaryWeaponDescription(), Color.WHITE);
		si.print(xpos + 10, ypos + 5, player.getArmorDescription(), Color.WHITE);
		si.print(xpos + 10, ypos + 6, player.getAccDescription(), Color.WHITE);
		// menuBox.draw();
		// si.print(xpos,24, "[Space] to continue, Up and Down to browse", Color.WHITE);

		si.refresh();
		si.saveBuffer();
		// ---
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
				case CharKey.N1:
					// Unequip Weapon
					if (player.getWeapon() != null) {
						selectedAction = new Unequip();
						selectedAction.setPerformer(player);
						selectedAction.setEquipedItem(player.getWeapon());
						exitInventory(itemDescription);
						return selectedAction;
					} else {
						continue;
					}
				case CharKey.N2:
					// Unequip Secondary Weapon
					if (player.getSecondaryWeapon() != null) {
						selectedAction = new Unequip();
						selectedAction.setPerformer(player);
						selectedAction.setEquipedItem(player.getSecondaryWeapon());
						exitInventory(itemDescription);
						return selectedAction;
					} else {
						continue;
					}
				case CharKey.N3:
					// Unequip Armor
					if (player.getArmor() != null) {
						selectedAction = new Unequip();
						selectedAction.setPerformer(player);
						selectedAction.setEquipedItem(player.getArmor());
						exitInventory(itemDescription);
						return selectedAction;
					} else {
						continue;
					}
				case CharKey.N4:
					// Unequip Shield
					if (player.getShield() != null) {
						selectedAction = new Unequip();
						selectedAction.setPerformer(player);
						selectedAction.setEquipedItem(player.getShield());
						exitInventory(itemDescription);
						return selectedAction;
					} else {
						continue;
					}
				}
			}
			if (selected == null) {
				break;
			}
			si.print(52, 8, selected.getDescription(), GFXDisplay.COLOR_BOLD);
			itemDescription.setText(selected.getDefinition().getMenuDescription());
			si.refresh();

			itemUsageChoices.draw();

			SimpleGFXMenuItem choice = null;
			try {
				choice = (SimpleGFXMenuItem) itemUsageChoices.getUnpagedOrdinalSelectionAKS(itemUsageKeys);
			} catch (AdditionalKeysSignal aks) {
				switch (aks.getKeyCode()) {
				case CharKey.u:
					choice = (SimpleGFXMenuItem) vecItemUsageChoices.elementAt(0);
					break;
				case CharKey.e:
					choice = (SimpleGFXMenuItem) vecItemUsageChoices.elementAt(1);
					break;
				case CharKey.t:
					choice = (SimpleGFXMenuItem) vecItemUsageChoices.elementAt(2);
					break;
				case CharKey.d:
					choice = (SimpleGFXMenuItem) vecItemUsageChoices.elementAt(3);
					break;
				}
			}
			if (choice != null) {
				switch (choice.getValue()) {
				case 1: // Use
					Use use = new Use();
					use.setPerformer(player);
					use.setItem(selected);
					exitInventory(itemDescription);
					return use;
				case 2: // Equip
					Equip equip = new Equip();
					equip.setPerformer(player);
					equip.setItem(selected);
					exitInventory(itemDescription);
					return equip;
				case 3: // Drop
					Drop drop = new Drop();
					drop.setPerformer(player);
					drop.setItem(selected);
					exitInventory(itemDescription);
					return drop;
				case 4: // Throw
					Throw throwx = new Throw();
					throwx.setPerformer(player);
					throwx.setItem(selected);
					exitInventory(itemDescription);
					throwx.setPosition(pickPosition("Throw where?", CharKey.SPACE));
					return throwx;
				case 5: // Cancel

					break;
				}
			}
			itemDescription.setText("");
			si.restore();
			si.refresh();

		} while (selected != null);
//		si.waitKey(CharKey.SPACE);
		si.restore();
		si.refresh();
		Equipment.eqMode = false;
		Equipment.menuDetail = false;
		exitInventory(itemDescription);
		leaveScreen();
		return null;

	}

	private void exitInventory(JTextArea itemDescription) {
		si.remove(itemDescription);
		si.restore(1);
		si.refresh();
	}

	/**
	 * Shows a message inmediately; useful for system messages.
	 * 
	 * @param x the message to be shown
	 */
	public void showMessage(String x) {
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		messageBox.setText(x);
		messageBox.setVisible(true); // Force it!
		// si.refresh();
	}

	public void showImportantMessage(String x) {
		showMessage(x);
		si.waitKey(CharKey.SPACE);
	}

	@Override
	public void showVersionDialog(String description, boolean stop) {
		if (stop) {
			si.showAlert(description);
		} else {
			System.out.println(description);
		}
	}

	public void showSystemMessage(String x) {
		messageBox.setForeground(COLOR_LAST_MESSAGE);
		messageBox.setText(x);
		// si.refresh();
		si.waitKey(CharKey.SPACE);
	}

	public void showPlayerStats() {

		si.saveBuffer();
		enterScreen();
		si.drawImage(IMG_STATUSSCR_BGROUND);
		si.print(1, 1, player.getName() + " the level " + player.getPlayerLevel() + " " + player.getClassString() + " "
				+ player.getStatusString(), GFXDisplay.COLOR_BOLD);
		si.print(1, 2, "Sex: " + (player.getSex() == Player.MALE ? "M" : "F"), Color.WHITE);
		si.print(1, 3,
				"Hits: " + player.getHits() + "/" + player.getHitsMax() + " Hearts: " + player.getHearts() + "/"
						+ player.getHeartsMax() + " Gold: " + player.getGold() + " Keys: " + player.getKeys(),
				Color.WHITE);
		si.print(1, 4, "Carrying: " + player.getItemCount() + "/" + player.getCarryMax(), Color.WHITE);
		si.print(1, 6, "Attack: +" + player.getAttack(), Color.WHITE);
		si.print(1, 7, "Soul Power: +" + player.getSoulPower(), Color.WHITE);
		si.print(1, 8, "Evade: " + player.getEvadeChance() + "%", Color.WHITE);
		si.print(1, 9, "Combat: " + (50 - player.getAttackCost()), Color.WHITE);
		si.print(1, 10, "Invokation: " + (50 - player.getCastCost()), Color.WHITE);
		si.print(1, 11, "Movement: " + (50 - player.getWalkCost()), Color.WHITE);

		si.print(1, 12, "Experience: " + player.getXp() + "/" + player.getNextXP(), Color.WHITE);

		/*
		 * si.print(1,2, "Skills", ConsoleSystemInterface.RED); Vector skills =
		 * player.getAvailableSkills(); int cont = 0; for (int i = 0; i < skills.size();
		 * i++){ if (i % 10 == 0) cont++; si.print((cont-1) * 25 + 1, 3 + i - ((cont-1)
		 * * 10), ((Skill)skills.elementAt(i)).getMenuDescription()); }
		 */

		si.print(1, 14, "Weapon Profficiences", GFXDisplay.COLOR_BOLD);
		si.print(1, 15, "Hand to hand", GFXDisplay.COLOR_BOLD);
		si.print(1, 16, "Daggers", GFXDisplay.COLOR_BOLD);
		si.print(1, 17, "Swords", GFXDisplay.COLOR_BOLD);
		si.print(1, 18, "Spears", GFXDisplay.COLOR_BOLD);
		si.print(22, 15, "Whips", GFXDisplay.COLOR_BOLD);
		si.print(22, 16, "Maces", GFXDisplay.COLOR_BOLD);
		si.print(22, 17, "Pole Combat", GFXDisplay.COLOR_BOLD);
		si.print(22, 18, "Combat Rings", GFXDisplay.COLOR_BOLD);
		si.print(49, 15, "Projectiles", GFXDisplay.COLOR_BOLD);
		si.print(49, 16, "Bows/XBows", GFXDisplay.COLOR_BOLD);
		si.print(49, 17, "Machinery", GFXDisplay.COLOR_BOLD);
		si.print(49, 18, "Shields", GFXDisplay.COLOR_BOLD);

		String[] wskills = ItemDefinition.CATS;
		int cont = 0;
		for (int i = 0; i < wskills.length; i++) {
			if (i % 4 == 0)
				cont++;
			si.print((cont - 1) * 23 + 13, 15 + i - ((cont - 1) * 4), verboseSkills[player.weaponSkill(wskills[i])],
					Color.WHITE);
		}

		si.print(1, 19, "Attack Damage  ", GFXDisplay.COLOR_BOLD);
		si.print(1, 20, "Actual Defense ", GFXDisplay.COLOR_BOLD);
		si.print(1, 21, "Shield Rates   ", GFXDisplay.COLOR_BOLD);

		si.print(16, 19, "" + player.getWeaponAttack(), Color.WHITE);
		si.print(16, 20,
				player.getArmorDefense() + (player.getDefenseBonus() != 0 ? "+" + player.getDefenseBonus() : ""),
				Color.WHITE);
		si.print(16, 21,
				"Block " + player.getShieldBlockChance() + "% Coverage " + player.getShieldCoverageChance() + "%",
				Color.WHITE);

		si.print(1, 23, "[ Press Space to continue ]", Color.WHITE);
		si.refresh();
		si.waitKey(CharKey.SPACE);
		si.restore();
		si.refresh();
		leaveScreen();
	}

	private BorderedMenuBox GetMenuBox() {
		return GetMenuBox(false);
	}

	private BorderedMenuBox GetMenuBox(boolean nullBox) {
		BufferedImage box = nullBox ? null : TILE_WEAPON_BACK;
		return new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN,
				COLOR_BORDER_OUT, BORDERS_SIZE, box);
	}

	public Action showSkills() throws ActionCancelException {
		Debug.enterMethod(this, "showSkills");
		enterScreen();
		si.saveBuffer();
		Vector<MenuItem> skills = player.getAvailableSkills();
		BorderedMenuBox menuBox = GetMenuBox(true);
		menuBox.setItemsPerPage(14);
		menuBox.setWidth(48);
		menuBox.setPosition(6, 4);
		menuBox.setMenuItems(skills);
		menuBox.setTitle("Skills");
		// menuBox.draw();
		si.refresh();
		Skill selectedSkill = (Skill) menuBox.getSelection();
		if (selectedSkill == null) {
			si.restore();
			si.refresh();
			Debug.exitMethod("null");
			leaveScreen();
			return null;
		}
		si.restore();
		si.refresh();
		if (selectedSkill.isSymbolic()) {
			Debug.exitMethod("null");
			leaveScreen();
			return null;
		}

		Action selectedAction = selectedSkill.getAction();
		selectedAction.setPerformer(player);
		if (selectedAction.canPerform(player))
			setTargets(selectedAction);
		else
			level.addMessage(selectedAction.getInvalidationMessage());

		Debug.exitMethod(selectedAction);
		leaveScreen();
		return selectedAction;
	}

	public void levelUp() {

		showMessage("You gained a level!, [Press Space to continue]");

		si.waitKey(CharKey.SPACE);
		enterScreen();
		if (player.deservesAdvancement(player.getPlayerLevel())) {
			Vector<Advancement> advancements = player.getAvailableAdvancements();
			if (!advancements.isEmpty()) {
				Advancement playerChoice = Display.thus.showLevelUp(advancements);
				playerChoice.advance(player);
				player.getGameSessionInfo().addHistoryItem("went for " + playerChoice.getName());
			}
		}
		if (player.deservesStatAdvancement(player.getPlayerLevel())) {
			Vector<Advancement> advancements = player.getAvailableStatAdvancements();
			if (!advancements.isEmpty()) {
				Advancement playerChoice = Display.thus.showLevelUp(advancements);
				playerChoice.advance(player);
				player.getGameSessionInfo().addHistoryItem("went for " + playerChoice.getName());
			}
		}
		leaveScreen();
		((GFXDisplay) Display.thus).showTextBox("LEVEL UP!\n\n [" + player.getLastIncrementString() + "]", 40, 60, 300,
				300);
		// showMessage("You gained a level!, ["+player.getLastIncrementString()+"]");
		player.resetLastIncrements();

		/*
		 * int soulOptions = 5; Vector soulIds = getLevelUpSouls(); int playerChoice =
		 * Display.thus.showLevelUp(soulIds); Item soul =
		 * ItemFactory.getItemFactory().createItem((String)soulIds.elementAt(
		 * playerChoice)); if (player.canCarry()){ player.addItem(soul); } else {
		 * player.getLevel().addItem(player.getPosition(), soul); }
		 * showMessage("You acquired a "+soul.getDescription());
		 */
	}

	@Override
	public void setPlayer(Player pPlayer) {
		super.setPlayer(pPlayer);
		flipFacing = false;
	}

	@Override
	public void commandSelected(int commandCode) {
		switch (commandCode) {
		case CommandListener.PROMPTQUIT:
			processQuit();
			break;
		case CommandListener.PROMPTSAVE:
			processSave();
			break;
		case CommandListener.HELP:
			si.saveBuffer();
			messageBox.setVisible(false);
			helpBox.setVisible(true);
			si.restore();
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
				addMessage(new Message("- Cancelled", player.getPosition()));
				eraseOnArrival = true;
				si.refresh();
				actionSelectedByCommand = null;
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
				addMessage(new Message("- Cancelled", player.getPosition()));
				eraseOnArrival = true;
				si.refresh();
				actionSelectedByCommand = null;
			}
			break;
		case CommandListener.SHOWMESSAGEHISTORY:
			showMessageHistory();
			break;
		case CommandListener.SHOWMAP:
			Display.thus.showMap(level.getMapLocationKey(), level.getDescription());
			break;
		case CommandListener.SWITCHMUSIC:
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
			break;
		case CommandListener.EXAMINELEVELMAP:
			// examineLevelMap(); Disabled, we have minimap on HUD
			break;
		case CommandListener.CHARDUMP:
			GameFiles.saveChardump(player);
			showMessage("Character File Dumped.");
			break;
		}
	}

//	Runnable interface
	public void run() {
	}

//	IO Utility    
	public void waitKey() {
		CharKey x = new CharKey(CharKey.NONE);
		while (x.code == CharKey.NONE)
			x = si.inkey();
	}

	private void drawStepsTo(int x, int y, Image tile, int cellHeight) {
		Position target = new Position(x, y);
		Line line = new Line(PC_POS, target);
		Position tmp = line.next();
		while (!tmp.equals(target)) {
			tmp = line.next();
			drawImageVP(tmp.x * 32 + 13, tmp.y * 32 + 14 - 4 * cellHeight, tile);
		}

	}

	class MerchantBox extends AddornedBorderPanel {
		private JList<MenuItem> lstMerchandise;
		private GFXButton btnBuy;
		private GFXButton btnExit;
		private GFXButton btnYes;
		private GFXButton btnNo;
		private JTextArea prompt;
		private JLabel lblGold;

		// private ShopMenuItem choice;
		private Item choice;
		private Thread activeThread;

		@Override
		public void setVisible(boolean val) {
			super.setVisible(val);
			if (val) {
				lstMerchandise.requestFocus();
				if (lstMerchandise.getModel().getSize() > 0)
					lstMerchandise.setSelectedIndex(0);
			}
		}

		public MerchantBox(Image UPRIGHT, Image UPLEFT, Image DOWNRIGHT, Image DOWNLEFT, Color OUT_COLOR,
				Color IN_COLOR, int borderWidth, int borderHeight) {
			super(UPRIGHT, UPLEFT, DOWNRIGHT, DOWNLEFT, OUT_COLOR, IN_COLOR, borderWidth, borderHeight);

			lstMerchandise = new JList<MenuItem>(new DefaultListModel<MenuItem>());
			btnBuy = new GFXButton(IMG_BUY_BTN);
			btnExit = new GFXButton(IMG_EXIT_BTN);
			btnYes = new GFXButton(IMG_YES_BTN);
			btnNo = new GFXButton(IMG_NO_BTN);

			lblGold = new JLabel();
			lblGold.setOpaque(false);
			lblGold.setForeground(Color.YELLOW);
			btnYes.setVisible(false);
			btnNo.setVisible(false);

			lstMerchandise.setCellRenderer(new MerchandiseCellRenderer());
			lstMerchandise.setOpaque(false);

			addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE
							|| e.getKeyCode() == KeyEvent.VK_Y) {
						if (btnYes.isVisible())
							doYes();
						else
							doBuy();
					}
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_N) {
						if (btnYes.isVisible())
							doNo();
						else
							doExit();
					}
				}

				public void keyReleased(KeyEvent e) {
				}

				public void keyTyped(KeyEvent e) {
				}

			});

			lstMerchandise.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
						if (btnYes.isVisible())
							doYes();
						else
							doBuy();
					} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						if (btnYes.isVisible())
							doNo();
						else
							doExit();
					} else if (e.getKeyCode() == KeyEvent.VK_NUMPAD8) {
						if (lstMerchandise.getSelectedIndex() > 0)
							lstMerchandise.setSelectedIndex(lstMerchandise.getSelectedIndex() - 1);
					} else if (e.getKeyCode() == KeyEvent.VK_NUMPAD2) {
						if (lstMerchandise.getSelectedIndex() < lstMerchandise.getModel().getSize() - 1)
							lstMerchandise.setSelectedIndex(lstMerchandise.getSelectedIndex() + 1);
					}
				}

				public void keyReleased(KeyEvent e) {
				}

				public void keyTyped(KeyEvent e) {
				}

			});

			setOpaque(false);
			setBorder(new EmptyBorder(STANDARD_WIDTH, STANDARD_WIDTH, STANDARD_WIDTH, STANDARD_WIDTH));

			setLayout(new BorderLayout());
			((BorderLayout) getLayout()).setHgap(16);
			((BorderLayout) getLayout()).setVgap(16);
			/*
			 * JLabel title = new JLabel("Skills"); title.setFont(UI_FONT);
			 * title.setForeground(GFXDisplay.GOLD);
			 */

			prompt = new JTextArea();
			prompt.setFont(FNT_MESSAGEBOX);
			prompt.setOpaque(false);
			prompt.setForeground(Color.WHITE);
			prompt.setLineWrap(true);
			prompt.setWrapStyleWord(true);
			prompt.setEditable(false);
			prompt.setFocusable(false);
			// prompt.setVisible(false);

			JPanel pnlButtons = new JPanel();
			pnlButtons.add(lblGold);
			pnlButtons.add(btnBuy);
			pnlButtons.add(btnExit);
			pnlButtons.setOpaque(false);

			JPanel pnlSuperior = new JPanel(new BorderLayout());
			pnlSuperior.add(prompt, BorderLayout.CENTER);
			JPanel miniPanelBotones = new JPanel();
			miniPanelBotones.setOpaque(false);
			miniPanelBotones.add(btnYes);
			miniPanelBotones.add(btnNo);
			pnlSuperior.add(miniPanelBotones, BorderLayout.SOUTH);
			pnlSuperior.setOpaque(false);

			add(pnlSuperior, BorderLayout.NORTH);
			add(lstMerchandise, BorderLayout.CENTER);
			add(pnlButtons, BorderLayout.SOUTH);

			setBackground(Color.BLACK);

			btnYes.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					doYes();
				}
			});

			btnNo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					doNo();
				}
			});

			btnBuy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					doBuy();
				}
			});

			btnExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					doExit();
				}
			});

		}

		private void doBuy() {
			if (activeThread != null) {
				// choice = (ShopMenuItem) lstMerchandise.getSelectedValue();
				choice = (Item) lstMerchandise.getSelectedValue();
				setPrompt("The " + choice.getDescription() + ", " + choice.getShopDescription() + "; it costs "
						+ choice.getGoldPrice() + ", Do you want to buy it? (Y/n)");
				btnBuy.setEnabled(false);
				btnExit.setEnabled(false);
				lstMerchandise.setEnabled(false);
				btnYes.setVisible(true);
				btnNo.setVisible(true);
				requestFocus();
			}
		}

		private void doExit() {
			if (activeThread != null) {
				choice = null;
				activeThread.interrupt();
			}

		}

		private void doYes() {
			activeThread.interrupt();
			btnBuy.setEnabled(true);
			btnExit.setEnabled(true);
			lstMerchandise.setEnabled(true);
			btnYes.setVisible(false);
			btnNo.setVisible(false);
			lstMerchandise.requestFocus();
		}

		private void doNo() {
			setPrompt("Too bad... May I interest you in something else?");
			btnBuy.setEnabled(true);
			btnExit.setEnabled(true);
			lstMerchandise.setEnabled(true);
			btnYes.setVisible(false);
			btnNo.setVisible(false);
			lstMerchandise.requestFocus();
		}

		public void setPrompt(String prompt) {
			this.prompt.setText(prompt);
		}

		public void setMerchandise(Vector<MenuItem> skills) {
			((DefaultListModel<MenuItem>) lstMerchandise.getModel()).removeAllElements();
			for (int i = 0; i < skills.size(); i++) {
				((DefaultListModel<MenuItem>) lstMerchandise.getModel()).addElement(skills.elementAt(i));
			}
		}

		public void informChoice(Thread who) {
			choice = null;
			activeThread = who;
		}

		public Item getSelection() {
			return choice;
		}

		public void setGold(int gold) {
			lblGold.setText("Player gold: " + gold);
		}

		class MerchandiseCellRenderer extends DefaultListCellRenderer {
			private JLabel ren;

			public MerchandiseCellRenderer() {
				ren = new JLabel();
				ren.setFont(FNT_MESSAGEBOX);
				ren.setOpaque(false);
				ren.setForeground(Color.WHITE);
				ren.setBackground(GFXDisplay.COLOR_BOLD);

			}

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Item smi = (Item) value;
				ren.setIcon(new ImageIcon(((GFXAppearance) smi.getAppearance()).getIconImage()));
				// ren.setText(smi.getMenuDescription());
				ren.setText(smi.getAttributesDescription() + " [" + smi.getDefinition().getMenuDescription() + "] ($"
						+ smi.getGoldPrice() + ")");
				ren.setOpaque(isSelected);
				return ren;
			}

		}
	}

	class MultiItemsBox extends AddornedBorderPanel {
		private JList<MenuItem> lstInventory;
		private GFXButton btnExit;
		private GFXButton btnOk;
		private JLabel lblPrompt;

		private Thread activeThread;

		@Override
		public void setVisible(boolean val) {
			super.setVisible(val);
			if (val) {
				lstInventory.requestFocus();
				if (lstInventory.getModel().getSize() > 0)
					lstInventory.setSelectedIndex(0);
			}
		}

		public MultiItemsBox(Image UPRIGHT, Image UPLEFT, Image DOWNRIGHT, Image DOWNLEFT, Color OUT_COLOR,
				Color IN_COLOR, int borderWidth, int borderHeight) {
			super(UPRIGHT, UPLEFT, DOWNRIGHT, DOWNLEFT, OUT_COLOR, IN_COLOR, borderWidth, borderHeight);

			lstInventory = new JList<MenuItem>(new DefaultListModel<MenuItem>());
			lstInventory.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			btnExit = new GFXButton(IMG_EXIT_BTN);
			btnOk = new GFXButton(IMG_OK_BTN);

			lstInventory.setOpaque(false);
			lstInventory.setCellRenderer(new ItemsCellRenderer());

			setOpaque(false);
			setBorder(new EmptyBorder(STANDARD_WIDTH, STANDARD_WIDTH, STANDARD_WIDTH, STANDARD_WIDTH));

			setLayout(new BorderLayout());

			lblPrompt = new JLabel("Inventory");
			lblPrompt.setFont(FNT_MESSAGEBOX);
			lblPrompt.setForeground(GFXDisplay.COLOR_BOLD);

			JPanel pnlButtons = new JPanel();
			pnlButtons.add(btnExit);
			pnlButtons.add(btnOk);
			pnlButtons.setOpaque(false);

			add(lblPrompt, BorderLayout.NORTH);
			add(lstInventory, BorderLayout.CENTER);
			add(pnlButtons, BorderLayout.SOUTH);

			setBackground(Color.BLACK);

			btnExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					doExit();
				}
			});
			btnOk.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					doOk();
				}
			});
			lstInventory.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
						doOk();
					} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						doExit();
					} else if (e.getKeyCode() == KeyEvent.VK_NUMPAD8) {
						if (lstInventory.getSelectedIndex() > 0)
							lstInventory.setSelectedIndex(lstInventory.getSelectedIndex() - 1);
					} else if (e.getKeyCode() == KeyEvent.VK_NUMPAD2) {
						if (lstInventory.getSelectedIndex() < lstInventory.getModel().getSize() - 1)
							lstInventory.setSelectedIndex(lstInventory.getSelectedIndex() + 1);
					}
				}

				public void keyReleased(KeyEvent e) {
				}

				public void keyTyped(KeyEvent e) {
				}

			});

		}

		private void doOk() {
			if (activeThread != null) {
				choice = new Vector<Item>();
				int[] indices = lstInventory.getSelectedIndices();
                for (int index : indices) {
                    choice.add(((Equipment) inventory.elementAt(index)).getItem());
                }
				activeThread.interrupt();
			}
		}

		private void doExit() {
			if (activeThread != null) {
				choice = null;
				activeThread.interrupt();
			}
		}

		private Vector<Item> choice;
		private Vector<MenuItem> inventory;

		public Vector<Item> getChoice() {
			return choice;
		}

		public void setPrompt(String prompt) {
			lblPrompt.setText(prompt);
		}

		public void setItems(Vector<MenuItem> items) {
			inventory = (Vector<MenuItem>) items.clone();
			((DefaultListModel<MenuItem>) lstInventory.getModel()).removeAllElements();
			for (int i = 0; i < items.size(); i++) {
				((DefaultListModel<MenuItem>) lstInventory.getModel()).addElement(items.elementAt(i));
			}
		}

		public void informChoice(Thread who) {
			activeThread = who;
		}

		class ItemsCellRenderer extends DefaultListCellRenderer {
			private JLabel ren;

			public ItemsCellRenderer() {
				ren = new JLabel();
				ren.setFont(FNT_MESSAGEBOX);
				ren.setOpaque(false);
				ren.setForeground(Color.WHITE);
				ren.setBackground(GFXDisplay.COLOR_BOLD);

			}

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				if (value instanceof Equipment) {
					Equipment smi = (Equipment) value;
					ren.setText(smi.getMenuDescription());
					ren.setIcon(new ImageIcon(((GFXAppearance) smi.getItem().getAppearance()).getIconImage()));
				} else {
					Item smi = (Item) value;
					ren.setText(smi.getMenuDescription());
					ren.setIcon(new ImageIcon(((GFXAppearance) smi.getAppearance()).getIconImage()));
				}

				ren.setOpaque(isSelected);
				return ren;
			}
		}
	}

	public Vector<String> getMessageBuffer() {
		// return new Vector(messageHistory.subList(0,21));
		if (messageHistory.size() > 20)
			return new Vector<String>(messageHistory.subList(messageHistory.size() - 21, messageHistory.size()));
		else
			return messageHistory;
	}

	class HelpBox extends AddornedBorderPanel {
		private GFXButton btnOk;

		public HelpBox(Image UPRIGHT, Image UPLEFT, Image DOWNRIGHT, Image DOWNLEFT, Color OUT_COLOR, Color IN_COLOR,
				int borderWidth, int borderHeight) {
			super(UPRIGHT, UPLEFT, DOWNRIGHT, DOWNLEFT, OUT_COLOR, IN_COLOR, borderWidth, borderHeight);
			setOpaque(false);
			setBorder(new EmptyBorder(BORDERS_SIZE, BORDERS_SIZE, BORDERS_SIZE, BORDERS_SIZE));

			btnOk = new GFXButton(IMG_OK_BTN);
			setLayout(new BorderLayout());
			add(btnOk, BorderLayout.SOUTH);
			btnOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					doOk();
				}
			});

			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						setVisible(false);
						si.recoverFocus();
					}
				}
			});
		}

		private void print(Graphics g, int x, int y, String text, Color color) {
			g.setColor(color);
			g.drawString(text, x * 10, y * 24);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(GFXDisplay.FNT_TITLE);
			print(g, 3, 2, "Help", GFXDisplay.COLOR_BOLD);
			g.setFont(GFXDisplay.FNT_TEXT);

			print(g, 3, 3, "(" + CharKey.getString(Display.getKeyBindings().getProperty("WEAPON_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 4, "(" + CharKey.getString(Display.getKeyBindings().getProperty("ATTACK1_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 5, "(" + CharKey.getString(Display.getKeyBindings().getProperty("DROP_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 6, "(" + CharKey.getString(Display.getKeyBindings().getProperty("EQUIP_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 7, "(" + CharKey.getString(Display.getKeyBindings().getProperty("TARGET_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 8, "(" + CharKey.getString(Display.getKeyBindings().getProperty("GET_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 9, "(" + CharKey.getString(Display.getKeyBindings().getProperty("JUMP_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 10, "(" + CharKey.getString(Display.getKeyBindings().getProperty("DIVE_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 11, "(" + CharKey.getString(Display.getKeyBindings().getProperty("RELOAD_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 12, "(" + CharKey.getString(Display.getKeyBindings().getProperty("SHOW_SKILLS_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 13, "(" + CharKey.getString(Display.getKeyBindings().getProperty("THROW_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 14, "(" + CharKey.getString(Display.getKeyBindings().getProperty("USE_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 15, "(" + CharKey.getString(Display.getKeyBindings().getProperty("UNEQUIP_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 3, 16, "(" + CharKey.getString(Display.getKeyBindings().getProperty("SWITCH_WEAPONS_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);

			print(g, 6, 3, "Action: Uses a mystic weapon or aims weapon", Color.WHITE);
			print(g, 6, 4, "Attack: Uses a weapon in a given direction", Color.WHITE);
			print(g, 6, 5, "Drop: Drops an item", Color.WHITE);
			print(g, 6, 6, "Equip: Wears a weapon, armor or accesory", Color.WHITE);
			print(g, 6, 7, "Fire: Aims a weapon at a position", Color.WHITE);
			print(g, 6, 8, "Get: Picks up an item", Color.WHITE);
			print(g, 6, 9, "Jump: Jumps in a direction", Color.WHITE);
			print(g, 6, 10, "Plunge: Dive into the water", Color.WHITE);
			print(g, 6, 11, "Reload: Reloads a given weapon", Color.WHITE);
			print(g, 6, 12, "Skills: Allows to use your character skills", Color.WHITE);
			print(g, 6, 13, "Throw: Throws an Item", Color.WHITE);
			print(g, 6, 14, "Use: Uses an Item", Color.WHITE);
			print(g, 6, 15, "Unequip: Take off an item", Color.WHITE);
			print(g, 6, 16, "Switch weapons: Exchange primary for secondary weapon", Color.WHITE);

			print(g, 41, 3, "(" + CharKey.getString(Display.getKeyBindings().getProperty("SHOW_STATS_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 41, 4, "(" + CharKey.getString(Display.getKeyBindings().getProperty("SHOW_INVENTORY_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 41, 5, "(" + CharKey.getString(Display.getKeyBindings().getProperty("LOOK_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 41, 6,
					"(" + CharKey.getString(Display.getKeyBindings().getProperty("SHOW_MESSAGE_HISTORY_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 41, 7, "(" + CharKey.getString(Display.getKeyBindings().getProperty("SHOW_MAP_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			// print(g, 41,8,
			// "("+CharKey.getString(Display.getKeyBindings().getProperty("EXAMINE_LEVEL_MAP_KEY"))+")",
			// GFXDisplay.COLOR_BOLD);
			print(g, 41, 9, "(" + CharKey.getString(Display.getKeyBindings().getProperty("QUIT_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 41, 10, "(" + CharKey.getString(Display.getKeyBindings().getProperty("PROMPT_SAVE_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);
			print(g, 41, 11, "(" + CharKey.getString(Display.getKeyBindings().getProperty("SWITCH_MUSIC_KEY")) + ")",
					GFXDisplay.COLOR_BOLD);

			print(g, 44, 3, "Character info: Shows your skills and attributes", Color.WHITE);
			print(g, 44, 4, "Inventory: Shows the inventory", Color.WHITE);
			print(g, 44, 5, "Look: Identifies map symbols and monsters", Color.WHITE);
			print(g, 44, 6, "Messages: Shows the latest messages", Color.WHITE);
			print(g, 44, 7, "Castle Map: Shows the castle map", Color.WHITE);
			// print(g, 44,8, "Area Map: Show the current area map", Color.WHITE);
			print(g, 44, 9, "Quit: Exits game", Color.WHITE);
			print(g, 44, 10, "Save: Saves game", Color.WHITE);
			print(g, 44, 11, "Switch Music: Turns music on/off", Color.WHITE);
		}

		private void doOk() {
			setVisible(false);
			messageBox.setVisible(true);
		}

		@Override
		public void setVisible(boolean val) {
			super.setVisible(val);
			if (val) {
				requestFocus();
			}
		}
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

	public void drawImageVP(int scrX, int scrY, Image img) {
		si.drawImage(CAMERA.x + scrX * cameraScale, CAMERA.y + scrY * cameraScale, img);
	}
}

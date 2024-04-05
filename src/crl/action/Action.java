package crl.action;

import java.util.List;

import sz.csi.CharKey;
import sz.csi.textcomponents.MenuItem;
import sz.util.OutParameter;
import sz.util.Position;
import crl.actor.Actor;
import crl.feature.Feature;
import crl.item.Item;
import crl.player.Player;
import crl.ui.UserInterface;
import crl.ui.effects.Effect;

public abstract class Action implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	protected Actor performer;

	protected int targetDirection;
	protected Item targetEquipedItem;
	protected Item targetItem;
	protected Position targetPosition;
	protected List<MenuItem> targetMultiItems;

	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int UPRIGHT = 4;
	public static final int UPLEFT = 5;
	public static final int DOWNRIGHT = 6;
	public static final int DOWNLEFT = 7;
	public static final int SELF = 8;

	public abstract String getID();

	public int getCost() {
		return 50;
	}

	protected boolean checkHearts(int q) {
		boolean ret = ((Player) performer).getHearts() >= q;
		if (ret)
			((Player) performer).reduceHearts(q);
		return ret;
	}

	public void setPerformer(Actor what) {
		performer = what;
	}

	public void setDirection(int direction) {
		targetDirection = direction;
	}

	public void setEquipedItem(Item item) {
		targetEquipedItem = item;
	}

	public void setPosition(Position position) {
		targetPosition = position;
	}

	public void setItem(Item what) {
		targetItem = what;
	}

	public void setMultiItems(List<MenuItem> what) {
		this.targetMultiItems = what;
	}

	public boolean needsSpirits() {
		return false;
	}

	public boolean needsDirection() {
		return false;
	}

	public boolean needsEquipedItem() {
		return false;
	}

	public boolean needsPosition() {
		return false;
	}

	public boolean needsItem() {
		return false;
	}

	public boolean needsMultiItems() {
		return false;
	}

	public boolean needsUnderlyingItem() {
		return false;
	}

	public String getPrompUnderlyingItem() {
		return "";
	}

	public String getPromptDirection() {
		return "";
	}

	public String getPromptItem() {
		return "";
	}

	public String getPromptEquipedItem() {
		return "";
	}

	public String getPromptPosition() {
		return "";
	}

	public String getPromptMultiItems() {
		return "";
	}

	public abstract void execute();

	public Position antiVariation(Position pos) {
		return Position.mul(pos, -1);
	}

	public static final Position VARUP = new Position(0, -1);
	public static final Position VARDN = new Position(0, 1);
	public static final Position VARLF = new Position(-1, 0);
	public static final Position VARRG = new Position(1, 0);
	public static final Position VARUR = new Position(1, -1);
	public static final Position VARUL = new Position(-1, -1);
	public static final Position VARDR = new Position(1, 1);
	public static final Position VARDL = new Position(-1, 1);
	public static final Position VARSL = new Position(0, 0);

	public static Position directionToVariation(int code) {
		switch (code) {
		case UP:
			return VARUP;
		case DOWN:
			return VARDN;
		case LEFT:
			return VARLF;
		case RIGHT:
			return VARRG;
		case UPRIGHT:
			return VARUR;
		case UPLEFT:
			return VARUL;
		case DOWNRIGHT:
			return VARDR;
		case DOWNLEFT:
			return VARDL;
		case SELF:
			return VARSL;
		default:
			return null;
		}
	}

	public static int toIntDirection(Position what) {
		switch (what.x()) {
		case 1:
			switch (what.y()) {
			case 1:
				return DOWNRIGHT;
			case 0:
				return RIGHT;
			case -1:
				return UPRIGHT;
			}
		case 0:
			switch (what.y()) {
			case 1:
				return DOWN;
			case -1:
				return UP;
			}
		case -1:
			switch (what.y()) {
			case 1:
				return DOWNLEFT;
			case 0:
				return LEFT;
			case -1:
				return UPLEFT;
			}
		}

		return -1;
	}

	public static int toIntDirection(CharKey ck) {
		if (ck.isUpArrow())
			return Action.UP;
		else if (ck.isLeftArrow())
			return Action.LEFT;
		else if (ck.isRightArrow())
			return Action.RIGHT;
		else if (ck.isDownArrow())
			return Action.DOWN;
		else if (ck.isUpRightArrow())
			return Action.UPRIGHT;
		else if (ck.isUpLeftArrow())
			return Action.UPLEFT;
		else if (ck.isDownLeftArrow())
			return Action.DOWNLEFT;
		else if (ck.isDownRightArrow())
			return Action.DOWNRIGHT;
		else if (ck.isSelfArrow())
			return Action.SELF;
		else
			return -1;
	}

	protected void drawEffect(Effect x) {
		UserInterface.getUI().drawEffect(x);
	}

	public String getSFX() {
		return null;
	}

	public Player getPlayer(Actor a) {
		if (a instanceof Player)
			return (Player) a;
		else
			throw new RuntimeException("getPlayer used in an Actor other than player");
	}

	public boolean canPerform(Actor a) {
		return true;
	}

	public Position getPositionalDirectionFrom(Position p) {
		return Position.add(p, directionToVariation(targetDirection));
	}

	public Position getPositionalDirectionFrom(Position p, int dir) {
		return Position.add(p, directionToVariation(dir));
	}

	protected String invalidationMessage;

	public String getInvalidationMessage() {
		return invalidationMessage;
	}

	public static int getGeneralDirection(Position from, Position to) {
		if (from.x == to.x)
			if (from.y > to.y)
				return UP;
			else if (from.y < to.y)
				return DOWN;
			else
				return SELF;
		else if (from.x > to.x)
			if (from.y > to.y)
				return UPLEFT;
			else if (from.y < to.y)
				return DOWNLEFT;
			else
				return LEFT;
		else {
			if (from.y > to.y)
				return UPRIGHT;
			else if (from.y < to.y)
				return DOWNRIGHT;
			else
				return RIGHT;
		}
	}

	public static void fillNormalPositions(Position where, int direction, OutParameter position1,
			OutParameter position2) {
		switch (direction) {
		case Action.UP:
			position1.setObject(Position.add(where, VARUL));
			position2.setObject(Position.add(where, VARUR));
			break;
		case Action.DOWN:
			position1.setObject(Position.add(where, VARDL));
			position2.setObject(Position.add(where, VARDR));
			break;
		case Action.LEFT:
			position1.setObject(Position.add(where, VARUL));
			position2.setObject(Position.add(where, VARDL));
			break;
		case Action.RIGHT:
			position1.setObject(Position.add(where, VARUR));
			position2.setObject(Position.add(where, VARDR));
			break;
		case Action.UPRIGHT:
			position1.setObject(Position.add(where, VARUP));
			position2.setObject(Position.add(where, VARRG));
			break;
		case Action.UPLEFT:
			position1.setObject(Position.add(where, VARUP));
			position2.setObject(Position.add(where, VARLF));
			break;
		case Action.DOWNLEFT:
			position1.setObject(Position.add(where, VARLF));
			position2.setObject(Position.add(where, VARDN));
			break;
		case Action.DOWNRIGHT:
			position1.setObject(Position.add(where, VARRG));
			position2.setObject(Position.add(where, VARDN));
			break;
		}
	}

	/**
	 * Sometimes when we are executing an action, we need to pause to display an
	 * updated version of the world
	 */
	protected final void actionAnimationPause() {
		try {
			Thread.sleep(150);
		} catch (Exception e) {
		}
	}
	
	protected static boolean checkIfDestroyable(Feature feature) {
		return feature != null && feature.isDestroyable();
	}
}

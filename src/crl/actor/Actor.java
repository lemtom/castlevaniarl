package crl.actor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import sz.util.Debug;
import sz.util.Position;
import sz.util.PriorityEnqueable;
import crl.action.Action;
import crl.ai.ActionSelector;
import crl.game.SFXManager;
import crl.level.Level;
import crl.ui.Appearance;

public class Actor implements Cloneable, java.io.Serializable, PriorityEnqueable {
	private static final long serialVersionUID = 1L;
	protected /* transient */ int positionx;
	protected /* transient */ int positiony;
	protected /* transient */ int positionz;
	protected transient Appearance appearance;

	protected ActionSelector selector;
	private /* transient */ Position position = new Position(0, 0, 0);
	private int hoverHeight;
	private /* transient */ int nextTime = 10;

	public int getCost() {
		return nextTime;
	}

	public void reduceCost(int value) {
		nextTime = nextTime - value;
	}

	public void setNextTime(int value) {
		nextTime = value;
	}

	protected Level level;

	public void updateStatus() {
		for (Entry<String, Integer> entry : hashCounters.entrySet()) {
			Integer counter = entry.getValue();
			if (counter == 0) {
				hashCounters.remove(entry.getKey());
			} else {
				entry.setValue(counter - 1);
			}
		}
	}

	public String getDescription() {
		return "";
	}

	public void execute(Action x) {
		if (x != null) {
			x.setPerformer(this);
			if (x.canPerform(this)) {
				if (x.getSFX() != null)
					SFXManager.play(x.getSFX());
				x.execute();
				// Debug.say("("+x.getCost()+")");
				setNextTime(x.getCost());
			}
		} else {
			setNextTime(50);
		}
		updateStatus();
	}

	public void act() {
		Action x = getSelector().selectAction(this);
		execute(x);
	}

	public void setPosition(int x, int y, int z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	/** Request to be removed from any dispatcher or structure */
	public void die() {
		aWannaDie = true;
	}

	public boolean wannaDie() {
		return aWannaDie;
	}

	private boolean aWannaDie;

	public void setPosition(Position p) {
		position = p;
	}

	public Position getPosition() {
		return position;
	}

	public void setLevel(Level what) {
		level = what;
	}

	public Level getLevel() {
		return level;
	}

	public ActionSelector getSelector() {
		return selector;
	}

	public void setSelector(ActionSelector value) {
		selector = value;
	}

	public Appearance getAppearance() {
		return appearance;
	}

	public void setAppearance(Appearance value) {
		appearance = value;
	}

	public Object clone() {
		try {
			Actor x = (Actor) super.clone();
			if (position != null)
				x.setPosition(new Position(position.x, position.y, position.z));
			return x;
		} catch (CloneNotSupportedException cnse) {
			Debug.doAssert(false, "failed class cast, Feature.clone()");
		}
		return null;
	}

	public void message(String mess) {
	}

	protected Map<String, Integer> hashCounters = new HashMap<>();

	public void setCounter(String counterID, int turns) {
		hashCounters.put(counterID, turns);
	}

	public int getCounter(String counterID) {
		Integer val = hashCounters.get(counterID);
		if (val == null)
			return -1;
		else
			return val;
	}

	public boolean hasCounter(String counterID) {
		return getCounter(counterID) > 0;
	}

	private final HashMap<String, Boolean> hashFlags = new HashMap<>();

	public void setFlag(String flagID, boolean value) {
		hashFlags.put(flagID, value);
	}

	public boolean getFlag(String flagID) {
		Boolean val = hashFlags.get(flagID);
		return val != null && val;
	}

	public int getHoverHeight() {
		return hoverHeight;
	}

	public void setHoverHeight(int hoverHeight) {
		this.hoverHeight = Math.max(hoverHeight, 0);
	}

	public int getStandingHeight() {
		if (isJumping) {
			return startingJumpingHeight + 2;
		}
		if (level.getMapCell(getPosition()) != null)
			return level.getMapCell(getPosition()).getHeight() + getHoverHeight();
		else
			return getHoverHeight();
	}

	private boolean isJumping;

	private int startingJumpingHeight;

	public boolean isJumping() {
		return isJumping;
	}

	public void doJump(int startingJumpingHeight) {
		this.isJumping = true;
		this.startingJumpingHeight = startingJumpingHeight;
	}

	public void stopJump() {
		this.isJumping = false;
	}

}
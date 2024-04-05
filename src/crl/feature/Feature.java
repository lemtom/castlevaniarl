package crl.feature;

import sz.util.*;

import crl.ui.*;

import java.io.Serializable;

import crl.Keycostable;
import crl.Visible;
import crl.game.SFXManager;
import crl.monster.Monster;
import crl.player.*;

/**
 * A feature is something that stays inside the level but may be moved,
 * destroyed or otherwise affected.
 */
public class Feature implements Cloneable, Serializable, Visible, Keycostable {

	private static final long serialVersionUID = 1L;

	private Feature prize;
	private int resistance; // How many blows til it gives the prize (max)
	private int currentResistance; // How many blows til it gives the prize
	private boolean destroyable;
	private boolean isSolid;
	private int heartPrize;
	private int mysticWeaponPrize = -1;
	private int keyPrize;
	private int upgradePrize;
	private Position position;
	private transient Appearance appearance;
	private String ID;
	private String description;
	private String appearanceID;
	private String trigger;
	private int heightMod;
	private int keyCost;
	private String effect;
	private int scorePrize;
	private int healPrize;
	private boolean relevant = true;
	private int faint;
	private int light;

	public String getID() {
		return ID;
	}

	private Feature getPrizeFor(Player p) {
		if (p.deservesUpgrade())
			return FeatureFactory.getFactory().buildFeature("UPGRADE");

		String[] prizeList = null;

		if (p.getPlayerClass() == Player.CLASS_VAMPIREKILLER) {
			if (Util.chance(10)) {
				prizeList = Util.getMysticWeapon(p, 50);
			} else
				prizeList = Util.getOtherPrize(40, 30, false);
		} else {
			prizeList = Util.getOtherPrize(50, 40, false);
		}
		if (prizeList != null)
			return FeatureFactory.getFactory().buildFeature(Util.randomElementOf(prizeList));
		else
			return null;
	}

	public Feature damage(Player p, int damage) {
		currentResistance -= damage;
		if (currentResistance < 0) {
			Feature pPrize = getPrizeFor(p);
			p.getLevel().destroyFeature(this);
			SFXManager.play("wav/breakpot.wav");
			if (pPrize != null) {
				pPrize.setPosition(position.x, position.y, position.z);
				p.getLevel().addFeature(pPrize);
			}
			return pPrize;
		}
		return null;
	}

	public Object clone() {
		try {
			Feature x = (Feature) super.clone();

			if (position != null)
				x.setPosition(position.x, position.y, position.z);
			if (prize != null)
				x.setPrize((Feature) prize.clone());
			return x;
		} catch (CloneNotSupportedException cnse) {
			Debug.doAssert(false, "failed class cast, Feature.clone()");
		}
		return null;
	}

	public void setPrize(Feature what) {
		prize = what;
	}

	public Feature(String pID, Appearance pApp, int resistance, String pDescription, int faint, int light) {
		ID = pID;
		appearance = pApp;
		appearanceID = pApp.getID();
		this.resistance = resistance;
		description = pDescription;
		currentResistance = resistance;
		this.faint = faint;
		this.light = light;
		Debug.doAssert(pApp != null, "No se especifico apariencia pa la featura");
	}

	public void setPosition(int x, int y, int z) {
		position = new Position(x, y, z);
	}

	public Appearance getAppearance() {
		if (appearance == null && appearanceID != null) {
			appearance = AppearanceFactory.getAppearanceFactory().getAppearance(appearanceID);
		}
		return appearance;
	}

	public String getDescription() {
		return description;
	}

	public Position getPosition() {
		return position;
	}

	public void setPrizesFor(Player p) {
		heartPrize = 0;
		mysticWeaponPrize = -1;
		upgradePrize = 0;

		if (p.deservesUpgrade())
			upgradePrize = 1;
	}

	public int getHeartPrize() {
		return heartPrize;
	}

	public void setHeartPrize(int value) {
		heartPrize = value;
	}

	public int getMysticWeaponPrize() {
		return mysticWeaponPrize;
	}

	public void setMysticWeaponPrize(int value) {
		mysticWeaponPrize = value;
	}

	public int getUpgradePrize() {
		return upgradePrize;
	}

	public void setUpgradePrize(int value) {
		upgradePrize = value;
	}

	public int getKeyPrize() {
		return keyPrize;
	}

	public void setKeyPrize(int value) {
		keyPrize = value;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String value) {
		trigger = value;
	}

	public int getHeightMod() {
		return heightMod;
	}

	public void setHeightMod(int value) {
		heightMod = value;
	}

	public int getKeyCost() {
		return keyCost;
	}

	public void setKeyCost(int value) {
		keyCost = value;
	}

	public boolean isSolid() {
		return isSolid;
	}

	public void setSolid(boolean value) {
		isSolid = value;
	}

	public boolean isDestroyable() {
		return destroyable;
	}

	public void setDestroyable(boolean value) {
		destroyable = value;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String value) {
		effect = value;
	}

	public int getScorePrize() {
		return scorePrize;
	}

	public void setScorePrize(int value) {
		scorePrize = value;
	}

	public int getHealPrize() {
		return healPrize;
	}

	public void setHealPrize(int value) {
		healPrize = value;
	}

	public boolean isRelevant() {
		return relevant;
	}

	public void setRelevant(boolean relevant) {
		this.relevant = relevant;
	}

	public boolean isVisible() {
		return !getAppearance().getID().equals("VOID");
	}

	public int getFaint() {
		return faint;
	}

	public void setFaint(int faint) {
		this.faint = faint;
	}

	public int getLight() {
		return light;
	}

	public void damage(Monster m) {
		currentResistance -= m.getAttack();
		if (currentResistance < 0) {
			m.getLevel().destroyFeature(this);
		}
	}
}
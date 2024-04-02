package crl.feature;

import crl.actor.*;
import crl.ui.*;

public class SmartFeature extends Actor implements Cloneable {
	private boolean destroyable;
	private int damageOnStep;
	private transient Appearance appearance;
	private String ID;
	private String description;
	private String appearanceID;
	private int height;

	public void setHeight(int val) {
		height = val;
	}

	public int getHeight() {
		return height;
	}

	private String effectOnStep;

	public SmartFeature(String pID, String pDescription, Appearance pAppearance) {
		ID = pID;
		description = pDescription;
		appearance = pAppearance;
		appearanceID = pAppearance.getID();
	}

	public boolean isDestroyable() {
		return destroyable;
	}

	public void setDestroyable(boolean value) {
		destroyable = value;
	}

	public int getDamageOnStep() {
		return damageOnStep;
	}

	public void setDamageOnStep(int value) {
		damageOnStep = value;
	}

	@Override
	public Object clone() {
		// try {
		SmartFeature x = (SmartFeature) super.clone();
		x.setSelector(selector.derive());
		return x;
		/*
		 * } catch (CloneNotSupportedException cnse){ Debug.doAssert(false,
		 * "failed class cast, Feature.clone()");
		 */
//		}                        */
//		return null;
	}

	@Override
	public Appearance getAppearance() {
		if (appearance == null) {
			if (appearanceID != null)
				appearance = AppearanceFactory.getAppearanceFactory().getAppearance(appearanceID);
		}
		return appearance;
	}

	@Override
	public void setAppearance(Appearance value) {
		appearance = value;
	}

	public String getID() {
		return ID;
	}

	public void setID(String value) {
		ID = value;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String value) {
		description = value;
	}

	public String getEffectOnStep() {
		return effectOnStep;
	}

	public void setEffectOnStep(String value) {
		effectOnStep = value;
	}

	public boolean isVisible() {
		return !getAppearance().getID().equals("VOID");
	}
}
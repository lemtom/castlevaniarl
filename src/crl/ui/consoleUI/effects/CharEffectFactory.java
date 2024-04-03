package crl.ui.consoleUI.effects;

import crl.game.Game;
import crl.ui.effects.Effect;
import crl.ui.effects.EffectFactory;
import sz.util.Position;

import java.util.HashMap;

public class CharEffectFactory extends EffectFactory {
	private HashMap<String, Effect> effects = new HashMap<>();

	public void setEffects(Effect[] effectsA) {
        for (Effect effect : effectsA) {
            effects.put(effect.getID(), effect);
        }
	}

	public boolean isDirectedEffect(String id) {
		return effects.containsKey(id) && effects.get(id) instanceof CharDirectedEffect;
	}

	public Effect createDirectedEffect(Position start, Position end, String ID, int length) {
		try {
			CharDirectedEffect ef = (CharDirectedEffect) effects.get(ID);
			ef.set(start, start, end, length);
			return ef;
		} catch (ClassCastException cce) {
			Game.addReport("ClassCastException with effect " + ID + " to Directed Effect");
			return null;
		} catch (NullPointerException cce) {
			Game.addReport("NullPointerException with effect " + ID);
			return null;
		}
	}

	public Effect createDirectionalEffect(Position start, int direction, int depth, String ID) {
		try {
			CharDirectionalEffect ef = (CharDirectionalEffect) effects.get(ID);
			ef.set(start, direction, depth);
			return ef;
		} catch (ClassCastException cce) {
			Game.addReport("ClassCastException with effect " + ID + " to Directed Effect");
			return null;
		} catch (NullPointerException cce) {
			Game.addReport("NullPointerException with effect " + ID);
			return null;
		}
	}

	public Effect createLocatedEffect(Position location, String ID) {
		try {
			CharEffect ef = (CharEffect) effects.get(ID);
			ef.set(location);
			return ef;
		} catch (ClassCastException cce) {
			Game.addReport("ClassCastException with effect " + ID + " to Directed Effect");
			return null;
		} catch (NullPointerException cce) {
			Game.addReport("NullPointerException with effect " + ID);
			return null;
		}
	}

}

package crl.ui.graphicsUI.effects;

import crl.ui.effects.Effect;
import crl.ui.effects.EffectFactory;
import sz.util.Position;

import java.util.HashMap;

public class GFXEffectFactory extends EffectFactory{
	private HashMap<String, Effect> effects = new HashMap<>();
	
	public void setEffects(Effect[] effectsA){
        for (Effect effect : effectsA) {
            effects.put(effect.getID(), effect);
        }
	}

	
	public boolean isDirectedEffect(String ID) {
		return effects.containsKey(ID) && (effects.get(ID) instanceof GFXDirectedEffect);
	}

	public Effect createDirectedEffect(Position start, Position end, String ID, int length) {
		try {
			GFXDirectedEffect x = (GFXDirectedEffect) effects.get(ID);
			if (x == null){
				System.out.println("Warning! effect "+ID+" is not registered");
				return null;
			}
			x.set(start, start, end, length);
			return x;
		} catch (ClassCastException cce){
			System.out.println("Warning! effect "+ID+" is not directed");
			return null;
		}
	}
	
	public Effect createDirectionalEffect(Position start, int direction, int depth, String ID) {
		try {
			GFXDirectionalEffect x = (GFXDirectionalEffect) effects.get(ID);
			if (x == null){
				System.out.println("Warning! effect "+ID+" is not registered");
				return null;
			}
			x.set(start, direction, depth);
			return x;
		} catch (ClassCastException cce){
			System.out.println("Warning! effect "+ID+" is not directional");
			return null;
		}
	}
	
	public Effect createLocatedEffect(Position location, String ID) {
		try {
			GFXEffect x = (GFXEffect) effects.get(ID);
			if (x == null){
				System.out.println("Warning! effect "+ID+" is not registered");
				return null;
			}
			x.set(location);
			return x;
		} catch (ClassCastException cce){
			System.out.println("Warning! effect "+ID+" is not Located");
			return null;
		}
	}

}

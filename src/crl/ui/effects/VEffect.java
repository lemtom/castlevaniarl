package crl.ui.effects;

import java.util.*;

public class VEffect {
   	Vector<Effect> effects;

	public void addEffect(Effect what){
		effects.add(what);
	}

	public Effect getEffect(int index){
		return effects.elementAt(index);
	}

	public VEffect(int size){
		effects = new Vector<>(size);
	}

	public void erase(){
		effects = new Vector<>(effects.capacity());
	}

	public int size(){
		return effects.size();
	}

}
package crl.ui.effects;

import java.util.ArrayList;

public class VEffect {
   	ArrayList<Effect> effects;

	public void addEffect(Effect what){
		effects.add(what);
	}

	public Effect getEffect(int index){
		return effects.get(index);
	}

	public VEffect(int size){
		effects = new ArrayList<>(size);
	}

	public void erase(){
		effects = new ArrayList<>();
	}

	public int size(){
		return effects.size();
	}

}
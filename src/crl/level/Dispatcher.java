package crl.level;

import java.io.Serializable;
import java.util.*;

import sz.util.*;
import crl.actor.*;

public class Dispatcher implements Serializable{
	private static final long serialVersionUID = 1L;

	private SZPriorityQueue actors;
	private int countdown;
	private Actor fixed;

	public Dispatcher(){
		actors = new SZPriorityQueue();
	}

	public boolean contains (Actor what){
		return actors.contains(what);
	}

	public List<PriorityEnqueable> getActors(){
		return actors.getArrayList();
    }

	public static int ixx = 0;
	public Actor getNextActor(){
		//Debug.say("---"+(ixx++)+"--------");
		if (countdown > 0){
			countdown--;
			return fixed;
		}
		
		//actors.printStatus();
		Actor x = (Actor) actors.unqueue();
		//Debug.say(x);

		while (x != null && x.wannaDie()){
			actors.remove(x);
			x  = (Actor) actors.unqueue();
		}
		//actors.enqueue(x);
		return x;
    }
	
	public void returnActor(Actor what){
		if (!actors.contains(what))
			actors.enqueue(what);
	}

    public void addActor(PriorityEnqueable what){
    	if (!actors.contains(what))
    		actors.enqueue(what);
	}

	public void addActor(Actor what, boolean high, Object classObj){
		if (!actors.contains(what))
			actors.forceToFront(what, classObj);
	}

	public void addActor(Actor what, boolean high){
		if (!actors.contains(what))
			actors.forceToFront(what);
	}

	public void removeActor(Actor what){
		actors.remove(what);
	}

	public void setFixed(Actor who, int howMuch){
		countdown = howMuch;
		fixed = who;
	}
	
	public void removeAll(){
		actors.removeAll();
	}
}
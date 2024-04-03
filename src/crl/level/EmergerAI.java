package crl.level;

import crl.action.*;
import crl.ai.*;
import crl.actor.*;

public class EmergerAI implements ActionSelector {
private static final long serialVersionUID = 1L;
	private int counter;

	public String getID(){
		return "Emerge";
	}

	public Action selectAction(Actor who) {
		Emerger x = (Emerger) who;
		counter++;
		if (x.getCounter() < counter){
			who.die();
			return EmergeMonster.getAction();
    	}
        return null;
	}

	 public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}
}
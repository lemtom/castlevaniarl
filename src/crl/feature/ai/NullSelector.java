package crl.feature.ai;

import crl.action.*;
import crl.ai.*;
import crl.actor.*;

public class NullSelector implements ActionSelector, Cloneable {
private static final long serialVersionUID = 1L;
	public String getID(){
	     return "NULL_SELECTOR";
	}

	public Action selectAction(Actor who){
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
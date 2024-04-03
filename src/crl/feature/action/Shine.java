package crl.feature.action;

import crl.action.*;

public class Shine extends Action {
private static final long serialVersionUID = 1L;
	
	public String getID(){
		return "Shine";
	}
	
	private static Shine singleton = new Shine();

	public void execute(){
    }

	public static Shine getAction(){
		return singleton;
	}
}
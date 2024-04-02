package crl.ui.consoleUI.effects;


import sz.csi.ConsoleSystemInterface;
import crl.ui.consoleUI.ConsoleUserInterface;

public class CharFlashEffect extends CharEffect{
	private int color;

    public CharFlashEffect(String ID, int color){
    	super (ID);
    	this.color = color;
    }

	public void drawEffect(ConsoleUserInterface ui, ConsoleSystemInterface si){
		si.flash(color);
		//animationPause();
	}

}
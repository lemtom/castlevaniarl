package crl.ui.consoleUI.effects;

import sz.csi.ConsoleSystemInterface;
import sz.util.*;
import crl.ui.consoleUI.ConsoleUserInterface;
import java.util.*;

public class CharSequentialEffect extends CharEffect{
	private Vector<Position> sequence;
	private String tiles;
	private int color;

	public CharSequentialEffect(String ID, Vector<Position> sequence, String tiles, int color, int delay){
    	super(ID);
    	setAnimationDelay(delay);
		this.tiles = tiles;
		this.color = color;
		this.sequence = sequence;
    }

	public void drawEffect(ConsoleUserInterface ui, ConsoleSystemInterface si){
		Position relative = Position.subs(getPosition(), ui.getPlayer().getPosition());
		Position center = Position.add(ui.PC_POS, relative);
		int tileIndex = 0;
		Enumeration<Position> seq = sequence.elements();
		while (seq.hasMoreElements()){
			Position nextPosition = Position.add(center, seq.nextElement());
			tileIndex++;
			if (tileIndex == tiles.length())
				tileIndex = 0;
			if (ui.insideViewPort(nextPosition))
				si.print(nextPosition.x, nextPosition.y, tiles.charAt(tileIndex), color);
			animationPause();
		}
	}

}
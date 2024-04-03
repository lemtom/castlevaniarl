package crl.ui.consoleUI.effects;

import crl.ui.consoleUI.ConsoleUserInterface;
import crl.ui.effects.Effect;
import sz.csi.ConsoleSystemInterface;

public abstract class CharEffect extends Effect {
	protected CharEffect(String id) {
		super(id);
	}

	protected CharEffect(String id, int delay) {
		super(id, delay);
	}

	public abstract void drawEffect(ConsoleUserInterface ui, ConsoleSystemInterface si);

}

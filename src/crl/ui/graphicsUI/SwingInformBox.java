package crl.ui.graphicsUI;

import javax.swing.*;

public class SwingInformBox extends JTextArea {
	private static final long serialVersionUID = 1L;

	public void clear() {
		boolean wait = false;
		do {
			try {
				setText("");
				wait = false;
			} catch (Error e) {
				wait = true;
			}
		} while (wait);
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean isFocusable() {
		return false;
	}

	public synchronized void addText(String txt) {
		boolean wait = false;
		do {
			try {
				setText(getText() + txt + ".\n");
				wait = false;
			} catch (Error e) {
				wait = true;
			}
		} while (wait);
	}
}

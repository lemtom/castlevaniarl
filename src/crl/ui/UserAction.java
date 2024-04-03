package crl.ui;

import crl.action.Action;

/** Links an Action with a KeyCode with which it is triggered */
public class UserAction implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private int keyCode;
	private Action action;

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int value) {
		/*
		 * if (value<0 || value > 115) keyCode = 0; else
		 */
		keyCode = value;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action value) {
		action = value;
	}

	public UserAction(Action action, int key) {
		setKeyCode(key);
		setAction(action);
	}
}
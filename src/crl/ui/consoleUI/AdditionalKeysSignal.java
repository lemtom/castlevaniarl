package crl.ui.consoleUI;

public class AdditionalKeysSignal extends Exception {

	private static final long serialVersionUID = 1L;
	private int keycode;

	public int getKeyCode() {
		return keycode;
	}

	public void setKeycode(int keycode) {
		this.keycode = keycode;
	}
	
	public AdditionalKeysSignal(int keycode){
		setKeycode(keycode);
	}
}

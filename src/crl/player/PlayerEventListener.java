package crl.player;

public interface PlayerEventListener {
	void informEvent(int code, Object param);
	void informEvent(int code);
}
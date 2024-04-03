package crl.game;

public class CRLException extends Exception {
	private static final long serialVersionUID = 1L;

	private String message;

	public CRLException(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}
package crl.ui;

public abstract class Appearance /* this must not be serializable for complete decoupling */ {
	private String id;

	protected Appearance(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}
}
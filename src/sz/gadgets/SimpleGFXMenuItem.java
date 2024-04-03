package sz.gadgets;

import java.awt.*;

public class SimpleGFXMenuItem implements GFXMenuItem {
	private static final long serialVersionUID = 1L;

	private String description;
	private int value;

	public SimpleGFXMenuItem(String description, int value) {
		this.description = description;
		this.value = value;
	}

	public String getMenuDescription() {
		return description;
	}

	public String getMenuDetail() {
		return null;
	}

	public Image getMenuImage() {
		return null;
	}

	public int getValue() {
		return value;
	}

	// Don't
	public char getMenuChar() {
		return 0;
	}

	public int getMenuColor() {
		return 0;
	}

}

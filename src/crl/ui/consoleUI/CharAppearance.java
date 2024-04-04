package crl.ui.consoleUI;

import crl.ui.Appearance;
import sz.csi.ConsoleSystemInterface;

public class CharAppearance extends Appearance {
	private char character;
	private int color;

	public static final CharAppearance VOID = new CharAppearance("VOID", ' ', ConsoleSystemInterface.BLACK);

	public static CharAppearance getVoidAppearance() {
		return VOID;
	}

	public CharAppearance(String id, char character, int color) {
		super(id);
		this.character = character;
		this.color = color;
	}

	public char getChar() {
		return character;
	}

	public int getColor() {
		return color;
	}

	public static final int BLACK = 0;
	public static final int DARK_BLUE = 1;
	public static final int GREEN = 2;
	public static final int TEAL = 3;
	public static final int DARK_RED = 4;
	public static final int PURPLE = 5;
	public static final int BROWN = 6;
	public static final int LIGHT_GRAY = 7;
	public static final int GRAY = 8;
	public static final int BLUE = 9;
	public static final int LEMON = 10;
	public static final int CYAN = 11;
	public static final int RED = 12;
	public static final int MAGENTA = 13;
	public static final int YELLOW = 14;
	public static final int WHITE = 15;

	public static int getColor(String colorName) {
		if (colorName == null)
			return -1;
		if (colorName.equals("BLACK"))
			return BLACK;
		if (colorName.equals("DARK_BLUE"))
			return DARK_BLUE;
		if (colorName.equals("GREEN"))
			return GREEN;
		if (colorName.equals("TEAL"))
			return TEAL;
		if (colorName.equals("DARK_RED"))
			return DARK_RED;
		if (colorName.equals("PURPLE"))
			return PURPLE;
		if (colorName.equals("BROWN"))
			return BROWN;
		if (colorName.equals("LIGHT_GRAY"))
			return LIGHT_GRAY;
		if (colorName.equals("GRAY"))
			return GRAY;
		if (colorName.equals("BLUE"))
			return BLUE;
		if (colorName.equals("LEMON"))
			return LEMON;
		if (colorName.equals("CYAN"))
			return CYAN;
		if (colorName.equals("RED"))
			return RED;
		if (colorName.equals("MAGENTA"))
			return MAGENTA;
		if (colorName.equals("YELLOW"))
			return YELLOW;
		if (colorName.equals("WHITE"))
			return WHITE;
		return -1;
	}
}
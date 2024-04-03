package sz.csi;

import sz.util.Position;

public interface ConsoleSystemInterface {
	/**
	 * Prints a character on the console
	 * 
	 * @param what  The character to be printed
	 * @param color The color, one of the ConsoleSystemInterface constants
	 */
	void print(int x, int y, char what, int color);

	/**
	 * Same as print but must check for validity of the coordinates
	 * 
	 * @param what  The character to be printed
	 * @param color The color, one of the ConsoleSystemInterface constants
	 */
	void safeprint(int x, int y, char what, int color);

	/**
	 * Prints a String on the console
	 * 
	 * @param what  The string to be printed
	 * @param color The color, one of the ConsoleSystemInterface constants
	 */
	void print(int x, int y, String what, int color);

	/**
	 * Prints a String on the console with the default color
	 * 
	 * @param what The String to be printed
	 */
	void print(int x, int y, String what);

	/**
	 * Checks what character is at a given position
	 * 
	 * @return The character at the x,y position
	 */
	char peekChar(int x, int y);

	/**
	 * Checks what color is at a given position
	 * 
	 * @return The color at the x,y position
	 */
	int peekColor(int x, int y);

	/**
	 * Waits until a key is pressed and returns it
	 * 
	 * @return The key that was pressed
	 */
	CharKey inkey();

	/**
	 * Locates the input caret on a given position
	 *
     */
	void locateCaret(int x, int y);

	/**
	 * Reads a string from the keyboard
	 * 
	 * @return The String that was read after pressing enter
	 */
	String input();

	/**
	 * Reads a string from the keyboard with a maximum length
	 * 
	 * @return The String that was read after pressing enter
	 */
	String input(int length);

	/**
	 * Checks if the position is valid
	 * 
	 * @return true if the position is valid
	 */
	boolean isInsideBounds(Position e);

	/**
	 * Clears the screen
	 *
	 */
	void cls();

	/**
	 * Refreshes the screen, printing all characters that were buffered
	 * <p>
	 * Some implementations may instead write directly to the console
	 */
	void refresh();

	/**
	 * Refreshes the screen, printing all characters that were buffered, and
	 * interrupts the Thread
	 * <p>
	 * Some implementations may instead write directly to the console
	 */
	void refresh(Thread t);

	/**
	 * Makes the screen flash with a given color
	 *
     */
	void flash(int color);

	/**
	 * Sets whether or not a buffer will be used
	 *
     */
	void setAutoRefresh(boolean value);

	/**
	 * Waits for the user to press a key
	 *
     */
	void waitKey(int keyCode);

	/**
	 * Saves the screen contents to a backup buffer
	 *
	 */
	void saveBuffer();

	/**
	 * Restores the contents of the backup buffer to screen
	 *
	 */
	void restore();

	int BLACK = 0;
	int DARK_BLUE = 1;
	int GREEN = 2;
	int TEAL = 3;
	int DARK_RED = 4;
	int PURPLE = 5;
	int BROWN = 6;
	int LIGHT_GRAY = 7;
	int GRAY = 8;
	int BLUE = 9;
	int LEMON = 10;
	int CYAN = 11;
	int RED = 12;
	int MAGENTA = 13;
	int YELLOW = 14;
	int WHITE = 15;
}
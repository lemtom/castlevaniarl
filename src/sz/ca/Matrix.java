package sz.ca;

public class Matrix {
	private int[][] values;
	private int[][] futureValues;

	/** Sets the future state of cell(x,y) to value */
	public void setFuture(int value, int x, int y) {
		futureValues[x][y] = value;
	}

	/** Sets the future state of cell(x,y) to value */
	public void setPresent(int value, int x, int y) {
		values[x][y] = value;
	}

	/** returns the current state of cell(x,y) */
	public int get(int x, int y) {
		return values[x][y];
	}

	public int getWidth() {
		return values.length;
	}

	public int getHeight() {
		return values[0].length;
	}

	/** All future values become current */
	public void advance() {
		for (int x = 0; x < values.length; x++) {
			System.arraycopy(futureValues[x], 0, values[x], 0, values[0].length);
		}
	}

	/**
	 * Returns the number of cells of type type that surround the matrix at x,y,
	 * wrapping from the sides
	 */
	public int getSurroundingCount(int x, int y, int type) {

		int upIndex = y == 0 ? getHeight() - 1 : y - 1;

		int downIndex = y == getHeight() - 1 ? 0 : y + 1;

		int rightIndex = x == getWidth() - 1 ? 0 : x + 1;

		int leftIndex = x == 0 ? getWidth() - 1 : x - 1;

		return ternary(type, upIndex, leftIndex) + ternary(type, y, leftIndex) + ternary(type, downIndex, leftIndex)
				+ ternary(type, upIndex, rightIndex) + ternary(type, y, rightIndex)
				+ ternary(type, downIndex, rightIndex) + ternary(type, downIndex, x) + ternary(type, upIndex, x);
	}

	/**
	 * Returns the number of cells of type type that surround the matrix at x,y
	 */
	public int getSurroundingCountNoWrap(int x, int y, int type) {
		return (y == 0 || x == 0 ? 0 : ternary(x - 1, y - 1, type)) + (x == 0 ? 0 : ternary(x - 1, y, type))
				+ (x == 0 || y == getHeight() - 1 ? 0 : ternary(x - 1, y + 1, type))
				+ (y == 0 || x == getWidth() - 1 ? 0 : ternary(x + 1, y - 1, type))
				+ (x == getWidth() - 1 ? 0 : ternary(x + 1, y, type))
				+ (x == getWidth() - 1 || y == getHeight() - 1 ? 0 : ternary(x + 1, y + 1, type))
				+ (y == getHeight() - 1 ? 0 : ternary(x, y + 1, type)) + (y == 0 ? 0 : ternary(x, y - 1, type));
	}

	private int ternary(int x, int y, int type) {
		return values[x][y] == type ? 1 : 0;
	}

	/** Sets all the values and old Values to 0 */
	public void clean() {
		values = new int[values.length][values[0].length];
		futureValues = new int[values.length][values[0].length];
	}

	public Matrix(int xdim, int ydim) {
		values = new int[xdim][ydim];
		futureValues = new int[xdim][ydim];
	}

	public Matrix(int dim) {
		this(dim, dim);
	}

	public int[][] getArrays() {
		return values;
	}
}

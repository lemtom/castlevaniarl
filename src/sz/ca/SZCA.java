package sz.ca;

public class SZCA {
	private static CARule[] aRules;
	private static Matrix aMatrix;
	private static int generation;
	private static boolean wrap;

	public static void runCA(Matrix map, CARule[] rules, int generations, boolean wrap) {
		aMatrix = map;
		aRules = rules;
		SZCA.wrap = wrap;
		for (int i = 0; i < generations; i++)
			step();
	}

	public static void step() {
		int WIDTH = aMatrix.getWidth();
		int HEIGHT = aMatrix.getHeight();

		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				for (CARule aRule : aRules) {
					aRule.apply(x, y, aMatrix, wrap);
				}
			}
		}

		aMatrix.advance();
		generation++;
	}
}

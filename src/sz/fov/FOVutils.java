package sz.fov;

public class FOVutils {
	static double slope(double x1, double y1, double x2, double y2) {
		double xDiff = x1 - x2;
		double yDiff = y1 - y2;
		if (yDiff != 0) {
			return xDiff / yDiff;
		} else {
			return 0;
		}
	}

	static double invSlope(double x1, double y1, double x2, double y2) {
		double slope = slope(x1, y1, x2, y2);
		if (slope != 0) {
			return 1 / slope;
		} else {
			return 0;
		}
	}

	static void applyCell(FOVMap map, int x, int y) {
		map.setSeen(x, y);
	}

	static void checkAndApplyY(FOVMap map, int center, int xCheck, int yCheck) {
		if (yCheck != center) {
			// apply cell
			applyCell(map, xCheck, yCheck);
		}
	}

	static void checkAndApplyX(FOVMap map, int center, int yCheck, int xCheck) {
		if (xCheck != center) {
			// apply cell
			applyCell(map, xCheck, yCheck);
		}
	}
}

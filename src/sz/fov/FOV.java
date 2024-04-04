package sz.fov;

import sz.util.Position;

/*
 * RECURSIVE SHADOWCASTING
 * Original Author: Björn Bergström
 * e-mail: dungeondweller@swipnet.se
 * 
 * Translated to Java: Santiago Zapata
 * e-mail: java.koder@gmail.com
 *
 * fov.cpp part of rscfovdemo
 *
 * implementation of recursive shadowcasting
 *
 * 060322: Santiago Zapata - translated to Java
 * 
 * 020125: Björn Bergström - changed from float to double to remove compiler
 *         warnings
 * 020125: Björn Bergström - included a check to avoid orthogonal edges to be
 *         scanned more than once
 * 020125: Greg McIntyre - declared the nwL, neL etc in FOV::start outside the
 *         for loops
 *
 */

public class FOV {

	/**
	 * scanNW2N scans the octant covering the area from north west to north from
	 * left to right the method ignores the octants starting and ending cells since
	 * they have been applied in FOV::start
	 */
	void scanNW2N(FOVMap map, int xCenter, int yCenter, int distance, int maxRadius, double startSlope,
			double endSlope) {
		if (distance > maxRadius) {
			return;
		}

		// calculate start and end cell of the scan
		int xStart = (int) (xCenter + 0.5 - (startSlope * distance));
		int xEnd = (int) (xCenter + 0.5 - (endSlope * distance));
		int yCheck = yCenter - distance;

		// is the starting cell the leftmost cell in the octant?
		// NO: call applyCell() to starting cell
		// YES: it has already been applied in FOV::start()
		if (xStart != xCenter - (distance)) {
			FOVutils.applyCell(map, xStart, yCheck);
		}

		// find out if starting cell blocks LOS
		boolean prevBlocked = this.scanCell(map, xStart, yCheck);

		// scan from the cell after the starting cell (xStart+1) to end cell of
		// scan (xCheck<=xEnd)
		for (int xCheck = xStart + 1; xCheck <= xEnd; xCheck++) {
			// is the current cell the rightmost cell in the octant?
			// NO: call applyCell() to current cell
			// YES: it has already been applied in FOV::start()
			FOVutils.checkAndApplyX(map, xCenter, yCheck, xCheck);

			// cell blocks LOS
			// if previous cell didn't block LOS (prevBlocked==false) we have
			// hit a 'new' section of walls. a new scan will be started with an
			// endSlope that 'brushes' by to the left of the blocking cell
			//
			// +---+a####+---+ @ = [xCenter+0.5,yCenter+0.5]
			// | |#####| | a = old [xCheck,yCheck]
			// | |#####| | b = new [xCheck-0.00001,yCheck+0.99999]
			// | |#####| |
			// +---b#####+---+
			// +---++---++---+
			// | || || |
			// | || || @ |
			// | || || |
			// +---++---++---+
			//
			if (this.scanCell(map, xCheck, yCheck)) {
				if (!prevBlocked) {
					this.scanNW2N(map, xCenter, yCenter, distance + 1, maxRadius, startSlope,
							FOVutils.slope(xCenter + 0.5, yCenter + 0.5, xCheck - 0.000001, yCheck + 0.999999));
				}
				prevBlocked = true;
			}

			// cell doesn't block LOS
			// if the cell is the first non-blocking cell after a section of walls
			// we need to calculate a new startSlope that 'brushes' by to the right
			// of the blocking cells
			//
			// #####a---++---+ @ = [xCenter+0.5,yCenter+0.5]
			// #####| || | a = new and old [xCheck,yCheck]
			// #####| || |
			// #####| || |
			// #####+---++---+
			// +---++---++---+
			// | || || |
			// | || || @ |
			// | || || |
			// +---++---++---+
			//
			else {
				if (prevBlocked) {
					startSlope = FOVutils.slope(xCenter + 0.5, yCenter + 0.5, xCheck, yCheck);
				}
				prevBlocked = false;
			}
		}

		// if the last cell of the scan didn't block LOS a new scan should be
		// started
		if (!prevBlocked) {
			this.scanNW2N(map, xCenter, yCenter, distance + 1, maxRadius, startSlope, endSlope);
		}
	}

	/**
	 * scanNE2N scans the octant covering the area from north east to north from
	 * right to left the method ignores the octants starting and ending cells since
	 * they have been applied in FOV::start
	 */
	void scanNE2N(FOVMap map, int xCenter, int yCenter, int distance, int maxRadius, double startSlope,
			double endSlope) {
		if (distance > maxRadius) {
			return;
		}

		// calculate start and end cell of the scan
		int xStart = (int) (xCenter + 0.5 - (startSlope * distance));
		int xEnd = (int) (xCenter + 0.5 - (endSlope * distance));
		int yCheck = yCenter - distance;

		// is starting cell the rightmost cell in the octant?
		// NO: call applyCell() to starting cell
		// YES: it has already been applied in FOV::start()
		if (xStart != xCenter - (-1 * distance)) {
			FOVutils.applyCell(map, xStart, yCheck);
		}

		// find out if starting cell blocks LOS
		boolean prevBlocked = this.scanCell(map, xStart, yCheck);

		// scan from the cell after the starting cell (xStart-1) to end cell of
		// scan (xCheck>=xEnd)
		for (int xCheck = xStart - 1; xCheck >= xEnd; xCheck--) {
			FOVutils.checkAndApplyX(map, xCenter, yCheck, xCheck);

			// cell blocks LOS
			// if previous cell didn't block LOS (prevBlocked==false) we have
			// hit a 'new' section of walls. a new scan will be started with an
			// endSlope that 'brushes' by to the right of the blocking cell
			//
			// +---+a####+---+ @ = [xCenter+0.5,yCenter+0.5]
			// | |#####| | a = old [xCheck,yCheck]
			// | |#####| | b = new [xCheck+0.9999,yCheck-0.00001]
			// | |#####| |
			// +---+#####b---+
			// +---++---++---+
			// | || || |
			// | @ || || |
			// | || || |
			// +---++---++---+
			//
			if (this.scanCell(map, xCheck, yCheck)) {
				if (!prevBlocked) {
					this.scanNE2N(map, xCenter, yCenter, distance + 1, maxRadius, startSlope,
							FOVutils.slope(xCenter + 0.5, yCenter + 0.5, (double) xCheck + 1, yCheck + 0.99999));
				}
				prevBlocked = true;
			}

			// cell doesn't block LOS
			// if the cell is the first non-blocking cell after a section of walls
			// we need to calculate a new startSlope that 'brushes' by to the left
			// of the blocking cells
			//
			// +---+a---b##### @ = [xCenter+0.5,yCenter+0.5]
			// | || |##### a = old [xCheck,yCheck]
			// | || |##### b = new [xCheck+0.99999,yCheck]
			// | || |#####
			// +---++---+#####
			// +---++---++---+
			// | || || |
			// | @ || || |
			// | || || |
			// +---++---++---+
			//
			else {
				if (prevBlocked) {
					startSlope = FOVutils.slope(xCenter + 0.5, yCenter + 0.5, xCheck + 0.9999999, yCheck);
				}
				prevBlocked = false;
			}
		}

		// if the last cell of the scan didn't block LOS a new scan should be
		// started
		if (!prevBlocked) {
			this.scanNE2N(map, xCenter, yCenter, distance + 1, maxRadius, startSlope, endSlope);
		}
	}

	/**
	 * scanNW2W scans the octant covering the area from north west to west from top
	 * to bottom the method ignores the octants starting and ending cells since they
	 * have been applied in FOV::start
	 */
	void scanNW2W(FOVMap map, int xCenter, int yCenter, int distance, int maxRadius, double startSlope,
			double endSlope) {
		if (distance > maxRadius) {
			return;
		}

		// calculate start and end cell of the scan
		int yStart = (int) (yCenter + 0.5 - (startSlope * distance));
		int yEnd = (int) (yCenter + 0.5 - (endSlope * distance));
		int xCheck = xCenter - distance;

		// is starting cell the topmost cell in the octant?
		// NO: call applyCell() to starting cell
		// YES: it has already been applied in FOV::start()
		if (yStart != yCenter - (distance)) {
			FOVutils.applyCell(map, xCheck, yStart);
		}

		// find out if starting cell blocks LOS
		boolean prevBlocked = this.scanCell(map, xCheck, yStart);

		// scan from the cell after the starting cell (yStart+1) to end cell of
		// scan (yCheck<=yEnd)
		for (int yCheck = yStart + 1; yCheck <= yEnd; yCheck++) {
			FOVutils.checkAndApplyY(map, yCenter, xCheck, yCheck);

			// cell blocks LOS
			// if previous cell didn't block LOS (prevBlocked==false) we have
			// hit a 'new' section of walls. a new scan will be started with an
			// endSlope that 'brushes' by the top of the blocking cell (see fig.)
			//
			// +---++---++---+ @ = [xCenter+0.5,yCenter+0.5]
			// | || || | a = old [xCheck,yCheck]
			// | || || | b = new [xCheck+0.99999,yCheck-0.00001]
			// | || || |
			// +---b+---++---+
			// a####+---++---+
			// #####| || |
			// #####| || |
			// #####| || |
			// #####+---++---+
			// +---++---++---+
			// | || || |
			// | || || @ |
			// | || || |
			// +---++---++---+
			//
			if (this.scanCell(map, xCheck, yCheck)) {
				if (!prevBlocked) {
					this.scanNW2W(map, xCenter, yCenter, distance + 1, maxRadius, startSlope,
							FOVutils.invSlope(xCenter + 0.5, yCenter + 0.5, xCheck + 0.99999, yCheck - 0.00001));
				}
				prevBlocked = true;
			}

			// cell doesn't block LOS
			// if the cell is the first non-blocking cell after a section of walls
			// we need to calculate a new startSlope that 'brushes' by the bottom
			// of the blocking cells
			//
			// #####+---++---+ @ = [xCenter+0.5,yCenter+0.5]
			// #####| || | a = old and new [xCheck,yCheck]
			// #####| || |
			// #####| || |
			// #####+---++---+
			// a---++---++---+
			// | || || |
			// | || || |
			// | || || |
			// +---++---++---+
			// +---++---++---+
			// | || || |
			// | || || @ |
			// | || || |
			// +---++---++---+
			//
			else {
				if (prevBlocked) {
					startSlope = FOVutils.invSlope(xCenter + 0.5, yCenter + 0.5, xCheck, yCheck);
				}
				prevBlocked = false;
			}
		}

		// if the last cell of the scan didn't block LOS a new scan should be
		// started
		if (!prevBlocked) {
			this.scanNW2W(map, xCenter, yCenter, distance + 1, maxRadius, startSlope, endSlope);
		}
	}

	/**
	 * scanSW2W scans the octant covering the area from southe west to west from
	 * bottom to top the method ignores the octants starting and ending cells since
	 * they have been applied in FOV::start
	 */
	void scanSW2W(FOVMap map, int xCenter, int yCenter, int distance, int maxRadius, double startSlope,
			double endSlope) {
		if (distance > maxRadius) {
			return;
		}

		// calculate start and end cell of the scan
		int yStart = (int) (yCenter + 0.5 - (startSlope * distance));
		int yEnd = (int) (yCenter + 0.5 - (endSlope * distance));
		int xCheck = xCenter - distance;

		// is starting cell the bottommost cell in the octant?
		// NO: call applyCell() to starting cell
		// YES: it has already been applied in FOV::start()
		if (yStart != yCenter - (-1 * distance)) {
			FOVutils.applyCell(map, xCheck, yStart);
		}

		// find out if starting cell blocks LOS
		boolean prevBlocked = this.scanCell(map, xCheck, yStart);

		// scan from the cell after the starting cell (yStart-1) to end cell of
		// scan (yCheck>=yEnd)
		for (int yCheck = yStart - 1; yCheck >= yEnd; yCheck--) {
			FOVutils.checkAndApplyY(map, yCenter, xCheck, yCheck);

			// cell blocks LOS
			// if previous cell didn't block LOS (prevBlocked==false) we have
			// hit a 'new' section of walls. a new scan will be started with an
			// endSlope that 'brushes' by the bottom of the blocking cell
			//
			// +---++---++---+ @ = [xCenter+0.5,yCenter+0.5]
			// | || || | a = old [xCheck,yCheck]
			// | || || @ | b = new [xCheck+0.99999,yCheck+1]
			// | || || |
			// +---++---++---+
			// a####+---++---+
			// #####| || |
			// #####| || |
			// #####| || |
			// #####+---++---+
			// +---b+---++---+
			// | || || |
			// | || || |
			// | || || |
			// +---++---++---+
			//
			if (this.scanCell(map, xCheck, yCheck)) {
				if (!prevBlocked) {
					this.scanSW2W(map, xCenter, yCenter, distance + 1, maxRadius, startSlope,
							FOVutils.invSlope(xCenter + 0.5, yCenter + 0.5, xCheck + 0.99999, (double) yCheck + 1));
				}
				prevBlocked = true;
			}

			// cell doesn't block LOS
			// if the cell is the first non-blocking cell after a section of walls
			// we need to calculate a new startSlope that 'brushes' by the top of
			// the blocking cells
			//
			// +---++---++---+ @ = [xCenter+0.5,yCenter+0.5]
			// | || || | a = old [xCheck,yCheck]
			// | || || @ | b = new [xCheck,yCheck+0.99999]
			// | || || |
			// +---++---++---+
			// a---++---++---+
			// | || || |
			// | || || |
			// | || || |
			// b---++---++---+
			// #####+---++---+
			// #####| || |
			// #####| || |
			// #####| || |
			// #####+---++---+
			//
			else {
				if (prevBlocked) {
					startSlope = FOVutils.invSlope(xCenter + 0.5, yCenter + 0.5, xCheck, yCheck + 0.99999);
				}
				prevBlocked = false;
			}
		}

		// if the last cell of the scan didn't block LOS a new scan should be
		// started
		if (!prevBlocked) {
			this.scanSW2W(map, xCenter, yCenter, distance + 1, maxRadius, startSlope, endSlope);
		}
	}

	/**
	 * scanSW2S scans the octant covering the area from south west to south from
	 * left to right the method ignores the octants starting and ending cells since
	 * they have been applied in FOV::start
	 */
	void scanSW2S(FOVMap map, int xCenter, int yCenter, int distance, int maxRadius, double startSlope,
			double endSlope) {
		if (distance > maxRadius) {
			return;
		}

		// calculate start and end cell of the scan
		int xStart = (int) (xCenter + 0.5 + (startSlope * distance));
		int xEnd = (int) (xCenter + 0.5 + (endSlope * distance));
		int yCheck = yCenter + distance;

		// is the starting cell the leftmost cell in the octant?
		// NO: call applyCell() to starting cell
		// YES: it has already been applied in FOV::start()
		if (xStart != xCenter + (-1 * distance)) {
			FOVutils.applyCell(map, xStart, yCheck);
		}

		// find out if starting cell blocks LOS
		boolean prevBlocked = this.scanCell(map, xStart, yCheck);

		// scan from the cell after the starting cell (xStart+1) to end cell of
		// scan (xCheck<=xEnd)
		for (int xCheck = xStart + 1; xCheck <= xEnd; xCheck++) {
			FOVutils.checkAndApplyX(map, xCenter, yCheck, xCheck);

			// cell blocks LOS
			// if previous cell didn't block LOS (prevBlocked==false) we have
			// hit a 'new' section of walls. a new scan will be started with an
			// endSlope that 'brushes' by to the left of the blocking cell
			//
			// +---++---++---+
			// | || || |
			// | || || @ |
			// | || || |
			// +---++---++---+
			// +---ba####+---+ @ = [xCenter+0.5,yCenter+0.5]
			// | |#####| | a = old [xCheck,yCheck]
			// | |#####| | b = new [xCheck-0.00001,yCheck]
			// | |#####| |
			// +---+#####+---+
			//
			if (this.scanCell(map, xCheck, yCheck)) {
				if (!prevBlocked) {
					this.scanSW2S(map, xCenter, yCenter, distance + 1, maxRadius, startSlope,
							FOVutils.slope(xCenter + 0.5, yCenter + 0.5, xCheck - 0.00001, yCheck));
				}
				prevBlocked = true;
			}

			// cell doesn't block LOS
			// if the cell is the first non-blocking cell after a section of walls
			// we need to calculate a new startSlope that 'brushes' by to the right
			// of the blocking cells
			//
			// +---++---++---+
			// | || || |
			// | || || @ |
			// | || || |
			// +---++---++---+
			// #####a---++---+ @ = [xCenter+0.5,yCenter+0.5]
			// #####| || | a = old [xCheck,yCheck]
			// #####| || | b = new [xCheck,yCheck+0.99999]
			// #####| || |
			// #####b---++---+
			//
			else {
				if (prevBlocked) {
					startSlope = FOVutils.slope(xCenter + 0.5, yCenter + 0.5, xCheck, yCheck + 0.99999);
				}
				prevBlocked = false;
			}
		}

		// if the last cell of the scan didn't block LOS a new scan should be
		// started
		if (!prevBlocked) {
			this.scanSW2S(map, xCenter, yCenter, distance + 1, maxRadius, startSlope, endSlope);
		}
	}

	/**
	 * scanSE2S scans the octant covering the area from south east to south from
	 * right to left the method ignores the octants starting and ending cells since
	 * they have been applied in FOV::start
	 */
	void scanSE2S(FOVMap map, int xCenter, int yCenter, int distance, int maxRadius, double startSlope,
			double endSlope) {
		if (distance > maxRadius) {
			return;
		}

		// calculate start and end cell of the scan
		int xStart = (int) (xCenter + 0.5 + (startSlope * distance));
		int xEnd = (int) (xCenter + 0.5 + (endSlope * distance));
		int yCheck = yCenter + distance;

		// is starting cell the rightmost cell in the octant?
		// NO: call applyCell() to starting cell
		// YES: it has already been applied in FOV::start()
		if (xStart != xCenter + (distance)) {
			FOVutils.applyCell(map, xStart, yCheck);
		}

		// find out if starting cell blocks LOS
		boolean prevBlocked = this.scanCell(map, xStart, yCheck);

		// scan from the cell after the starting cell (xStart-1) to end cell of
		// scan (xCheck>=xEnd)
		for (int xCheck = xStart - 1; xCheck >= xEnd; xCheck--) {
			FOVutils.checkAndApplyX(map, xCenter, yCheck, xCheck);

			// cell blocks LOS
			// if previous cell didn't block LOS (prevBlocked==false) we have
			// hit a 'new' section of walls. a new scan will be started with an
			// endSlope that 'brushes' by to the right of the blocking cell
			//
			// +---++---++---+
			// | || || |
			// | @ || || |
			// | || || |
			// +---++---++---+
			// +---+a####b---+ @ = [xCenter+0.5,yCenter+0.5]
			// | |#####| | a = old [xCheck,yCheck]
			// | |#####| | b = new [xCheck+1,yCheck]
			// | |#####| |
			// +---+#####+---+
			//
			if (this.scanCell(map, xCheck, yCheck)) {
				if (!prevBlocked) {
					this.scanSE2S(map, xCenter, yCenter, distance + 1, maxRadius, startSlope,
							FOVutils.slope(xCenter + 0.5, yCenter + 0.5, (double) xCheck + 1, yCheck));
				}
				prevBlocked = true;
			}

			// cell doesn't block LOS
			// if the cell is the first non-blocking cell after a section of walls
			// we need to calculate a new startSlope that 'brushes' by to the left
			// of the blocking cells
			//
			// +---++---++---+
			// | || || |
			// | @ || || |
			// | || || |
			// +---++---++---+
			// +---+a---+##### @ = [xCenter+0.5,yCenter+0.5]
			// | || |##### a = old [xCheck,yCheck]
			// | || |##### b = new [xCheck+0.99999,yCheck+0.99999]
			// | || |#####
			// +---++---b#####
			//
			else {
				if (prevBlocked) {
					startSlope = FOVutils.slope(xCenter + 0.5, yCenter + 0.5, xCheck + 0.99999, yCheck + 0.99999);
				}
				prevBlocked = false;
			}
		}

		// if the last cell of the scan didn't block LOS a new scan should be
		// started
		if (!prevBlocked) {
			this.scanSE2S(map, xCenter, yCenter, distance + 1, maxRadius, startSlope, endSlope);
		}
	}

	/**
	 * scanNE2E scans the octant covering the area from north east to east from top
	 * to bottom the method ignores the octants starting and ending cells since they
	 * have been applied in FOV::start
	 */
	void scanNE2E(FOVMap map, int xCenter, int yCenter, int distance, int maxRadius, double startSlope,
			double endSlope) {
		if (distance > maxRadius) {
			return;
		}

		// calculate start and end cell of the scan
		int yStart = (int) (yCenter + 0.5 + (startSlope * distance));
		int yEnd = (int) (yCenter + 0.5 + (endSlope * distance));
		int xCheck = xCenter + distance;

		// is starting cell the topmost cell in the octant?
		// NO: call applyCell() to starting cell
		// YES: it has already been applied in FOV::start()
		if (yStart != yCenter + (-1 * distance)) {
			FOVutils.applyCell(map, xCheck, yStart);
		}

		// find out if starting cell blocks LOS
		boolean prevBlocked = this.scanCell(map, xCheck, yStart);

		// scan from the cell after the starting cell (yStart+1) to end cell of
		// scan (yCheck<=yEnd)
		for (int yCheck = yStart + 1; yCheck <= yEnd; yCheck++) {
			FOVutils.checkAndApplyY(map, yCenter, xCheck, yCheck);

			// cell blocks LOS
			// if previous cell didn't block LOS (prevBlocked==false) we have
			// hit a 'new' section of walls. a new scan will be started with an
			// endSlope that 'brushes' by the top of the blocking cell (see fig.)
			//
			// +---++---++---+ @ = [xCenter+0.5,yCenter+0.5]
			// | || || | a = old [xCheck,yCheck]
			// | || || | b = new [xCheck,yCheck-0.00001]
			// | || || |
			// +---++---+b---+
			// +---++---+a####
			// | || |#####
			// | || |#####
			// | || |#####
			// +---++---+#####
			// +---++---++---+
			// | || || |
			// | @ || || |
			// | || || |
			// +---++---++---+
			//
			if (this.scanCell(map, xCheck, yCheck)) {
				if (!prevBlocked) {
					this.scanNE2E(map, xCenter, yCenter, distance + 1, maxRadius, startSlope,
							FOVutils.invSlope(xCenter + 0.5, yCenter + 0.5, xCheck, yCheck - 0.00001));
				}
				prevBlocked = true;
			}

			// cell doesn't block LOS
			// if the cell is the first non-blocking cell after a section of walls
			// we need to calculate a new startSlope that 'brushes' by the bottom
			// of the blocking cells
			//
			// +---++---+##### @ = [xCenter+0.5,yCenter+0.5]
			// | || |##### a = old [xCheck,yCheck]
			// | || |##### b = new [xCheck+0.99999,yCheck]
			// | || |#####
			// +---++---+#####
			// +---++---+a---b
			// | || || |
			// | || || |
			// | || || |
			// +---++---++---+
			// +---++---++---+
			// | || || |
			// | @ || || |
			// | || || |
			// +---++---++---+
			//
			else {
				if (prevBlocked) {
					startSlope = FOVutils.invSlope(xCenter + 0.5, yCenter + 0.5, xCheck + 0.99999, yCheck);
				}
				prevBlocked = false;
			}
		}

		// if the last cell of the scan didn't block LOS a new scan should be
		// started
		if (!prevBlocked) {
			this.scanNE2E(map, xCenter, yCenter, distance + 1, maxRadius, startSlope, endSlope);
		}
	}

	/**
	 * scanSE2E scans the octant covering the area from south east to east from
	 * bottom to top the method ignores the octants starting and ending cells since
	 * they have been applied in FOV::start
	 */
	void scanSE2E(FOVMap map, int xCenter, int yCenter, int distance, int maxRadius, double startSlope,
			double endSlope) {
		if (distance > maxRadius) {
			return;
		}

		// calculate start and end cell of the scan
		int yStart = (int) (yCenter + 0.5 + (startSlope * distance));
		int yEnd = (int) (yCenter + 0.5 + (endSlope * distance));
		int xCheck = xCenter + distance;

		// is starting cell the bottommost cell in the octant?
		// NO: call applyCell() to starting cell
		// YES: it has already been applied in FOV::start()
		if (yStart != yCenter + (distance)) {
			FOVutils.applyCell(map, xCheck, yStart);
		}

		// find out if starting cell blocks LOS
		boolean prevBlocked = this.scanCell(map, xCheck, yStart);

		// scan from the cell after the starting cell (yStart-1) to end cell of
		// scan (yCheck>=yEnd)
		for (int yCheck = yStart - 1; yCheck >= yEnd; yCheck--) {
			// is the current cell the topmost cell in the octant?
			// NO: call applyCell() to current cell
			// YES: it has already been applied in FOV::start()
			FOVutils.checkAndApplyY(map, yCenter, xCheck, yCheck);

			// cell blocks LOS
			// if previous cell didn't block LOS (prevBlocked==false) we have
			// hit a 'new' section of walls. a new scan will be started with an
			// endSlope that 'brushes' by the bottom of the blocking cell
			//
			// +---++---++---+ @ = [xCenter+0.5,yCenter+0.5]
			// | || || | a = old [xCheck,yCheck]
			// | @ || || | b = new [xCheck,yCheck+1]
			// | || || |
			// +---++---++---+
			// +---++---+a####
			// | || |#####
			// | || |#####
			// | || |#####
			// +---++---+#####
			// +---++---+b---+
			// | || || |
			// | || || |
			// | || || |
			// +---++---++---+
			//
			if (this.scanCell(map, xCheck, yCheck)) {
				if (!prevBlocked) {
					this.scanSE2E(map, xCenter, yCenter, distance + 1, maxRadius, startSlope,
							FOVutils.invSlope(xCenter + 0.5, yCenter + 0.5, xCheck, (double) yCheck + 1));
				}
				prevBlocked = true;
			}

			// cell doesn't block LOS
			// if the cell is the first non-blocking cell after a section of walls
			// we need to calculate a new startSlope that 'brushes' by the top of
			// the blocking cells
			//
			// +---++---++---+ @ = [xCenter+0.5,yCenter+0.5]
			// | || || | a = old [xCheck,yCheck]
			// | @ || || | b = new [xCheck+0.99999,yCheck+0.99999]
			// | || || |
			// +---++---++---+
			// +---++---+a---+
			// | || || |
			// | || || |
			// | || || |
			// +---++---++---b
			// +---++---+#####
			// | || |#####
			// | || |#####
			// | || |#####
			// +---++---+#####
			//
			else {
				if (prevBlocked) {
					startSlope = FOVutils.invSlope(xCenter + 0.5, yCenter + 0.5, xCheck + 0.99999, yCheck + 0.99999);
				}
				prevBlocked = false;
			}
		}

		// if the last cell of the scan didn't block LOS a new scan should be
		// started
		if (!prevBlocked) {
			this.scanSE2E(map, xCenter, yCenter, distance + 1, maxRadius, startSlope, endSlope);
		}
	}

	private int maxRadiusX;
	private int startX;
	private int startY;
	private boolean circle;

	public void startCircle(FOVMap map, int x, int y, int maxRadius) {
		circle = true;
		this.maxRadiusX = maxRadius;
		startX = x;
		startY = y;
		start(map, x, y, maxRadius);
	}

	public void start(FOVMap map, int x, int y, int maxRadius) {
		if (map == null) {
			return;
		}
		// apply starting cell
		FOVutils.applyCell(map, x, y);

		if (maxRadius > 0) {
			int nL = scanAndApplyNorth(map, x, y, maxRadius);
			int neL = scanAndApplyNorthEast(map, x, y, maxRadius);
			int eL = scanAndApplyEast(map, x, y, maxRadius);
			int seL = scanAndApplySouthEast(map, x, y, maxRadius);
			int sL = scanAndApplySouth(map, x, y, maxRadius);
			int swL = scanAndApplySouthWest(map, x, y, maxRadius);
			int wL = scanAndApplyWest(map, x, y, maxRadius);
			int nwL = scanAndApplyNorthWest(map, x, y, maxRadius);

			// scan the octant covering the area from north west to north
			// if it isn't blocked
			if (nL != 1 || nwL != 1) {
				this.scanNW2N(map, x, y, 1, maxRadius, 1, 0);
			}

			// scan the octant covering the area from north east to north
			// if it isn't blocked
			if (nL != 1 || neL != 1) {
				this.scanNE2N(map, x, y, 1, maxRadius, -1, 0);
			}

			// scan the octant covering the area from north west to west
			// if it isn't blocked
			if (nwL != 1 || wL != 1) {
				this.scanNW2W(map, x, y, 1, maxRadius, 1, 0);
			}

			// scan the octant covering the area from south west to west
			// if it isn't blocked
			if (swL != 1 || wL != 1) {
				this.scanSW2W(map, x, y, 1, maxRadius, -1, 0);
			}

			// scan the octant covering the area from south west to south
			// if it isn't blocked
			if (swL != 1 || sL != 1) {
				this.scanSW2S(map, x, y, 1, maxRadius, -1, 0);
			}

			// scan the octant covering the area from south east to south
			// if it isn't blocked
			if (seL != 1 || sL != 1) {
				this.scanSE2S(map, x, y, 1, maxRadius, 1, 0);
			}

			// scan the octant covering the area from north east to east
			// if it isn't blocked
			if (neL != 1 || eL != 1) {
				this.scanNE2E(map, x, y, 1, maxRadius, -1, 0);
			}

			// scan the octant covering the area from south east to east
			// if it isn't blocked
			if (seL != 1 || eL != 1) {
				this.scanSE2E(map, x, y, 1, maxRadius, 1, 0);
			}

		}
	}

	/**
	 * Scan and apply until a blocking cell is hit or until maxRadius is reached
	 */
	private int scanAndApplyNorthWest(FOVMap map, int x, int y, int maxRadius) {
		int counter;
		for (counter = 1; counter <= maxRadius; counter++) {
			FOVutils.applyCell(map, x - counter, y - counter);
			if (this.scanCell(map, x - counter, y - counter)) {
				break;
			}
		}
		return counter;
	}

	/**
	 * Scan and apply until a blocking cell is hit or until maxRadius is reached
	 */
	private int scanAndApplyWest(FOVMap map, int x, int y, int maxRadius) {
		int counter;
		for (counter = 1; counter <= maxRadius; counter++) {
			FOVutils.applyCell(map, x - counter, y);
			if (this.scanCell(map, x - counter, y)) {
				break;
			}
		}
		return counter;
	}

	/**
	 * Scan and apply until a blocking cell is hit or until maxRadius is reached
	 */
	private int scanAndApplySouthWest(FOVMap map, int x, int y, int maxRadius) {
		int counter;
		for (counter = 1; counter <= maxRadius; counter++) {
			FOVutils.applyCell(map, x - counter, y + counter);
			if (this.scanCell(map, x - counter, y + counter)) {
				break;
			}
		}
		return counter;
	}

	/**
	 * Scan and apply until a blocking cell is hit or until maxRadius is reached
	 */
	private int scanAndApplySouth(FOVMap map, int x, int y, int maxRadius) {
		int counter;
		for (counter = 1; counter <= maxRadius; counter++) {
			FOVutils.applyCell(map, x, y + counter);
			if (this.scanCell(map, x, y + counter)) {
				break;
			}
		}
		return counter;
	}

	/**
	 * Scan and apply until a blocking cell is hit or until maxRadius is reached
	 */
	private int scanAndApplySouthEast(FOVMap map, int x, int y, int maxRadius) {
		int counter;
		for (counter = 1; counter <= maxRadius; counter++) {
			FOVutils.applyCell(map, x + counter, y + counter);
			if (this.scanCell(map, x + counter, y + counter)) {
				break;
			}
		}
		return counter;
	}

	/**
	 * Scan and apply until a blocking cell is hit or until maxRadius is reached
	 */
	private int scanAndApplyEast(FOVMap map, int x, int y, int maxRadius) {
		int counter;
		for (counter = 1; counter <= maxRadius; counter++) {
			FOVutils.applyCell(map, x + counter, y);
			if (this.scanCell(map, x + counter, y)) {
				break;
			}
		}
		return counter;
	}

	/**
	 * Scan and apply until a blocking cell is hit or until maxRadius is reached
	 */
	private int scanAndApplyNorthEast(FOVMap map, int x, int y, int maxRadius) {
		int counter;
		for (counter = 1; counter <= maxRadius; counter++) {
			FOVutils.applyCell(map, x + counter, y - counter);
			if (this.scanCell(map, x + counter, y - counter)) {
				break;
			}
		}
		return counter;
	}

	/**
	 * Scan and apply until a blocking cell is hit or until maxRadius is reached
	 */
	private int scanAndApplyNorth(FOVMap map, int x, int y, int maxRadius) {
		int counter;
		for (counter = 1; counter <= maxRadius; counter++) {
			FOVutils.applyCell(map, x, y - counter);
			if (this.scanCell(map, x, y - counter)) {
				break;
			}
		}
		return counter;
	}

	boolean scanCell(FOVMap map, int x, int y) {
		if (!circle)
			return map.blockLOS(x, y);
		else {
			if (Position.flatDistance(x, y, startX, startY) >= maxRadiusX)
				return true;
			else
				return map.blockLOS(x, y);

		}
	}

}

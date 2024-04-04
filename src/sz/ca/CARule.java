package sz.ca;

import sz.util.Debug;

/**
 * Stores a rule of the form: if {baseCell} has {condType} {cellQuant}
 * {cellParam} around, turn into {destinationCell}
 * <p>
 * Example: when 1 has more than 4 2 around turn into 0
 */
public class CARule {

	private int baseCell;
	private int condType;
	private int cellQuant;
	private int cellParam;
	private int destinationCell;

	public static final int HAS = 0;
	public static final int MORE_THAN = 1;
	public static final int LESS_THAN = 2;

	public CARule(int baseCell, int condType, int cellQuant, int cellParam, int destinationCell) {
		validateType(condType, cellQuant);
		this.baseCell = baseCell;
		Debug.doAssert(condType >= 0 && condType <= 2, " Valid conditions on rule set");
		this.condType = condType;
		this.cellQuant = cellQuant;
		this.cellParam = cellParam;
		this.destinationCell = destinationCell;
		Debug.exitMethod();
	}

	public void apply(int x, int y, Matrix m, boolean wrap) {
		if (m.get(x, y) == baseCell) {
			int surroundingCount = 0;
			if (wrap)
				surroundingCount = m.getSurroundingCount(x, y, cellParam);
			else
				surroundingCount = m.getSurroundingCountNoWrap(x, y, cellParam);
			switch (condType) {
			case HAS:
				if (surroundingCount == cellQuant) {
					m.setFuture(destinationCell, x, y);
				}
				break;
			case MORE_THAN:
				if (surroundingCount > cellQuant) {
					m.setFuture(destinationCell, x, y);
				}
				break;
			case LESS_THAN:
				if (surroundingCount < cellQuant) {
					m.setFuture(destinationCell, x, y);
				}
				break;
			default:
				Debug.doAssert(false, "Condition Type");
			}
		}
	}

	private void validateType(int condType, int cellQuant) {
		Debug.doAssert(cellQuant >= 0 && cellQuant <= 8, "Invalid cell quantity parameter on rule: " + cellQuant);
		Debug.doAssert(condType >= 0 && condType <= 2, "Invalid condition type : " + condType);
	}

	public String toString() {
		String comparation = " INVALIDCOMPARATION";
		switch (condType) {
		case 0:
			comparation = " has ";
			break;
		case 1:
			comparation = " there are more than ";
			break;
		case 2:
			comparation = " there are less than ";
			break;
		default:
			break;
		}
		return "When the cell status is " + baseCell + " and " + comparation + cellQuant + " cells of status "
				+ cellParam + " around, turn it into " + destinationCell;
	}

}
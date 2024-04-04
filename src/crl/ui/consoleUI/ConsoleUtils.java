package crl.ui.consoleUI;

import java.util.List;

import crl.feature.Feature;
import crl.item.Item;
import crl.level.Cell;
import sz.csi.ConsoleSystemInterface;
import sz.csi.textcomponents.MenuItem;

public class ConsoleUtils {

	static int findCurrentCellColor(Cell currentCell, Feature currentFeature, boolean visible, String exitOn) {
		if (currentCell == null) {
			return ConsoleSystemInterface.BLACK;
		}
		if (exitOn != null) {
			return ConsoleSystemInterface.RED;
		}
		if (currentCell.isSolid() || currentFeature != null && currentFeature.isSolid()) {
			return ConsoleSystemInterface.BROWN;
		}
		if (visible) {
			return ConsoleSystemInterface.LIGHT_GRAY;
		}
		return ConsoleSystemInterface.GRAY;
	}

	static int determineCellColorByBloodLevel(String bloodLevel, int cellColor, boolean notDay) {
		switch (Integer.parseInt(bloodLevel)) {
		case 0:
			if (notDay) {
				cellColor = ConsoleSystemInterface.DARK_RED;
			} else {
				cellColor = ConsoleSystemInterface.RED;
			}
			break;
		case 1:
			if (notDay) {
				cellColor = ConsoleSystemInterface.PURPLE;
			} else {
				cellColor = ConsoleSystemInterface.DARK_RED;
			}
			break;
		case 8:
			cellColor = ConsoleSystemInterface.LEMON;
			break;
		}
		return cellColor;
	}

	static int determineCellColorByWater(boolean canFloatUpward) {
		if (canFloatUpward) {
			return ConsoleSystemInterface.BLUE;
		} else {
			return ConsoleSystemInterface.DARK_BLUE;
		}
	}

	static String determineLooked(Cell choosen, Feature feat, List<MenuItem> items, String bloodAt) {
		String looked = "";
		if (choosen != null)
			looked += choosen.getDescription();
		if (bloodAt != null)
			looked += "{bloody}";
		if (feat != null)
			looked += ", " + feat.getDescription();
		Item item = determineItem(items);
		if (item != null)
			if (items.size() == 1)
				looked += ", " + item.getDescription();
			else
				looked += ", " + item.getDescription() + " and some items";
		return looked;
	}

	private static Item determineItem(List<MenuItem> items) {
		Item item = null;
		if (items != null) {
			item = (Item) items.get(0);
		}
		return item;
	}

}

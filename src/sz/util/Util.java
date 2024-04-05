package sz.util;

import sz.csi.textcomponents.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import crl.player.Player;

public class Util {
	private static Random rand = new Random(System.currentTimeMillis());

	public static int rand(int low, int hi) {
		return (int) (rand.nextDouble() * (hi - low + 1) + low);
	}

	public static int greater(int a, int b) {
		return Math.max(a, b);
	}

	public static int abs(int a) {
		if (a > 0)
			return a;
		return -a;
	}

	public static boolean chance(int p) {
		return rand(1, 100) <= p;
	}

	public static List<MenuItem> page(List<MenuItem> source, int elementsOnPage, int pageNumber) {
		if (source.isEmpty())
			return source;
		if ((pageNumber + 1) * elementsOnPage > source.size())
			return new ArrayList<>(source.subList(pageNumber * elementsOnPage, source.size()));
		else
			return new ArrayList<>(source.subList(pageNumber * elementsOnPage, (pageNumber + 1) * elementsOnPage));
	}

	public static String randomElementOf(String[] array) {
		return array[rand(0, array.length - 1)];
	}

	public static Object randomElementOf(List<?> collection) {
		return collection.get(rand(0, collection.size() - 1));
	}

	public static Object randomElementOf(Object[] array) {
		return array[rand(0, array.length - 1)];
	}

	public static int sign(int n) {
		return Integer.compare(n, 0);
	}

	public static String[] getOtherPrize(int firstChance, int secondChance, boolean withCoin) {
		if (chance(firstChance))
			if (chance(secondChance))
				if (chance(10))
					if (chance(10))
						if (chance(10))
							return new String[] { "WHITE_MONEY_BAG" };
						else
							return new String[] { "POT_ROAST" };
					else
						return new String[] { "INVISIBILITY_POTION", "ROSARY", "BLUE_MONEY_BAG" };
				else
					return new String[] { "RED_MONEY_BAG" };
			else
				return new String[] { "BIGHEART" };
		else if (withCoin) {
			return new String[] { "SMALLHEART", "COIN" };
		}
		return new String[] { "SMALLHEART" };
	}

	public static String[] getMysticWeapon(Player p, int chance) {
		if (p.getFlag("MYSTIC_CRYSTAL") && chance(chance))
			return new String[] { "CRYSTALWP" };
		else if (p.getFlag("MYSTIC_FIST") && chance(chance))
			return new String[] { "FISTWP" };
		else if (p.getFlag("MYSTIC_CROSS") && chance(chance))
			return new String[] { "CROSSWP" };
		else if (p.getFlag("MYSTIC_STOPWATCH") && chance(chance))
			return new String[] { "STOPWATCHWP" };
		else if (p.getFlag("MYSTIC_HOLY_WATER") && chance(chance))
			return new String[] { "HOLYWP" };
		else if (p.getFlag("MYSTIC_HOLY_BIBLE") && chance(chance))
			return new String[] { "BIBLEWP" };
		else
			return new String[] { "AXEWP", "DAGGERWP" };
	}
}
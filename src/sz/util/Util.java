package sz.util;

import sz.csi.textcomponents.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Util {
	private static Random rand = new Random(System.currentTimeMillis());

	public static int rand(int low, int hi) {
        return (int) ((rand.nextDouble() * (hi - low + 1)) + low);
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
		return Util.rand(1, 100) <= p;
	}

	public static List<MenuItem> page(List<MenuItem> source, int elementsOnPage, int pageNumber) {
		// System.out.println("elements on page"+elementsOnPage+" page
		// Number"+pageNumber);
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
}
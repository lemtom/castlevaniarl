package sz.util;

public class Debug {

	static int methodLevel;

	static final boolean debug = false;
	static final boolean timing = false;
	static final boolean gossip = false;
	static long firstTimer;
	static long lastTimer;

	public static void startTimer() {
		if (timing)
			firstTimer = System.currentTimeMillis();
	}

	public static void stopTimer(String desc) {
		if (timing) {
			lastTimer = System.currentTimeMillis();
			System.out.println("Timing for " + desc + ": " + (lastTimer - firstTimer));
		}
	}

	public static void enterStaticMethod(String classs, String method) {
		if (!debug)
			return;
		methodLevel++;
		System.out.println(spaces(methodLevel) + "-" + classs + "." + method + "()");
	}

	public static void enterMethod(Object cls, String method, Object params) {
		if (!debug)
			return;
		methodLevel++;
		System.out.println(spaces(methodLevel) + "-" + cls + "." + method + "(" + params + ")");
	}

	public static void enterMethod(Object cls, String method) {
		if (!debug)
			return;
		methodLevel++;
		System.out.println(spaces(methodLevel) + "-" + cls + "." + method + "()");
	}

	public static void doAssert(boolean expression, String msg) {
		if (!expression) {
			System.out.println("Programming Assertion Failed:" + msg);
			System.exit(0);
		}
	}

	public static void exitExceptionally(Throwable why) {
		if (!debug)
			return;
		System.out.println(spaces(methodLevel) + "throws " + why);
		methodLevel--;
	}

	public static void exitMethod() {
		if (!debug)
			return;
		System.out.println(spaces(methodLevel) + "-done.");
		methodLevel--;
	}

	public static void exitMethod(Object returns) {
		if (!debug)
			return;
		System.out.println(spaces(methodLevel) + "-done, returns " + returns);
		methodLevel--;
	}

	public static void exitMethod(int returns) {
		if (!debug)
			return;
		System.out.println(spaces(methodLevel) + "-done, returns " + returns);
		methodLevel--;
	}

	public static void say(String s) {
		if (!gossip)
			return;
		System.out.println(spaces(methodLevel + 1) + s);
	}

	public static void say(Object o) {
		if (!gossip)
			return;
		if (o == null) {
			System.out.println(spaces(methodLevel + 1) + "NULL");
		} else
			System.out.println(spaces(methodLevel + 1) + o);
	}

	public static void say(String s, int level) {
		if (!gossip)
			return;
		if (level < 4)
			System.out.println(s);
	}

	private static String spaces(int n) {

		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < n; i++) {
			ret.append("   ");
		}
		return ret.toString();
	}

	public static void byebye(String msg) {
		System.out.println(msg);
		System.exit(0);
	}
}
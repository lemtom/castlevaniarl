package sz.csi;

import java.util.*;

import sz.util.Debug;

public class CharKey {
	public static final int UARROW = 0;
	public static final int DARROW = 1;
	public static final int LARROW = 2;
	public static final int RARROW = 3;
	public static final int LCTRL = 4;
	public static final int RCTRL = 5;
	public static final int LALT = 6;
	public static final int RALT = 7;
	public static final int LSHIFT = 8;
	public static final int RSHIFT = 9;
	public static final int ENTER = 10;
	public static final int BACKSPACE = 11;
	public static final int F1 = 12;
	public static final int F2 = 13;
	public static final int F3 = 14;
	public static final int F4 = 15;
	public static final int F5 = 16;
	public static final int F6 = 17;
	public static final int F7 = 18;
	public static final int F8 = 19;
	public static final int F9 = 20;
	public static final int F10 = 21;
	public static final int F11 = 22;
	public static final int F12 = 23;
	public static final int INSERT = 24;
	public static final int HOME = 25;
	public static final int PAGEUP = 26;
	public static final int PAGEDOWN = 27;
	public static final int DELETE = 28;
	public static final int END = 29;
	public static final int ESC = 30;
	public static final int TAB = 31;
	public static final int OPENSHARPBRACETS = 32;
	public static final int CLOSESHARPBRACETS = 33;
	public static final int SEMICOLON = 34;
	public static final int APOSTROPHE = 35;
	public static final int COMMA = 36;
	public static final int DOT = 37;
	public static final int SLASH = 38;
	public static final int BACKSLASH = 39;
	public static final int SPACE = 40;
	public static final int MINUS = 41;
	public static final int EQUALS = 42;
	public static final int BACKAPOSTROPHE = 43;
	public static final int CURLYMINUS = 44;
	public static final int EXCLAMATION = 45;
	public static final int ARROBE = 46;
	public static final int MONEY = 47;
	public static final int PERCENTAGE = 48;
	public static final int EXPONENCIATION = 49;
	public static final int AMPERSAND = 50;
	public static final int ASTERISK = 51;
	public static final int OPENPARENTHESIS = 52;
	public static final int CLOSEPARENTHESIS = 53;
	public static final int UNDERLINE = 54;
	public static final int PLUS = 55;
	public static final int OPENCURLYBRACETS = 56;
	public static final int CLOSECURLYBRACETS = 57;
	public static final int COLON = 58;
	public static final int COMILLAS = 59;
	public static final int LESSTHAN = 60;
	public static final int MORETHAN = 61;
	public static final int QUESTION = 62;
	public static final int OR = 63;
	public static final int a = 64;
	public static final int b = 65;
	public static final int c = 66;
	public static final int d = 67;
	public static final int e = 68;
	public static final int f = 69;
	public static final int g = 70;
	public static final int h = 71;
	public static final int i = 72;
	public static final int j = 73;
	public static final int k = 74;
	public static final int l = 75;
	public static final int m = 76;
	public static final int n = 77;
	public static final int o = 78;
	public static final int p = 79;
	public static final int q = 80;
	public static final int r = 81;
	public static final int s = 82;
	public static final int t = 83;
	public static final int u = 84;
	public static final int v = 85;
	public static final int w = 86;
	public static final int x = 87;
	public static final int y = 88;
	public static final int z = 89;
	public static final int A = 90;
	public static final int B = 91;
	public static final int C = 92;
	public static final int D = 93;
	public static final int E = 94;
	public static final int F = 95;
	public static final int G = 96;
	public static final int H = 97;
	public static final int I = 98;
	public static final int J = 99;
	public static final int K = 100;
	public static final int L = 101;
	public static final int M = 102;
	public static final int N = 103;
	public static final int O = 104;
	public static final int P = 105;
	public static final int Q = 106;
	public static final int R = 107;
	public static final int S = 108;
	public static final int T = 109;
	public static final int U = 110;
	public static final int V = 111;
	public static final int W = 112;
	public static final int X = 113;
	public static final int Y = 114;
	public static final int Z = 115;
	public static final int NONE = 116;
	public static final int N0 = 117;
	public static final int N1 = 118;
	public static final int N2 = 119;
	public static final int N3 = 120;
	public static final int N4 = 121;
	public static final int N5 = 122;
	public static final int N6 = 123;
	public static final int N7 = 124;
	public static final int N8 = 125;
	public static final int N9 = 126;
	public static final int CTRL = 127;

	public int code;
	public static final Map<String, String> mirrors = new Hashtable<String, String>(20);

	static {
		mirrors.put("" + OPENSHARPBRACETS, "[");
		mirrors.put("" + CLOSESHARPBRACETS, "]");
		mirrors.put("" + SEMICOLON, ";");
		mirrors.put("" + APOSTROPHE, "'");
		mirrors.put("" + COMMA, ",");
		mirrors.put("" + DOT, ".");
		mirrors.put("" + SLASH, "/");
		mirrors.put("" + BACKSLASH, "\\");
		mirrors.put("" + SPACE, " ");
		mirrors.put("" + MINUS, "-");
		mirrors.put("" + EQUALS, "=");
		mirrors.put("" + BACKAPOSTROPHE, "`");
		mirrors.put("" + CURLYMINUS, "~");
		mirrors.put("" + EXCLAMATION, "!");
		mirrors.put("" + ARROBE, "@");
		mirrors.put("" + MONEY, "$");
		mirrors.put("" + PERCENTAGE, "%");
		mirrors.put("" + EXPONENCIATION, "^");
		mirrors.put("" + AMPERSAND, "&");
		mirrors.put("" + ASTERISK, "*");
		mirrors.put("" + OPENPARENTHESIS, "(");
		mirrors.put("" + CLOSEPARENTHESIS, ")");
		mirrors.put("" + UNDERLINE, "_");
		mirrors.put("" + PLUS, "+");
		mirrors.put("" + OPENCURLYBRACETS, "{");
		mirrors.put("" + CLOSECURLYBRACETS, "}");
		mirrors.put("" + COLON, ":");
		mirrors.put("" + COMILLAS, "\"");
		mirrors.put("" + LESSTHAN, "<");
		mirrors.put("" + MORETHAN, ">");
		mirrors.put("" + QUESTION, "?");
		mirrors.put("" + OR, "|");
		mirrors.put("" + CTRL, "Ctrl");

	}

	public CharKey(int code) {
		this.code = code;
	}

	public CharKey() {

	}

	public static String getString(String code) {
		return getString(Integer.parseInt(code));
	}

	public static String getString(int code) {
		if (code >= 90 && code <= 115)
			return "" + ((char) (code - 25));
		if (code >= 64 && code <= 89)
			return "" + ((char) (code + 33));
		if (code >= 117 && code <= 126)
			return "" + ((char) (code - 69));
		String ret = mirrors.get("" + code);
		if (ret != null)
			return ret;
		else
			return "(" + code + ")";
	}

	public String toString() {
		return getString(code);
	}

	public boolean isArrow() {
		Debug.enterMethod(this, "isArrow");
		boolean ret = (isRightArrow() || isUpArrow() || isLeftArrow() || isDownArrow() || isDownLeftArrow()
				|| isDownRightArrow() || isUpLeftArrow() || isUpRightArrow() || isSelfArrow());
		Debug.exitMethod(ret + "");
		return ret;
	}

	public boolean isDownArrow() {
		return code == CharKey.DARROW || code == CharKey.N2;
	}

	public boolean isDownLeftArrow() {
		return code == CharKey.N1;
	}

	public boolean isDownRightArrow() {
		return code == CharKey.N3;
	}

	public boolean isLeftArrow() {
		return code == CharKey.LARROW || code == CharKey.N4;
	}

	public boolean isRightArrow() {
		return code == CharKey.RARROW || code == CharKey.N6;
	}

	public boolean isUpArrow() {
		return code == CharKey.UARROW || code == CharKey.N8;
	}

	public boolean isUpLeftArrow() {
		return code == CharKey.N7;
	}

	public boolean isUpRightArrow() {
		return code == CharKey.N9;
	}

	public boolean isSelfArrow() {
		return code == CharKey.N5;
	}

	public boolean isMetaKey() {
		return code == CTRL || code == RALT || code == RCTRL || code == RSHIFT || code == LALT || code == LCTRL
				|| code == LSHIFT;
	}

	public boolean isAlphaNumeric() {
		if (code >= 90 && code <= 115)
			return true;
		if (code >= 64 && code <= 89)
			return true;
		if (code >= 117 && code <= 126)
			return true;
		return false;
	}
}
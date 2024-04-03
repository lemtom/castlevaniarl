package sz.csi.textcomponents;

import sz.csi.ConsoleSystemInterface;

public class TextBox extends TextComponent {
	private static final long serialVersionUID = 1L;
	private StringBuilder[] lines;
	private String title = "";

	public TextBox(ConsoleSystemInterface si) {
		super(si);
		lines = new StringBuilder[] { new StringBuilder() };
	}

	@Override
	public void setHeight(int value) {
		super.setHeight(value);
		if (hasBorder())
			value -= 2;

		lines = new StringBuilder[value];

		for (int i = 0; i < value; i++) {
			lines[i] = new StringBuilder();
		}
	}

	@Override
	public void setBorder(boolean value) {
		super.setBorder(value);
		if (hasBorder()) {
			lines = new StringBuilder[getHeight() - 2];
		} else {
			lines = new StringBuilder[getHeight()];
		}

		for (int i = 0; i < lines.length; i++) {
			lines[i] = new StringBuilder();
		}
	}

	public void draw() {
		if (height == 0)
			return;
		clearBox();
		if (hasBorder()) {
			drawBorder();
			si.print(position.x + 2, position.y, title);
		}
		for (int i = 0; i < lines.length; i++) {
			si.print(inPosition.x, inPosition.y + i, lines[i].toString(), foreColor);
		}
	}

	public void setText(String text) {
		clear();
		String[] tokens = text.split(" ");
		int curx = 0;
		int cury = 0;
		for (String token : tokens) {
			int distance = inWidth - curx;
			if (distance < token.length() + 1) {
				if (cury < inHeight - 1) {
					curx = 0;
					cury++;
				} else {
					break;
				}
			}
			if (token.equals("\n")) {
				curx = 0;
				cury++;
			} else {
				lines[cury].append(token).append(" ");
				curx += token.length() + 1;
			}
		}
	}

	public void setTitle(String pTitle) {
		title = pTitle;
	}

	public void clear() {
		for (int i = 0; i < lines.length; i++)
			lines[i] = new StringBuilder();
	}

}
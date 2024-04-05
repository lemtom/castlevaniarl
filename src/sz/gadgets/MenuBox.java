package sz.gadgets;

import crl.conf.gfx.data.GFXConfiguration;
import crl.ui.graphicsUI.GFXDisplay;
import crl.ui.graphicsUI.SwingSystemInterface;
import sz.csi.CharKey;
import sz.csi.textcomponents.MenuItem;
import sz.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MenuBox {

	private ArrayList<MenuItem> items;
	private String title = "";

	// State Attributes
	private int currentPage;
	private int pages;

	// Components
	private int xpos, ypos, width, itemsPerPage;
	private SwingSystemInterface si;
	private BufferedImage box;
	private GFXConfiguration configuration;

	public MenuBox(SwingSystemInterface g, GFXConfiguration configuration, BufferedImage box) {
		this.si = g;
		this.configuration = configuration;
		this.box = box;
	}

	public void setPosition(int x, int y) {
		xpos = x;
		ypos = y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setItemsPerPage(int ipp) {
		itemsPerPage = ipp;
	}

	public void setMenuItems(ArrayList<MenuItem> items) {
		this.items = items;
	}

	private int gap = 24;

	public void setGap(int val) {
		gap = val;
	}

	public void draw() {
		int realW = width * 10 + 20;
		int realH = (itemsPerPage + 1) * gap + 20;
		int realPosX = xpos * 10 - 20;
		int realPosY = ypos * 24 - 30;
		Color COLOR_WINDOW_BACKGROUND = this.configuration.getWindowBackgroundColour();
		Color COLOR_BORDER_IN = this.configuration.getBorderColourIn();
		Color COLOR_BORDER_OUT = this.configuration.getBorderColourOut();

		si.getGraphics2D().setColor(COLOR_WINDOW_BACKGROUND);
		si.getGraphics2D().fillRect(realPosX + 6, realPosY + 6, realW - 14, realH - 14);
		si.getGraphics2D().setColor(COLOR_BORDER_OUT);
		si.getGraphics2D().drawRect(realPosX + 6, realPosY + 6, realW - 14, realH - 14);
		si.getGraphics2D().setColor(COLOR_BORDER_IN);
		si.getGraphics2D().drawRect(realPosX + 8, realPosY + 8, realW - 18, realH - 18);

		pages = (int) ((double) ((items.size() - 1) / (itemsPerPage)) + 1);

		si.print(xpos, ypos, title, GFXDisplay.COLOR_BOLD);
		List<MenuItem> shownItems = Util.page(items, itemsPerPage, currentPage);

		if (ordinal) {
			xpos -= 2;
		}

		int i = 0;
		for (; i < shownItems.size(); i++) {
			GFXMenuItem item = (GFXMenuItem) shownItems.get(i);
			if (!ordinal) {
				si.printAtPixel(xpos * 10, (ypos + 1) * 24 + i * gap, ((char) (97 + i)) + ".", GFXDisplay.COLOR_BOLD);
			}
			if (box != null) {
				si.drawImage((xpos + 2) * 10 + 1, ypos * 24 + i * gap + (int) (gap * 0.3D) - 4, box);
			}
			if (item.getMenuImage() != null)
				si.drawImage((xpos + 2) * 10 + 5, ypos * 24 + i * gap + (int) (gap * 0.3D), item.getMenuImage());
			String description = item.getMenuDescription();
			if (description.length() > width - 2) {
				description = description.substring(0, width - 4);
			}
			String detail = item.getMenuDetail();
			if (detail != null && detail.length() > width - 2) {
				detail = detail.substring(0, width - 4);
			}
			si.printAtPixel((xpos + 6) * 10, (ypos + 1) * 24 + i * gap, description, Color.WHITE);
			if (detail != null && !detail.isEmpty()) {
				si.printAtPixel((xpos + 6) * 10, (ypos + 1) * 24 + i * gap + 18, detail, Color.WHITE);
			}
		}
		ordinal = false;

		si.refresh();
	}

	public void setBounds(int x, int y, int width, int height) {
		this.xpos = x;
		this.ypos = y;
		this.width = width;
		this.itemsPerPage = height;
	}

	public Object getSelection() {
		int pageElements = itemsPerPage;
		while (true) {

			draw();
			List<MenuItem> shownItems = Util.page(items, pageElements, currentPage);
			CharKey key = new CharKey(CharKey.NONE);
			while (key.code != CharKey.SPACE && key.code != CharKey.UARROW && key.code != CharKey.DARROW
					&& key.code != CharKey.N8 && key.code != CharKey.N2
					&& (key.code < CharKey.A || key.code > CharKey.A + pageElements - 1)
					&& (key.code < CharKey.a || key.code > CharKey.a + pageElements - 1))
				key = si.inkey();
			if (key.code == CharKey.SPACE)
				return null;
			if (key.code == CharKey.UARROW || key.code == CharKey.N8 && currentPage > 0)
				currentPage--;
			if (key.code == CharKey.DARROW || key.code == CharKey.N2 && currentPage < pages - 1)
				currentPage++;

			if (key.code >= CharKey.A && key.code <= CharKey.A + shownItems.size() - 1)
				return shownItems.get(key.code - CharKey.A);
			else if (key.code >= CharKey.a && key.code <= CharKey.a + shownItems.size() - 1)
				return shownItems.get(key.code - CharKey.a);
			si.restore();

		}
	}

	public Object getUnpagedSelection() {
		int pageElements = itemsPerPage;
		draw();
		List<MenuItem> shownItems = Util.page(items, pageElements, currentPage);
		CharKey key = new CharKey(CharKey.NONE);
		while (key.code != CharKey.SPACE && (key.code < CharKey.A || key.code > CharKey.A + pageElements - 1)
				&& (key.code < CharKey.a || key.code > CharKey.a + pageElements - 1))
			key = si.inkey();
		if (key.code == CharKey.SPACE)
			return null;
		if (key.code >= CharKey.A && key.code <= CharKey.A + shownItems.size() - 1)
			return shownItems.get(key.code - CharKey.A);
		else if (key.code >= CharKey.a && key.code <= CharKey.a + shownItems.size() - 1)
			return shownItems.get(key.code - CharKey.a);
		return null;

	}

	boolean ordinal = false;

	public Object getUnpagedOrdinalSelectionAKS(int[] keys) throws AdditionalKeysSignal {
		ordinal = true;
		draw();
		CharKey key = new CharKey(CharKey.NONE);
		while (key.code != CharKey.SPACE && !isOneOf(key.code, keys))
			key = si.inkey();
		if (key.code == CharKey.SPACE)
			return null;
		if (isOneOf(key.code, keys))
			throw new AdditionalKeysSignal(key.code);
		return null;

	}

	protected boolean isOneOf(int value, int[] values) {
		for (int j : values) {
			if (value == j)
				return true;
		}
		return false;
	}

	public void setTitle(String s) {
		title = s;
	}
}
package crl.ui.graphicsUI;

import javax.swing.*;
import java.awt.*;

public class AddornedBorderPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Image UPRIGHT;
	private Image UPLEFT;
	private Image DOWNRIGHT;
	private Image DOWNLEFT;
	private Color OUT_COLOR;
	private Color IN_COLOR;
	private int borderWidth;
	private int borderHeight;
	private static final Color TRANSPARENT_BLUE = new Color(20, 20, 20, 200);

	public AddornedBorderPanel(Image upRight, Image upLeft, Image downRight, Image downLeft, Color outColor,
			Color inColor, int borderWidth, int borderHeight) {
		this.UPRIGHT = upRight;
		this.UPLEFT = upLeft;
		this.DOWNRIGHT = downRight;
		this.DOWNLEFT = downLeft;
		this.OUT_COLOR = outColor;
		this.IN_COLOR = inColor;
		this.borderHeight = borderHeight;
		this.borderWidth = borderWidth;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(TRANSPARENT_BLUE);
		g.fillRect(6, 6, getWidth() - 14, getHeight() - 14);
		g.setColor(OUT_COLOR);
		g.drawRect(6, 6, getWidth() - 14, getHeight() - 14);
		g.setColor(IN_COLOR);
		g.drawRect(8, 8, getWidth() - 18, getHeight() - 18);
		g.drawImage(UPLEFT, 0, 0, this);
		g.drawImage(UPRIGHT, getWidth() - borderWidth, 0, this);
		g.drawImage(DOWNLEFT, 0, getHeight() - borderHeight, this);
		g.drawImage(DOWNRIGHT, getWidth() - borderWidth, getHeight() - borderHeight, this);
	}

	public void paintAt(Graphics g, int x, int y) {
		g.setColor(TRANSPARENT_BLUE);
		g.fillRect(x + 6, y + 6, getWidth() - 14, getHeight() - 14);
		g.setColor(OUT_COLOR);
		g.drawRect(x + 6, y + 6, getWidth() - 14, getHeight() - 14);
		g.setColor(IN_COLOR);
		g.drawRect(x + 8, y + 8, getWidth() - 18, getHeight() - 18);
		g.drawImage(UPLEFT, x, y, this);
		g.drawImage(UPRIGHT, x + getWidth() - borderWidth, y, this);
		g.drawImage(DOWNLEFT, x, y + getHeight() - borderHeight, this);
		g.drawImage(DOWNRIGHT, x + getWidth() - borderWidth, y + getHeight() - borderHeight, this);
	}

}

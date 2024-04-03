package crl.ui.graphicsUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import crl.conf.gfx.data.GFXConfiguration;

class SwingInterfacePanel extends JPanel {

	private static final long serialVersionUID = -7392757206841150146L;
	private Image bufferImage;
	private Graphics bufferGraphics;

	private Image backImage;
	private Graphics backGraphics;

	private Image[] backImageBuffers;
	private Graphics[] backGraphicsBuffers;

	private Color color;
	private Font font;
	private FontMetrics fontMetrics;
	protected GFXConfiguration configuration;

	public void cls() {
		Color oldColor = bufferGraphics.getColor();
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.fillRect(0, 0, configuration.getScreenWidth(), configuration.getScreenHeight());
		bufferGraphics.setColor(oldColor);
	}

	public void setColor(Color color) {
		this.color = color;
		bufferGraphics.setColor(color);
	}

	public void setFontFace(Font f) {
		font = f;
		bufferGraphics.setFont(f);
		fontMetrics = bufferGraphics.getFontMetrics();
	}

	public Graphics2D getCurrentGraphics() {
		return (Graphics2D) bufferGraphics;
	}

	public SwingInterfacePanel(GFXConfiguration configuration) {
		this.configuration = configuration;
		setLayout(null);
		setBorder(new LineBorder(Color.GRAY));
	}

	public void init() {
		bufferImage = createImage(configuration.getScreenWidth(), configuration.getScreenHeight());
		bufferGraphics = bufferImage.getGraphics();
		bufferGraphics.setColor(Color.WHITE);
		backImage = createImage(configuration.getScreenWidth(), configuration.getScreenHeight());
		backGraphics = backImage.getGraphics();
		backImageBuffers = new Image[5];
		backGraphicsBuffers = new Graphics[5];
		for (int i = 0; i < 5; i++) {
			backImageBuffers[i] = createImage(configuration.getScreenWidth(), configuration.getScreenHeight());
			backGraphicsBuffers[i] = backImageBuffers[i].getGraphics();
		}

	}

	public void drawImage(Image img) {
		bufferGraphics.drawImage(img, 0, 0, this);
	}

	public void drawImage(int scrX, int scrY, Image img) {
		bufferGraphics.drawImage(img, scrX, scrY, this);
	}

	public void print(int x, int y, String text) {
		bufferGraphics.drawString(text, x, y);
		// repaint();
	}

	public void print(int x, int y, String text, Color c, boolean centered) {
		if (centered) {
			int width = fontMetrics.stringWidth(text);
			x = x - (width / 2);
		}
		Color old = bufferGraphics.getColor();
		bufferGraphics.setColor(c);
		bufferGraphics.drawString(text, x, y);
		bufferGraphics.setColor(old);
	}

	public void print(int x, int y, String text, Color c) {
		print(x, y, text, c, false);
		// repaint();
	}

	public void saveBuffer() {
		backGraphics.drawImage(bufferImage, 0, 0, this);
	}

	public void saveBuffer(int buffer) {
		backGraphicsBuffers[buffer].drawImage(bufferImage, 0, 0, this);
	}

	public void restore() {
		bufferGraphics.drawImage(backImage, 0, 0, this);
	}

	public void restore(int buffer) {
		bufferGraphics.drawImage(backImageBuffers[buffer], 0, 0, this);
	}

	public void flash(Color c) {
		// Void
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bufferImage != null) {
			g.drawImage(bufferImage, 0, 0, this);
		}
	}
}
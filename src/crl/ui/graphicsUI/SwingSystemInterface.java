package crl.ui.graphicsUI;

import crl.conf.gfx.data.GFXConfiguration;
import crl.game.Game;
import sz.SystemInterface;
import sz.csi.CharKey;
import sz.util.ImageUtils;
import sz.util.Position;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class SwingSystemInterface implements SystemInterface, Runnable {
	protected GFXConfiguration configuration;

	public void run() {
		// Void
	}

	private SwingInterfacePanel sip;
	private StrokeNClickInformer aStrokeInformer;
	private Position caretPosition = new Position(0, 0);
	private Map<String, Image> images = new HashMap<>();

	// private JTextArea invTextArea;
	private JFrame frameMain;
	private Point posClic;

	public void addMouseListener(MouseListener listener) {
		frameMain.removeMouseListener(listener);
		frameMain.addMouseListener(listener);
	}

	public void addMouseMotionListener(MouseMotionListener listener) {
		frameMain.removeMouseMotionListener(listener);
		frameMain.addMouseMotionListener(listener);
	}

	public void setCursor(Cursor c) {
		frameMain.setCursor(c);
	}

	public void setIcon(Image icon) {
		frameMain.setIconImage(icon);
	}

	public void setTitle(String title) {
		frameMain.setTitle(title);
	}

	public void setVisible(boolean bal) {
		frameMain.setVisible(bal);
	}

	public void showAlert(String message) {
		JOptionPane.showMessageDialog(frameMain, message, "Alert", JOptionPane.ERROR_MESSAGE);
	}

	public SwingSystemInterface(GFXConfiguration configuration) {
		this.configuration = configuration;
		frameMain = new JFrame();

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		frameMain.setBounds((size.width - configuration.getScreenWidth()) / 2,
				(size.height - configuration.getScreenHeight()) / 2, configuration.getScreenWidth(),
				configuration.getScreenHeight());
		frameMain.getContentPane().setLayout(new GridLayout(1, 1));
		frameMain.setUndecorated(true);

		sip = new SwingInterfacePanel(this.configuration);
		frameMain.getContentPane().add(sip);
		frameMain.setVisible(true);
		frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameMain.setBackground(Color.BLACK);
		// SZ030507 aStrokeInformer = new StrokeInformer();
		aStrokeInformer = new StrokeNClickInformer();
		frameMain.addKeyListener(aStrokeInformer);
		frameMain.addMouseListener(aStrokeInformer);
		frameMain.setFocusable(true);
		sip.init();

		frameMain.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				frameMain.setLocation(e.getX() - posClic.x + frameMain.getLocation().x,
						e.getY() - posClic.y + frameMain.getLocation().y);
			}

			public void mouseMoved(MouseEvent e) {
				// Void
			}

		});
		frameMain.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				// Void
			}

			public void mouseEntered(MouseEvent e) {
				// Void
			}

			public void mouseExited(MouseEvent e) {
				// Void
			}

			public void mousePressed(MouseEvent e) {
				posClic = e.getPoint();
			}

			public void mouseReleased(MouseEvent e) {
				// Void
			}
		});
		int n = JOptionPane.showConfirmDialog(frameMain, "Activate Full Screen Mode?", "Welcome to CastlevaniaRL",
				JOptionPane.YES_NO_OPTION);
		if (n == 0) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			if (gs.isFullScreenSupported()) {
				DisplayMode[] modes = gs.getDisplayModes();
				for (DisplayMode mode : modes) {
					System.out.println(mode.getWidth() + "x" + mode.getHeight());
				}
				int screenWidth = 1024;
				int screenHeight = 768;
				DisplayMode displayMode = gs.getDisplayMode();
				displayMode = new DisplayMode(screenWidth, screenHeight, displayMode.getBitDepth(),
						displayMode.getRefreshRate());
				gs.setFullScreenWindow(frameMain);
				try {
					gs.setDisplayMode(displayMode);
				} catch (Exception e) {
					gs.setFullScreenWindow(null);
				}
			}
		}
	}

	public void cls() {
		sip.cls();
	}

	public void drawImage(String filename) {
		Image im = images.get(filename);
		if (im == null) {
			try {
				im = ImageUtils.createImage(filename);
			} catch (Exception e) {
				Game.crash("Exception trying to create image " + filename, e);
			}
			images.put(filename, im);
		}
		sip.drawImage(im);
		sip.repaint();
	}

	public void drawImage(Image image) {
		sip.drawImage(image);
		sip.repaint();
	}

	public void refresh() {
		// invTextArea.setVisible(false);
		sip.repaint();
	}

	public void printAtPixel(int x, int y, String text) {
		sip.print(x, y, text);
	}

	public void printAtPixel(int x, int y, String text, Color color) {
		sip.print(x, y, text, color);
	}

	public void printAtPixelCentered(int x, int y, String text, Color color) {
		sip.print(x, y, text, color, true);
	}

	public void print(int x, int y, String text, Color color) {
		sip.print(x * 10, y * 24, text, color);
	}

	public void waitKey(int keyCode) {
		CharKey x = new CharKey(CharKey.NONE);
		while (x.code != keyCode)
			x = inkey();
	}

	public void drawImage(int scrX, int scrY, Image img) {
		sip.drawImage(scrX, scrY, img);
	}

	public void drawImage(int scrX, int scrY, String filename) {
		Image im = images.get(filename);
		if (im == null) {
			try {
				im = ImageUtils.createImage(filename);
			} catch (Exception e) {
				Game.crash("Exception trying to create image " + filename, e);
			}
			images.put(filename, im);
		}
		sip.drawImage(scrX, scrY, im);
	}

	public void drawImageCC(int consoleX, int consoleY, Image img) {
		drawImage(consoleX * 10, consoleY * 24, img);
	}

	public void drawImageCC(int consoleX, int consoleY, String img) {
		drawImage(consoleX * 10, consoleY * 24, img);
	}

	public synchronized CharKey inkey() {
		aStrokeInformer.informKey(Thread.currentThread());
		try {
			this.wait();
		} catch (InterruptedException ie) {
		}
		CharKey ret = new CharKey(aStrokeInformer.getInkeyBuffer());
		return ret;
	}

	public Graphics2D getGraphics2D() {
		return sip.getCurrentGraphics();
	}

	public void setFont(Font fnt) {
		sip.setFontFace(fnt);
		// invTextArea.setFont(fnt);
	}

	public void setColor(Color color) {
		sip.setColor(color);
		// invTextArea.setForeground(color);
	}

	// public String input(int consXPrompt,int consYPrompt,String prompt,Color
	// promptColor, int maxLength, Color textColor){
	public String input(int xpos, int ypos, Color textColor, int maxLength) {
		StringBuilder ret = new StringBuilder();
		CharKey read = new CharKey(CharKey.NONE);
		saveBuffer();
		while (true) {
			restore();
			printAtPixel(xpos, ypos, ret + "_", textColor);
			refresh();
			while (read.code == CharKey.NONE)
				read = inkey();
			if (read.code == CharKey.ENTER)
				break;
			if (read.code == CharKey.BACKSPACE) {
				if (ret.length() == 0) {
					read.code = CharKey.NONE;
					continue;
				}
				if (ret.length() > 1)
					ret = new StringBuilder(ret.substring(0, ret.length() - 1));
				else
					ret = new StringBuilder();
				caretPosition.x--;
				// print(caretPosition.x, caretPosition.y, " ");
			} else {
				if (ret.length() >= 50) {
					read.code = CharKey.NONE;
					continue;
				}
				if (!read.isAlphaNumeric()) {
					read.code = CharKey.NONE;
					continue;
				}

				String nuevo = read.toString();
				// print(caretPosition.x, caretPosition.y, nuevo, Color.WHITE);
				ret.append(nuevo);
				caretPosition.x++;
			}
			read.code = CharKey.NONE;
		}
		return ret.toString();
	}

	public void saveBuffer() {
		sip.saveBuffer();
	}

	public void saveBuffer(int buffer) {
		sip.saveBuffer(buffer);
	}

	public void restore() {
		sip.restore();
	}

	public void restore(int buffer) {
		sip.restore(buffer);
	}

	public void flash(Color c) {
		sip.flash(c);
	}

	public void add(Component c) {
		sip.add(c);
		sip.validate();
	}

	public void remove(Component c) {
		sip.remove(c);
		sip.validate();
	}

	public void recoverFocus() {
		frameMain.requestFocus();
	}
}
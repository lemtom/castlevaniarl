package sz.csi.wswing;

import java.awt.*;

/**
 * Shows the console Gets keyboard input
 */
public class SwingConsoleFrame extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;

	private SwingConsolePanel swingConsolePanel;

	public void init(Font f, int xdim, int ydim) {
		initComponents();
		swingConsolePanel.init(f, xdim, ydim);
	}

	private void initComponents() {
		swingConsolePanel = new SwingConsolePanel();
		getContentPane().setLayout(new BorderLayout(1, 1));
		setTitle("CastlevaniaRL - Santiago Zapata");
		setBackground(Color.black);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(swingConsolePanel);
		setCursor(null);
		pack();
	}

	public SwingConsolePanel getSwingConsolePanel() {
		return swingConsolePanel;
	}
}
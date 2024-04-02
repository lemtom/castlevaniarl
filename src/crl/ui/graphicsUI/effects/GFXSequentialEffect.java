package crl.ui.graphicsUI.effects;

import java.awt.Image;
import java.util.Enumeration;
import java.util.ArrayList;

import sz.util.Position;
import crl.conf.gfx.data.GFXConfiguration;
import crl.ui.graphicsUI.GFXUserInterface;
import crl.ui.graphicsUI.SwingSystemInterface;

public class GFXSequentialEffect extends GFXEffect {
	private ArrayList<Position> sequence;
	private Image[] tiles;

	public GFXSequentialEffect(String ID, ArrayList<Position> sequence, Image[] tiles, int delay,
			GFXConfiguration configuration) {
		super(ID, configuration);
		setAnimationDelay(delay);
		this.tiles = tiles;
		this.sequence = sequence;
	}

	public void drawEffect(GFXUserInterface ui, SwingSystemInterface si) {
		si.saveBuffer();
		Position relative = Position.subs(getPosition(), ui.getPlayer().getPosition());
		Position center = Position.add(ui.PC_POS, relative);
		int tileIndex = 0;

		for (Position next : sequence) {
			Position nextPosition = Position.add(center, next);
			tileIndex++;
			if (tileIndex == tiles.length)
				tileIndex = 0;
			if (ui.insideViewPort(nextPosition))
				ui.drawImageVP(nextPosition.x * 32, nextPosition.y * 32, tiles[tileIndex]);
			si.refresh();
			animationPause();
		}
		si.restore();
	}

}
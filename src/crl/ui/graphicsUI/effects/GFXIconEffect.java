package crl.ui.graphicsUI.effects;

import crl.conf.gfx.data.GFXConfiguration;
import crl.ui.graphicsUI.GFXUserInterface;
import crl.ui.graphicsUI.SwingSystemInterface;
import sz.util.Position;

import java.awt.*;

public class GFXIconEffect extends GFXEffect{
	private Image tile;

    public GFXIconEffect(String ID, Image tile, int delay, GFXConfiguration configuration){
    	super(ID, configuration);
		this.tile = tile;
		setAnimationDelay(delay);
    }

	public void drawEffect(GFXUserInterface ui, SwingSystemInterface si){
		si.saveBuffer();
		//si.setAutoRefresh(false);
		int height = 0;
		if (ui.getPlayer().getLevel().getMapCell(getPosition()) != null)
			height = ui.getPlayer().getLevel().getMapCell(getPosition()).getHeight();
		Position relative = Position.subs(getPosition(), ui.getPlayer().getPosition());
		Position center = Position.add(ui.PC_POS, relative);
		if (ui.insideViewPort(center))
			ui.drawImageVP(center.x * 32, center.y * 32 - 4 * height, tile);
		si.refresh();
		animationPause();
		si.restore();
	}
}
package crl.ui.graphicsUI.effects;

import crl.conf.gfx.data.GFXConfiguration;
import crl.ui.graphicsUI.GFXUserInterface;
import crl.ui.graphicsUI.SwingSystemInterface;
import sz.util.Position;

import java.awt.*;

public class GFXAnimatedEffect extends GFXEffect{
	private Image[] frames;
	
	private int xoff;
	private int yoff;
	
	public GFXAnimatedEffect(String ID, Image[] frames, int delay, GFXConfiguration configuration){
		super (ID, delay, configuration);
		this.frames = frames;
	}
	
	public GFXAnimatedEffect(String ID, Image[] frames, int delay, int xoff, int yoff, GFXConfiguration configuration){
		this (ID, frames, delay, configuration);
		this.xoff = xoff;
		this.yoff = yoff;
	}

	public void drawEffect(GFXUserInterface ui, SwingSystemInterface si){
		int height = 0;
		if (ui.getPlayer().getLevel().getMapCell(getPosition()) != null)
			height = ui.getPlayer().getLevel().getMapCell(getPosition()).getHeight();
		Position relative = Position.subs(getPosition(), ui.getPlayer().getPosition());
		Position absolute = Position.add(ui.PC_POS, relative);
		if (!ui.insideViewPort(absolute))
			return;
		si.saveBuffer();
        for (Image frame : frames) {
            ui.drawImageVP(
                    absolute.x * 32 + xoff,
                    absolute.y * 32 - 4 * height + yoff, frame);
            si.refresh();
            animationPause();
            si.restore();
        }
	}
}

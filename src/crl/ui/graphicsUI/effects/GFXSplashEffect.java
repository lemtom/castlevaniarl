package crl.ui.graphicsUI.effects;

import crl.conf.gfx.data.GFXConfiguration;
import crl.ui.graphicsUI.GFXUserInterface;
import crl.ui.graphicsUI.SwingSystemInterface;
import sz.util.Position;

import java.awt.*;

public class GFXSplashEffect extends GFXEffect{
	private Image[] tiles;

    public GFXSplashEffect(String ID, Image[] tiles, int delay, GFXConfiguration configuration){
    	super(ID, delay, configuration);
		this.tiles = tiles;
    }
    
	public void drawEffect(GFXUserInterface ui, SwingSystemInterface si){
		si.saveBuffer();
		//ui.refresh();
		Position relative = Position.subs(getPosition(), ui.getPlayer().getPosition());
		Position center = Position.add(ui.PC_POS, relative);
		int height = 0;
		if (ui.getPlayer().getLevel().getMapCell(getPosition()) != null)
			height = ui.getPlayer().getLevel().getMapCell(getPosition()).getHeight();
		ui.drawImageVP(center.x*32, 
				     center.y*32, tiles[0]);

		for (int ring = 1; ring < tiles.length; ring++){
			drawCircle(ui, si, center, ring, tiles[ring],height);
			si.refresh();
			animationPause();
		}
    }

	private void drawCircle(GFXUserInterface ui, SwingSystemInterface si, Position p, int radius, Image tile, int height){
		int d = 3 - (2 * radius);
		Position runner = new Position(0, radius);
		Position zero = new Position(0,0);
        do {
            if (Position.flatDistance(zero, runner) <= radius)
                drawCirclePixels(ui, si, p, runner.x, runner.y, tile, height);
            if (d < 0)
                d = d + (4 * runner.x) + 6;
            else {


                //d = d + 4 * (x-y) + 10;
                d = d + 4 * (runner.x - runner.y) + 10;
                runner.y--;
            }
            runner.x++;
        } while (runner.y != 0);
		
	}


	private void drawCirclePixels(GFXUserInterface ui, SwingSystemInterface si, Position center, int x, int y, Image tile, int height){
		if (ui.insideViewPort(center.x+x, center.y + y))
			ui.drawImageVP((center.x + x)*32, (center.y + y)*32-4*height, tile);
		if (ui.insideViewPort(center.x+x, center.y - y))
			ui.drawImageVP((center.x + x)*32, (center.y - y)*32-4*height, tile);
		if (ui.insideViewPort(center.x-x, center.y + y))
			ui.drawImageVP((center.x - x)*32, (center.y + y)*32-4*height, tile);
		if (ui.insideViewPort(center.x-x, center.y - y))
			ui.drawImageVP((center.x - x)*32, (center.y - y)*32-4*height, tile);
		if (ui.insideViewPort(center.x+y, center.y + x))
			ui.drawImageVP((center.x + y)*32, (center.y + x)*32-4*height, tile);
		if (ui.insideViewPort(center.x+y, center.y - x))
			ui.drawImageVP((center.x + y)*32, (center.y - x)*32-4*height, tile);
		if (ui.insideViewPort(center.x-y, center.y + x))
			ui.drawImageVP((center.x - y)*32, (center.y + x)*32-4*height, tile);
		if (ui.insideViewPort(center.x-y, center.y - x))
			ui.drawImageVP((center.x - y)*32, (center.y - x)*32-4*height, tile);
	}
}
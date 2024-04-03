package crl.ui.graphicsUI.effects;

import crl.conf.gfx.data.GFXConfiguration;
import sz.util.Line;
import sz.util.Position;

public abstract class GFXDirectedEffect extends GFXEffect {
	protected Line effectLine;
	protected int depth;
	private Position startPosition;
	
	public GFXDirectedEffect(String id, GFXConfiguration configuration){
		super(id, configuration);
	}

	public GFXDirectedEffect(String id, int delay, GFXConfiguration configuration){
		super(id, delay, configuration);
	}

    public void set(Position loc, Position startPosition, Position pivotPosition, int depth){
		super.set(loc);
		this.startPosition = new Position(loc);
		effectLine = new Line(startPosition,pivotPosition);
		setDepth(depth);
	}
	
	public int getDepth() {
		return depth;
	}

	public void setDepth(int value) {
		depth = value;
	}
	

}

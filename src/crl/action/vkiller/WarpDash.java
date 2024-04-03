package crl.action.vkiller;

import sz.util.Line;
import sz.util.Position;
import crl.action.HeartAction;
import crl.level.Cell;
import crl.level.Level;
import crl.player.Player;
import crl.ui.UserInterface;
import crl.ui.effects.EffectFactory;

public class WarpDash extends HeartAction {
private static final long serialVersionUID = 1L;
	public int getHeartCost() {
		return 3;
	}

	public String getID(){
		return "WarpDash";
	}
	
	@Override
	public boolean needsPosition(){
		return true;
	}

	@Override
	public String getPromptPosition(){
		return "Where do you want to dash?";
	}

	@Override
	public void execute(){
		super.execute();
		Player player = (Player) performer;
		Level level = player.getLevel();
		level.addMessage("You dash and disappear!");
		if (targetPosition.equals(performer.getPosition())){
        	level.addMessage("You appear in the same place!");
        	return;
        }

		Line line = new Line(player.getPosition(), targetPosition);
		Position runner = line.next();
		int i = 0;
		for (; i < 5; i++){
			runner = line.next();
        	Cell destinationCell = performer.getLevel().getMapCell(runner);
			if (
				destinationCell == null ||
				destinationCell.isSolid() ||
				destinationCell.getHeight() != level.getMapCell(player.getPosition()).getHeight()
			)
				break;
			
		}
		player.landOn(new Position(runner));
		drawEffect(EffectFactory.getSingleton().createDirectedEffect(performer.getPosition(), targetPosition, "SFX_WARPDASH", i));
		player.see();
		UserInterface.getUI().refresh();
	}

	@Override
	public int getCost(){
		Player p = (Player) performer;
		return (int)(p.getCastCost() * 1.4);
	}
	
	@Override
	public String getSFX(){
		return "wav/scrch.wav";
	}
}
package crl.action.renegade;

import sz.util.Line;
import sz.util.Position;
import crl.action.HeartAction;
import crl.level.Cell;
import crl.level.Level;
import crl.player.Player;
import crl.ui.UserInterface;
import crl.ui.effects.EffectFactory;

public class ShadeTeleport extends HeartAction {

	private static final long serialVersionUID = 1L;

	public String getID() {
		return "ShadeTeleport";
	}

	public int getHeartCost() {
		return 5;
	}

	@Override
	public boolean needsPosition() {
		return true;
	}

	@Override
	public String getPromptPosition() {
		return "Where do you want to blink?";
	}

	@Override
	public void execute() {
		super.execute();
		Player player = (Player) performer;
		Level level = player.getLevel();
		level.addMessage("You wrap in your cape and dissapear!");
		if (targetPosition.equals(performer.getPosition())) {
			level.addMessage("You appear in the same place!");
			return;
		}

        Line line = new Line(player.getPosition(), targetPosition);
		Position runner = line.next();
		int i = 0;
		for (; i < 8; i++) {
			runner = line.next();
			Cell destinationCell = performer.getLevel().getMapCell(runner);
			if (level.isWalkable(runner)
					&& destinationCell.getHeight() == level.getMapCell(player.getPosition()).getHeight())
				;
			else
				break;
		}
		drawEffect(EffectFactory.getSingleton().createDirectedEffect(player.getPosition(), targetPosition,
				"SFX_SHADETELEPORT", i));

		player.setPosition(new Position(runner));
		player.see();
		UserInterface.getUI().refresh();

	}

	@Override
	public int getCost() {
		Player p = (Player) performer;
		return (int) (p.getCastCost() * 1.4);
	}

	@Override
	public String getSFX() {
		return "wav/scrch.wav";
	}
}
package crl.action.monster;

import sz.util.Position;
import crl.action.Action;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Damage;
import crl.player.Player;

public class Dash extends Action {
	private static final long serialVersionUID = 1L;

	public String getID() {
		return "SLICE_DIVE";
	}

	@Override
	public boolean needsDirection() {
		return true;
	}

	public void execute() {
		Monster aMonster = (Monster) performer;
		targetDirection = aMonster.starePlayer();
		Position variation = directionToVariation(targetDirection);
		Level aLevel = performer.getLevel();
		Player aPlayer = aLevel.getPlayer();
		String message = "The " + aMonster.getDescription() + " dives to you!";
		Position destinationPoint = null;
		for (int i = 0; i < 5; i++) {
			destinationPoint = Position.add(performer.getPosition(), variation);
			if (!aLevel.isValidCoordinate(destinationPoint) || aLevel.isSolid(destinationPoint))
				break;
			if (aPlayer.getPosition().equals(destinationPoint)
					&& aPlayer.getStandingHeight() == aMonster.getStandingHeight()) {
				aPlayer.damage("The " + aMonster.getDescription() + " slices you!", aMonster,
						new Damage(aMonster.getAttack(), false));
			}

			aMonster.setPosition(destinationPoint);
		}
		aLevel.addMessage(message);
	}

	@Override
	public String getPromptDirection() {
		return "Where do you want to slice?";
	}

}
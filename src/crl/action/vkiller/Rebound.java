package crl.action.vkiller;

import sz.util.Debug;
import sz.util.Position;
import crl.action.Action;
import crl.feature.Feature;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;

public class Rebound extends Action {
	private static final long serialVersionUID = 1L;

	public String getID() {
		return "Rebound";
	}

	@Override
	public boolean needsDirection() {
		return true;
	}

	public void execute() {
		Debug.doAssert(performer instanceof Player, "action.Rebound");
		Player aPlayer = (Player) performer;

		Level aLevel = performer.getLevel();
		if (aPlayer.getHearts() < 1) {
			aLevel.addMessage("You don't have enough hearts");
			return;
		}
		aPlayer.reduceHearts(1);
		aLevel.addMessage("You throw a rebound crystal!");

		Position variation = new Position(directionToVariation(targetDirection));
		int runLength = 0;
		Position runner = new Position(performer.getPosition());
		StringBuilder message = new StringBuilder();
		Position bouncePoint = new Position(performer.getPosition());
		for (int i = 0; i < 20; i++) {
			runLength++;
			runner.add(variation);
			Feature destinationFeature = aLevel.getFeatureAt(runner);
			if (checkIfDestroyable(destinationFeature)) {
				message.append("The crystal hits the ").append(destinationFeature.getDescription());
				Feature prize = destinationFeature.damage(aPlayer, 1);
				if (prize != null) {
					message.append(", and destroys it");
				}
				aLevel.addMessage(message.toString());
				break;
			}
			Monster targetMonster = performer.getLevel().getMonsterAt(runner);
			message = new StringBuilder();
			if (targetMonster != null && !targetMonster.isInWater()) {
				message.append("The crystal hits the ").append(targetMonster.getDescription());
				targetMonster.damage(message, 1);
				if (targetMonster.isDead()) {
					message.append(", destroying it!");
					performer.getLevel().removeMonster(targetMonster);
				}
				if (targetMonster.wasSeen())
					aLevel.addMessage(message.toString());
				break;
			}

			Cell targetCell = performer.getLevel().getMapCell(runner);
			if (targetCell != null && (targetCell.isSolid()
					|| targetCell.getHeight() > aLevel.getMapCell(performer.getPosition()).getHeight() + 2)) {
				aLevel.addMessage("The crystal rebounds in the " + targetCell.getDescription());

				bouncePoint = new Position(runner);
				Position bounce = new Position(0, 0);
				if (aLevel.getMapCell(runner.x + variation.x, runner.y, performer.getPosition().z) == null
						|| aLevel.getMapCell(runner.x + variation.x, runner.y, performer.getPosition().z).isSolid())
					bounce.x = 1;
				if (aLevel.getMapCell(runner.x, runner.y + variation.y, performer.getPosition().z) == null
						|| aLevel.getMapCell(runner.x, runner.y + variation.y, performer.getPosition().z).isSolid())
					bounce.y = 1;

				variation.mul(Position.mul(new Position(-1, -1), bounce));
				runLength = 0;
			}

			if (i == 19) {
				/*
				 * x.setPosition(bouncePoint); x.setDepth(runLength); aLevel.addEffect(x);
				 */
			}
		}

	}

	@Override
	public int getCost() {
		Player p = (Player) performer;
		return 25 / (p.getShotLevel() + 1);
	}

	@Override
	public String getPromptDirection() {
		return "Where do you want to throw the Dagger?";
	}
}
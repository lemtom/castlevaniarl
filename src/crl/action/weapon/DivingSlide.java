package crl.action.weapon;

import sz.util.Position;
import crl.action.Action;
import crl.actor.Actor;
import crl.feature.Feature;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;

public class DivingSlide extends Action {
	private static final long serialVersionUID = 1L;

	public String getID() {
		return "DivingSlide";
	}

	@Override
	public boolean needsDirection() {
		return true;

	}

	@Override
	public String getPromptDirection() {
		return "Where do you want to slide?";
	}

	public void execute() {
		Player aPlayer = (Player) performer;
		if (!checkHearts(8))
			return;

		Position variation = directionToVariation(targetDirection);

		Level aLevel = performer.getLevel();
		if (aPlayer.getWeapon() == null) {
			aLevel.addMessage("You can't slide without a proper weapon");
			return;
		}
		int startingHeight = aLevel.getMapCell(performer.getPosition()).getHeight();
		Position startingPosition = new Position(aPlayer.getPosition());
		int jumpingRange = 3;
		if (aPlayer.hasIncreasedJumping())
			jumpingRange++;

		for (int i = 1; i <= jumpingRange; i++) {
			Position destinationPoint = Position.add(startingPosition, Position.mul(variation, i));
			Cell destinationCell = aLevel.getMapCell(destinationPoint);
			Cell currentCell = aLevel.getMapCell(performer.getPosition());
			if (destinationCell == null)
				return;
			if (destinationCell.getHeight() > startingHeight + 2) {
				aLevel.addMessage("You bump into the wall!");
				aPlayer.land();
				return;
			} else {
				if (destinationCell.getHeight() < startingHeight)
					aLevel.addMessage("You jump from the platform!");
				if (!destinationCell.isSolid()) {
					hit(destinationPoint, aLevel, aPlayer);
					aPlayer.landOn(destinationPoint);
				} else {
					aLevel.addMessage("You bump into the " + destinationCell.getShortDescription());
					aPlayer.land();
					return;
				}
			}
		}
	}

	private void hit(Position destinationPoint, Level aLevel, Player player) {
		StringBuilder message = new StringBuilder();
		Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
		if (checkIfDestroyable(destinationFeature)) {
			message.append("You slice the ").append(destinationFeature.getDescription());
			Feature prize = destinationFeature.damage(player, player.getWeapon().getAttack());
			if (prize != null) {
				message.append(", and cut it apart!");
			}
			aLevel.addMessage(message.toString());
		}

		Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
		message = new StringBuilder();
		if (targetMonster != null && !(targetMonster.isInWater() && targetMonster.canSwim())) {
			message.append("You slice the ").append(targetMonster.getDescription());
			targetMonster.damageWithWeapon(message, player.getWeaponAttack());
			if (targetMonster.isDead()) {
				message.append(", and cut it apart!");
				// performer.getLevel().removeMonster(targetMonster);
			}
			if (targetMonster.wasSeen())
				aLevel.addMessage(message.toString());
		}
	}

	@Override
	public boolean canPerform(Actor a) {
		Player aPlayer = (Player) a;
		Level aLevel = performer.getLevel();
		if (aPlayer.getHearts() < 8) {
			invalidationMessage = "You need more energy!";
			return false;
		}
		return true;
	}
}
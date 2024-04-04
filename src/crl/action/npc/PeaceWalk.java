package crl.action.npc;

import sz.util.Position;
import crl.action.Action;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;

public class PeaceWalk extends Action {
	private static final long serialVersionUID = 1L;

	public String getID() {
		return "PeaceWalk";
	}

	@Override
	public boolean needsDirection() {
		return true;
	}

	public void execute() {

		Position variation = directionToVariation(targetDirection);
		Position destinationPoint = Position.add(performer.getPosition(), variation);
		Level aLevel = performer.getLevel();
		if (!aLevel.isValidCoordinate(destinationPoint))
			return;
		Cell destinationCell = aLevel.getMapCell(destinationPoint);
		Cell currentCell = aLevel.getMapCell(performer.getPosition());
		Monster destinationMonster = aLevel.getMonsterAt(destinationPoint);
		if (checkEligibility(destinationPoint, aLevel, destinationCell, currentCell, destinationMonster))
			if (destinationCell.isEthereal())
				performer.setPosition(aLevel.getDeepPosition(destinationPoint));
			else
				performer.setPosition(destinationPoint);
	}

	private boolean checkEligibility(Position destinationPoint, Level aLevel, Cell destinationCell, Cell currentCell,
			Monster destinationMonster) {
		return destinationCell != null && !destinationCell.isSolid() && destinationMonster == null
				&& (currentCell == null || destinationCell.getHeight() == currentCell.getHeight())
				&& !destinationCell.isWater() && !destinationCell.isShallowWater()
				&& !aLevel.getPlayer().getPosition().equals(destinationPoint);
	}

}
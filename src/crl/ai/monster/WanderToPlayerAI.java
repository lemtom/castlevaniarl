package crl.ai.monster;

import crl.action.Action;
import crl.action.ActionFactory;
import crl.action.monster.MonsterWalk;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.level.Cell;
import crl.monster.Monster;
import sz.util.Debug;
import sz.util.Position;
import sz.util.Util;

/**
 * This AI is used by those enemies that just walk until they find the player,
 * optionally performing a ranged attack him when he is in range
 */
public class WanderToPlayerAI extends MonsterAI {
	private static final long serialVersionUID = 1L;

	public Action selectAction(Actor who) {
		Debug.doAssert(who instanceof Monster, "WanderToPlayerAI selectAction");
		Monster aMonster = (Monster) who;

		if (checkIfEnemyInLevel(aMonster)) {
			aMonster.setEnemy(null);
		}

		if (checkIfEnemyOrCharmed(aMonster)) {

			int directionToMonster = establishDirectionToMonster(aMonster);

			if (directionToMonster == -1) {
				directionToMonster = aMonster.starePlayer();
				return tryToWalkToPlayer(who, directionToMonster);
			} else {
				return walk(who, directionToMonster);
			}
		}

		int directionToPlayer = aMonster.starePlayer();
		if (directionToPlayer == -1) {
			// Wander aimlessly
			int direction = Util.rand(0, 7);
			Action ret = new MonsterWalk();
			ret.setDirection(direction);
			return ret;
		} else {
			int distanceToPlayer = Position.flatDistance(aMonster.getPosition(),
					aMonster.getLevel().getPlayer().getPosition());
			// Decide if will try to attack the player or walk to him
			if (canRangedAttack()) {
				// Try
				for (RangedAttack ra : rangedAttacks) {
					if (considerAttacking(distanceToPlayer, ra)) {
						Action ret = ActionFactory.getActionFactory().getAction(ra.getAttackId());
						ret.setDirection(directionToPlayer);
						return ret;
					}
				}

			}
			return moveTowardsPlayer(aMonster, directionToPlayer);
		}

	}

	/**
	 * Couldn't attack the player, move to him
	 */
	private Action moveTowardsPlayer(Monster aMonster, int directionToPlayer) {
		Action ret = new MonsterWalk();
		ret.setDirection(directionToPlayer);
		Cell currentCell = aMonster.getLevel().getMapCell(aMonster.getPosition());
		Cell destinationCell = aMonster.getLevel()
				.getMapCell(Position.add(aMonster.getPosition(), Action.directionToVariation(directionToPlayer)));

		if (nullCheck(currentCell, destinationCell)) {
			ret.setDirection(Util.rand(0, 7));
			return ret;
		}
		if (evaluateDestinationCell(aMonster, currentCell, destinationCell))
			ret.setDirection(Util.rand(0, 7));
		return ret;
	}

	private Action walk(Actor who, int directionToMonster) {
		Action ret = new MonsterWalk();
		if (!who.getLevel()
				.isWalkable(Position.add(who.getPosition(), Action.directionToVariation(directionToMonster)))) {
			directionToMonster = Util.rand(0, 7);
			while (true) {
				if (!Position.add(who.getPosition(), Action.directionToVariation(directionToMonster))
						.equals(who.getLevel().getPlayer().getPosition()))
					break;
			}
			ret.setDirection(directionToMonster);
		} else {
			ret.setDirection(directionToMonster);
		}
		return ret;
	}

	/**
	 * Walk TO player except if will bump him
	 */
	private Action tryToWalkToPlayer(Actor who, int directionToMonster) {
		if (directionToMonster == -1) {
			return null;
		} else {
			Position targetPositionX = Position.add(who.getPosition(), Action.directionToVariation(directionToMonster));
			if (!who.getLevel().isWalkable(targetPositionX)) {
				return null;
			} else {
				return walkConditionally(directionToMonster,
						who.getLevel().getPlayer().getPosition().equals(targetPositionX));
			}
		}
	}

	private Action walkConditionally(int directionToMonster, boolean condition) {
		if (condition) {
			return null;
		} else {
			Action ret = new MonsterWalk();
			ret.setDirection(directionToMonster);
			return ret;
		}
	}

	public String getID() {
		return "WANDER";
	}

	@Override
	public ActionSelector derive() {

		try {
			return (ActionSelector) clone();
		} catch (CloneNotSupportedException cnse) {
			return null;
		}
	}

	// Conditions

	private boolean nullCheck(Cell currentCell, Cell destinationCell) {
		return destinationCell == null || currentCell == null;
	}

	private boolean evaluateDestinationCell(Monster aMonster, Cell currentCell, Cell destinationCell) {
		return (destinationCell.isSolid() && !aMonster.isEthereal())
				|| destinationCell.getHeight() > currentCell.getHeight() + aMonster.getLeaping() + 1;
	}

	private boolean canRangedAttack() {
		return Util.chance(50) && rangedAttacks != null;
	}

	private boolean considerAttacking(int distanceToPlayer, RangedAttack ra) {
		return distanceToPlayer <= ra.getRange() && Util.chance(ra.getFrequency());
	}

}
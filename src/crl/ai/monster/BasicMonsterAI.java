package crl.ai.monster;

import crl.action.Action;
import crl.action.ActionFactory;
import crl.action.monster.*;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.monster.Monster;
import sz.util.OutParameter;
import sz.util.Position;
import sz.util.Util;

/**
 * Multifunctional AI Module
 * 
 * @author Slash
 *
 */
public class BasicMonsterAI extends MonsterAI {
	private static final long serialVersionUID = 1L;
	// AI Parameters
	/**
	 * Defines if the monster never moves
	 */
	private boolean isStationary;

	/**
	 * Determines if the monster waits for the player to get nearby than x tiles
	 * before attacking
	 */
	private int waitPlayerRange;

	/**
	 * Defines if the monster wants to keep away from the player to a certain
	 * distance
	 */
	private int approachLimit = 0;

	/**
	 * Defines if the player patrols an area, and when he must leave his patrol
	 * range to attack a player
	 */
	private int patrolRange = 0;

	// Extended monster attributes
	/**
	 * Keeps track of the charge count for attacks which need charge
	 */
	private int chargeCounter = 0;

	/**
	 * Keeps track of the last direction the monster walked on
	 */
	private int lastDirection = -1;

	/**
	 * Keeps track if the monster must change direction. Used mainly for patrolling
	 * monsters.
	 */
	private boolean changeDirection;

	/**
	 * Selects an action to perform
	 */
	public Action selectAction(Actor who) {
		Monster aMonster = (Monster) who;

		if (chargeCounter > 0) {
			chargeCounter--;
		}

		// If monster has an enemy, check if he is still at the level
		if (checkIfEnemyInLevel(aMonster)) {
			aMonster.setEnemy(null);
		}
		// If monster has an enemy, or is charmed
		if (checkIfEnemyOrCharmed(aMonster)) {
			// Stare at your enemy or pick a enemy from nearby monsters
			int directionToMonster = establishDirectionToMonster(aMonster);
			// If you found no enemy
			return findEnemy(who, aMonster, directionToMonster);
		}
		// else, monster has no enemy, and is not charmed
		// Stare to the player
		int directionToPlayer = aMonster.starePlayer();
		int playerDistance = Position.flatDistance(aMonster.getPosition(),
				aMonster.getLevel().getPlayer().getPosition());
		// If monster is a patroller, and player distance is bigger than patrol range,
		// continue patrolling
		if (patrolRange > 0 && playerDistance > patrolRange) {
			return patrol(aMonster);
		}
		// monster is not a patroller
		// If monster didn't see the player
		if (directionToPlayer == -1) {
			// If is stationary or semistationary, do nothing
			if (isStationary || waitPlayerRange > 0) {
				return null;
			} else {
				// Else walk randomly
				int direction = Util.rand(0, 7);
				return tryWalking(aMonster, direction);
			}
		} else {
			return seePlayer(who, aMonster, directionToPlayer, playerDistance);
		}
	}

	private Action findEnemy(Actor who, Monster aMonster, int directionToMonster) {
		if (directionToMonster == -1) {
			// Stare to the player
			directionToMonster = aMonster.starePlayer();
			// If you didn't find the player, do nothing
			if (directionToMonster == -1) {
				return null;
			} else {
				// Ensure we are not bumping the player
				Position targetPositionX = Position.add(who.getPosition(),
						Action.directionToVariation(directionToMonster));
				return walkConditionally(aMonster, directionToMonster,
						who.getLevel().getPlayer().getPosition().equals(targetPositionX));
			}
		} else { // Found your enemy
			// If you are stationary, do nothing
			return tryWalking(aMonster, directionToMonster);
		}
	}

	private Action patrol(Monster aMonster) {
		if (lastDirection == -1 || changeDirection) {
			lastDirection = Util.rand(0, 7);
			changeDirection = false;
		}
		return tryWalking(aMonster, lastDirection);
	}

	/**
	 * The monster saw the player.
	 */
	private Action seePlayer(Actor who, Monster aMonster, int directionToPlayer, int playerDistance) {
		// If is stationary or semistationary and the player is still too far, do
		// nothing
		if (waitPlayerRange > 0 && playerDistance > waitPlayerRange) {
			return null;
		}

		// If monster has an approach limit, and player is closer than it
		if (playerDistance < approachLimit) {
			// Get away from player
			int direction = Action.toIntDirection(Position.mul(Action.directionToVariation(directionToPlayer), -1));
			return tryWalking(aMonster, direction);
		} else {
			// else, just attack the player
			// If monster is on the water, swim to the player
			if (aMonster.canSwim() && aMonster.isInWater() && !aMonster.getLevel().getPlayer().isSwimming()) {
				return tryWalking(aMonster, directionToPlayer);
			}
			return tryAttacking(who, aMonster, directionToPlayer, playerDistance);
		}
	}

	/**
	 * If monster sees the player and has attacks, maybe try attacking the player.
	 */
	private Action tryAttacking(Actor who, Monster aMonster, int directionToPlayer, int playerDistance) {
		if (checkIfCanAttack(aMonster)) {

			// Pick an attack from those available
			for (RangedAttack element : rangedAttacks) {
				if (checkRange(aMonster, playerDistance, element)) {
					Action ret = ActionFactory.getActionFactory().getAction(element.getAttackId());
					// If attacks needs charge, ensure I have charge, else try another attack
					if (element.getChargeCounter() > 0) {
						if (chargeCounter > 0) {
							continue;
						} else {
							// Prepare to charge again, but try to execute the attack
							chargeCounter = element.getChargeCounter();
						}
					}

					configureAttack(element, ret);
					// Set the player position as the attack target
					ret.setPosition(who.getLevel().getPlayer().getPreviousPosition());
					return ret;
				}
			}
		}
		// Didn't try to attack the player, so try to walk to him
		return tryWalking(aMonster, directionToPlayer);
	}

	private boolean checkIfCanAttack(Monster aMonster) {
		return aMonster.seesPlayer() && rangedAttacks != null;
	}

	/**
	 * If the player is on attack range, and is either a direct attack or we are at
	 * the same height as the player, and randomly
	 * 
	 */
	private boolean checkRange(Monster aMonster, int playerDistance, RangedAttack element) {
		return element.getRange() >= playerDistance && Util.chance(element.getFrequency()) && (element.getAttackType()
				.equals(MonsterMissile.TYPE_DIRECT)
				|| (!element.getAttackType().equals(MonsterMissile.TYPE_DIRECT)
						&& aMonster.getStandingHeight() == aMonster.getLevel().getPlayer().getStandingHeight()));
	}

	/**
	 * Configure attack according to its type
	 */
	private void configureAttack(RangedAttack element, Action ret) {
		if (ret instanceof MonsterMissile) {
			((MonsterMissile) ret).set(element.getAttackType(), element.getStatusEffect(), element.getRange(),
					element.getAttackMessage(), element.getEffectType(), element.getEffectID(), element.getDamage(),
					element.getEffectWav());
		} else if (ret instanceof MonsterCharge) {
			((MonsterCharge) ret).set(element.getRange(), element.getAttackMessage(), element.getDamage(),
					element.getEffectWav());
		} else if (ret instanceof SummonMonster) {
			((SummonMonster) ret).set(element.getSummonMonsterId(), element.getAttackMessage());
		}
	}

	private Action walkConditionally(Monster aMonster, int directionToMonster, boolean condition) {
		if (condition) {
			return null;
		} else {
			return tryWalking(aMonster, directionToMonster);
		}
	}

	private Action tryWalking(Monster aMonster, int direction) {
		if (isStationary) {
			return null;
		} else {
			Action ret = null;
			// Check if must swim or walk
			if (aMonster.canSwim() && aMonster.isInWater()) {
				ret = new Swim();
			} else {
				ret = new MonsterWalk();
			}

			// Check if can walk toward direction directly or in approximate direction
			OutParameter direction1 = new OutParameter();
			OutParameter direction2 = new OutParameter();
			fillAlternateDirections(direction1, direction2, direction);
			if (canWalkTowards(aMonster, direction)) {
				ret.setDirection(direction);
			} else if (canWalkTowards(aMonster, direction1.getIntValue())) {
				ret.setDirection(direction1.getIntValue());
			} else if (canWalkTowards(aMonster, direction2.getIntValue())) {
				ret.setDirection(direction2.getIntValue());
			} else {
				// Can't walk toward direction directly, so just bump around
				ret.setDirection(Util.rand(0, 7));
			}
			return ret;
		}
	}

	/**
	 * Returns ArrayLists adjacent to a general direction
	 * <p>
	 * Example: If general direction is Left, will return Up-Left and Down-Left
	 * <p>
	 * O-- <@- O--
	 * <p>
	 * If general direction is Down-Right, will return Down and Right
	 * <p>
	 * --- -@O -OJ
	 * 
	 * @param direction1       Outparameter to be filled with one the adjacent
	 *                         directions
	 * @param direction2       Outparameter to be filled with one the adjacent
	 *                         directions
	 * @param generalDirection The general direction one of Action.UP, DOWN, LEFT,
	 *                         RIGHT, UPRIGHT, UPLEFT, DOWNRIGHT or DOWNLEFT
	 */
	private void fillAlternateDirections(OutParameter direction1, OutParameter direction2, int generalDirection) {
		Position pos = Action.directionToVariation(generalDirection);
		Position d1 = null;
		Position d2 = null;
		if (pos.x == 0) {
			d1 = new Position(-1, pos.y);
			d2 = new Position(1, pos.y);
		} else if (pos.y == 0) {
			d1 = new Position(pos.x, -1);
			d2 = new Position(pos.x, 1);
		} else {
			d1 = new Position(pos.x, 0);
			d2 = new Position(0, pos.y);
		}
		direction1.setIntValue(Action.toIntDirection(d1));
		direction2.setIntValue(Action.toIntDirection(d2));
	}

	/**
	 * Defines if a monster can walk toward a direction
	 */
	private boolean canWalkTowards(Monster aMonster, int direction) {
		Position destination = Position.add(aMonster.getPosition(), Action.directionToVariation(direction));
		if (!aMonster.getLevel().isValidCoordinate(destination))
			return false;
		if (aMonster.getLevel().getMonsterAt(destination) != null) {
			return false;
		}
		if (aMonster.getLevel().isAir(destination)) {
			return aMonster.isEthereal() || aMonster.isFlying();
		}
		if (!aMonster.getLevel().isWalkable(destination)) {
			return aMonster.isEthereal();
		} else
			return true;
	}

	public String getID() {
		return "BASIC_MONSTER_AI";
	}

	@Override
	public ActionSelector derive() {
		try {
			return (ActionSelector) clone();
		} catch (CloneNotSupportedException cnse) {
			return null;
		}
	}

	public void setApproachLimit(int limit) {
		approachLimit = limit;
	}

	public void setWaitPlayerRange(int limit) {
		waitPlayerRange = limit;
	}

	public void setPatrolRange(int limit) {
		patrolRange = limit;
	}

	public int getPatrolRange() {
		return patrolRange;
	}

	public void setStationary(boolean isStationary) {
		this.isStationary = isStationary;
	}

	public void setChangeDirection(boolean value) {
		changeDirection = value;
	}

}
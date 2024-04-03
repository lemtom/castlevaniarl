package crl.action;

import sz.util.Debug;
import sz.util.Position;
import crl.actor.Actor;
import crl.feature.Feature;
import crl.item.Merchant;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.npc.NPC;
import crl.player.Consts;
import crl.player.Damage;
import crl.player.Player;
import crl.ui.UserInterface;

public class Jump extends Action {
	private static final long serialVersionUID = 1L;
	private Player aPlayer;

	public String getID() {
		return "Jump";
	}

	@Override
	public boolean needsDirection() {
		return !aPlayer.hasCounter(Consts.C_BATMORPH) && !aPlayer.hasCounter(Consts.C_BATMORPH2)
				&& !aPlayer.isSwimming();

	}

	@Override
	public boolean canPerform(Actor a) {
		aPlayer = getPlayer(a);
		return super.canPerform(a);
	}

	@Override
	public String getPromptDirection() {
		return "Where do you want to jump?";
	}

	@Override
	public String getSFX() {
		Player p = (Player) performer;
		if (p.getSex() == Player.MALE) {
			return "wav/jump_male.wav";
		} else {
			return "wav/jump_male.wav";
		}

	}

	@Override
	public int getCost() {
		return (int) (aPlayer.getWalkCost() * 1.3);
	}

	public void execute() {
		Debug.doAssert(performer instanceof Player, "Walk action, tried for not player");
		aPlayer = (Player) performer;
		Position variation = directionToVariation(targetDirection);
		Level aLevel = performer.getLevel();
		if (aPlayer.isSwimming()) {
			tryFloating(aLevel);
			return;
		}
		if (aPlayer.hasCounter(Consts.C_BATMORPH) || aPlayer.hasCounter(Consts.C_BATMORPH2)) {
			tryFlying(aLevel);
			return;
		}

		if (targetDirection == Action.SELF) {
			aLevel.addMessage("You jump upward");
			return;
		}
		int startingHeight = aLevel.getMapCell(performer.getPosition()).getHeight();
		Position startingPosition = new Position(aPlayer.getPosition());
		int jumpingRange = 4;
		if (aPlayer.hasIncreasedJumping())
			jumpingRange++;
		aLevel.addMessage("You jump.");
		boolean messaged = false;
		aPlayer.setJustJumped(true);
		Cell currentCell = aLevel.getMapCell(startingPosition);
		aPlayer.doJump(aPlayer.getStandingHeight());
		loopOverJumpingRange(variation, aLevel, startingHeight, startingPosition, jumpingRange, messaged, currentCell);
		aPlayer.stopJump();
		aLevel.addMessage("You hold your breath.");
	}

	private void loopOverJumpingRange(Position variation, Level aLevel, int startingHeight, Position startingPosition,
			int jumpingRange, boolean messaged, Cell currentCell) {
		for (int i = 1; i < jumpingRange; i++) {
			Position destinationPoint = Position.add(startingPosition, Position.mul(variation, i));
			Cell destinationCell = aLevel.getMapCell(destinationPoint);
			if (destinationCell == null) {
				if (!aLevel.isValidCoordinate(destinationPoint)) {
					destinationPoint = Position.subs(destinationPoint, variation);
					aPlayer.landOn(destinationPoint);
					break;
				}
				if (i < jumpingRange - 1) {
					aPlayer.setPosition(destinationPoint);
					UserInterface.getUI().safeRefresh();
					actionAnimationPause();
					continue;
				} else {
					aPlayer.landOn(destinationPoint);
					break;
				}

			}
			Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
			if (destinationFeature != null && destinationFeature.getKeyCost() > aPlayer.getKeys()) {
				aPlayer.land();
				break;
			}
			if (destinationCell.getHeight() > startingHeight + 2) {
				aPlayer.land();
				break;
			} else {
				if (!messaged && destinationCell.getHeight() < startingHeight) {
					aLevel.addMessage("You fly from the " + currentCell.getShortDescription() + "!");
					messaged = true;
				}
				if (!destinationCell.isSolid()) {
					tryToLand(jumpingRange, i, destinationPoint);
				} else {
					aLevel.addMessage("You bump into the " + destinationCell.getShortDescription());
					aPlayer.land();
					break;
				}
			}
			Monster aMonster = aLevel.getMonsterAt(destinationPoint);
			if (aMonster != null && !(aMonster instanceof Merchant || aMonster instanceof NPC)) {
				// Damage the poor player and bounce him back
				if (aPlayer.damage("You are bounced back by the " + aMonster.getDescription() + "!", aMonster,
						new Damage(aMonster.getAttack(), false)))
					aLevel.getPlayer().bounceBack(Position.mul(variation, -1), 3);
				break;
			}
		}
	}

	private void tryToLand(int jumpingRange, int i, Position destinationPoint) {
		if (i < jumpingRange - 1) {
			aPlayer.setPosition(destinationPoint);
			UserInterface.getUI().safeRefresh();
			actionAnimationPause();
		} else {
			aPlayer.landOn(destinationPoint);
		}
	}

	private void tryFlying(Level aLevel) {
		if (aPlayer.getStandingHeight() > 3) {
			if (aPlayer.getPosition().z != 0) {
				Position deep = new Position(aPlayer.getPosition());
				deep.z--;
				if (aLevel.getMapCell(deep).getID().equals("AIR")) {
					aLevel.addMessage("You fly upward");
					aPlayer.setPosition(deep);
					aPlayer.setHoverHeight(0);
				} else {
					aLevel.addMessage("You can't fly upward");
				}
			} else {
				aLevel.addMessage("You can't fly upward");
			}
		} else {
			aLevel.addMessage("You fly upward.");
			aPlayer.setHoverHeight(aPlayer.getHoverHeight() + 1);
		}
	}

	private void tryFloating(Level aLevel) {
		if (aLevel.getMapCell(aPlayer.getPosition()).isShallowWater()) {
			aLevel.addMessage("You are already floating");
		} else {
			if (aPlayer.getPosition().z != 0) {
				Position deep = new Position(aPlayer.getPosition());
				deep.z--;
				if (aLevel.getMapCell(deep).isShallowWater()) {
					aLevel.addMessage("You float to the surface");
					aPlayer.landOn(deep);
				} else {
					aLevel.addMessage("You can't float upward");
				}
			} else {
				aLevel.addMessage("You can't float upward");
			}
		}
	}
}

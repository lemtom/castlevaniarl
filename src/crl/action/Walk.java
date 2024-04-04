package crl.action;

import sz.util.Debug;
import sz.util.Position;
import sz.util.Util;
import crl.Keycostable;
import crl.actor.Actor;
import crl.feature.Feature;
import crl.item.Merchant;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.npc.Hostage;
import crl.npc.NPC;
import crl.player.Damage;
import crl.player.Player;
import crl.ui.UserInterface;

public class Walk extends Action {
	private static final long serialVersionUID = 1L;
	private Player aPlayer;

	public String getID() {
		return "Walk";
	}

	@Override
	public boolean needsDirection() {
		return true;
	}

	public void execute() {
		Debug.doAssert(performer instanceof Player, "An actor different from the player tried to execute Walk action");
		Debug.enterMethod(this, "execute");
		aPlayer = (Player) performer;
		if (targetDirection == Action.SELF) {
			aPlayer.getLevel().addMessage("You stand alert.");
			return;
		}
		Position variation = directionToVariation(targetDirection);
		Position destinationPoint = Position.add(performer.getPosition(), variation);
		Level aLevel = performer.getLevel();
		Cell destinationCell = aLevel.getMapCell(destinationPoint);
		Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
		Cell currentCell = aLevel.getMapCell(performer.getPosition());

		if (destinationCell == null || destinationCell.isEthereal()) {
			if (!aLevel.isValidCoordinate(destinationPoint)) {
				aPlayer.land();
				Debug.exitMethod();
				return;
			}
			if (!aPlayer.isFlying()) {
				destinationPoint = aLevel.getDeepPosition(destinationPoint);
				if (destinationPoint == null) {
					aPlayer.land();
					Debug.exitMethod();
					return;
				} else {
					aLevel.addMessage("You fall!");
					destinationCell = aLevel.getMapCell(destinationPoint);
					currentCell = aLevel.getMapCell(destinationPoint);
				}
			} else {
				aPlayer.setPosition(destinationPoint);
				Debug.exitMethod();
				return;
			}
		}

		if (destinationCell.getHeight() > currentCell.getHeight() + 2 && !aPlayer.isEthereal() && !aPlayer.isFlying())
			aLevel.addMessage("You can't climb it.");
		else {
			tryToInteract(variation, destinationPoint, aLevel, destinationCell, destinationFeature, currentCell);
		}
		Debug.exitMethod();
	}

	private void tryToInteract(Position variation, Position destinationPoint, Level aLevel, Cell destinationCell,
			Feature destinationFeature, Cell currentCell) {
		if (destinationCell.getHeight() < currentCell.getHeight())
			aLevel.addMessage("You descend");
		if (isSolid(destinationCell)) {
			tryToBump(aLevel, destinationCell);
		} else if (isSolid(destinationFeature))
			aLevel.addMessage("You bump into the " + destinationFeature.getDescription());
		else if (!aLevel.isWalkable(destinationPoint) && !aPlayer.isEthereal())
			aLevel.addMessage("Your way is blocked");
		else if (checkKeyCost(destinationCell))
			aLevel.addMessage("You need " + (destinationCell.getKeyCost() - aPlayer.getKeys()) + " more keys to enter");
		else if (destinationFeature != null && checkKeyCost(destinationFeature))
			aLevel.addMessage(
					"You need " + (destinationFeature.getKeyCost() - aPlayer.getKeys()) + " more keys to enter");
		else {
			Actor aActor = aLevel.getActorAt(destinationPoint);
			if (compareHeight(aActor)) {
				interactWithActor(variation, destinationPoint, aLevel, aActor);
			} else {
				if (shouldAddBlood(aLevel))
					aLevel.addBlood(destinationPoint, Util.rand(0, 1));
				aPlayer.landOn(destinationPoint);
			}
		}
	}

	private void interactWithActor(Position variation, Position destinationPoint, Level aLevel, Actor aActor) {
		if (aActor instanceof Merchant && !((Merchant) aActor).isHostile()) {
			aPlayer.informPlayerEvent(Player.EVT_MERCHANT, aActor);
		} else {
			if (aActor instanceof NPC && !((NPC) aActor).isHostile()) {
				interactWithNPC(aLevel, (NPC) aActor);
			} else {
				if (aActor instanceof Monster) {
					interactWithMonster(variation, aLevel, (Monster) aActor);
				} else {
					if (shouldAddBlood(aLevel))
						aLevel.addBlood(destinationPoint, Util.rand(0, 1));
					aPlayer.landOn(destinationPoint);
				}
			}
		}
	}

	private boolean shouldAddBlood(Level aLevel) {
		return aLevel.getBloodAt(aPlayer.getPosition()) != null && Util.chance(30);
	}

	private void interactWithNPC(Level aLevel, NPC aActor) {
		if (aActor.getNPCID().equals("LARDA")) {
			aPlayer.informPlayerEvent(Player.EVT_INN, aActor);
		} else {
			aPlayer.informPlayerEvent(Player.EVT_CHAT, aActor);
			if (aActor.isPriest() && !aPlayer.getFlag("HEALED_BY_" + aActor.getNPCID())) {
				aPlayer.heal();
				aPlayer.setFlag("HEALED_BY_" + aActor.getNPCID(), true);
			}
			if (aActor instanceof Hostage && !aPlayer.hasHostage() && !((Hostage) aActor).isRescued()) {
				aPlayer.setHostage((Hostage) aActor);
				aPlayer.addHistoricEvent("found " + aActor.getDescription() + " at the " + aLevel.getDescription());
				aLevel.removeMonster(aActor);
			}
		}
	}

	private void interactWithMonster(Position variation, Level aLevel, Monster aMonster) {
		if (!aPlayer.isInvincible()) {
			if (aPlayer.hasEnergyField()) {
				StringBuilder buff = new StringBuilder("You shock the " + aMonster.getDescription() + "!");
				aMonster.damage(buff, aPlayer.getAttack());
				aLevel.addMessage(buff.toString());
			} else {
				if (aPlayer.damage("You bump with the " + aMonster.getDescription() + "!", aMonster,
						new Damage(aMonster.getAttack(), false))) {
					aLevel.getPlayer().bounceBack(Position.mul(variation, -1), 2);
					if (!aPlayer.getPosition().equals(aMonster.getPosition())) {
						aLevel.addMessage("You are bounced back by the " + aMonster.getDescription() + "!");
					}
				}
			}
		}
	}

	@Override
	public int getCost() {
		return aPlayer.getWalkCost();
	}
	
	private boolean compareHeight(Actor aActor) {
		return aActor != null && aActor.getStandingHeight() == aPlayer.getStandingHeight();
	}
	
	private boolean isSolid(Keycostable destination) {
		return destination != null && destination.isSolid() && !aPlayer.isEthereal();
	}

	private static void tryToBump(Level aLevel, Cell destinationCell) {
		if (destinationCell.getID().startsWith("SIGNPOST")) {
			UserInterface.getUI().setPersistantMessage(destinationCell.getDescription());
		} else {
			aLevel.addMessage("You bump into the " + destinationCell.getShortDescription());
		}
	}

	private boolean checkKeyCost(Keycostable destination) {
		return destination.getKeyCost() > aPlayer.getKeys();
	}
}
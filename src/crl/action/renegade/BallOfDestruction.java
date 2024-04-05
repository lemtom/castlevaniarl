package crl.action.renegade;

import sz.util.Position;
import crl.action.Action;
import crl.actor.Actor;
import crl.feature.Feature;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;
import crl.ui.effects.EffectFactory;

public class BallOfDestruction extends Action {
	private static final long serialVersionUID = 1L;

	public String getID() {
		return "BallOfDestruction";
	}

	@Override
	public boolean needsDirection() {
		return true;
	}

	@Override
	public String getSFX() {
		return "wav/fire.wav";
	}

	public void execute() {
		Position variation = directionToVariation(targetDirection);
		Level aLevel = performer.getLevel();
		Player aPlayer = aLevel.getPlayer();
		if (aPlayer.getHearts() < 4) {
			aLevel.addMessage("You need more hearts.");
			return;
		}
		aPlayer.reduceHearts(4);
		aLevel.addMessage("Three balls of fire emerge from your cape!");

		int otherDir1 = 0;
		int otherDir2 = 0;
		switch (targetDirection) {
		case Action.UP:
			otherDir1 = Action.UPLEFT;
			otherDir2 = Action.UPRIGHT;
			break;
		case Action.DOWN:
			otherDir1 = Action.DOWNLEFT;
			otherDir2 = Action.DOWNRIGHT;
			break;
		case Action.LEFT:
			otherDir1 = Action.UPLEFT;
			otherDir2 = Action.DOWNLEFT;
			break;
		case Action.RIGHT:
			otherDir1 = Action.UPRIGHT;
			otherDir2 = Action.DOWNRIGHT;
			break;
		case Action.UPRIGHT:
			otherDir1 = Action.UP;
			otherDir2 = Action.RIGHT;
			break;
		case Action.UPLEFT:
			otherDir1 = Action.UP;
			otherDir2 = Action.LEFT;
			break;
		case Action.DOWNLEFT:
			otherDir1 = Action.LEFT;
			otherDir2 = Action.DOWN;
			break;
		case Action.DOWNRIGHT:
			otherDir1 = Action.RIGHT;
			otherDir2 = Action.DOWN;
			break;
		case Action.SELF:
			aLevel.addMessage("The balls dissapear uppon hitting the floor");
			return;
		}
		Position var1 = directionToVariation(otherDir1);
		Position var2 = directionToVariation(otherDir2);
		int i = 0;
		for (; i < 20; i++) {
			Position destinationPoint = Position.add(aPlayer.getPosition(), Position.mul(variation, i + 1));
			if (hit(destinationPoint, i))
				break;
		}
		if (i == 20)
			drawEffect(EffectFactory.getSingleton().createDirectedEffect(aPlayer.getPosition(),
					this.getPositionalDirectionFrom(aPlayer.getPosition()), "SFX_RENEGADE_BOD", 20));
		// -----
		i = 0;
		for (; i < 20; i++) {
			Position destinationPoint = Position.add(aPlayer.getPosition(), Position.mul(var1, i + 1));
			if (hit(destinationPoint, i))
				break;
		}
		if (i == 20)
			drawEffect(EffectFactory.getSingleton().createDirectedEffect(aPlayer.getPosition(),
					this.getPositionalDirectionFrom(aPlayer.getPosition(), otherDir1), "SFX_RENEGADE_BOD", 20));
		// -----
		i = 0;
		for (; i < 20; i++) {
			Position destinationPoint = Position.add(aPlayer.getPosition(), Position.mul(var2, i + 1));
			if (hit(destinationPoint, i))
				break;
		}
		if (i == 20)
			drawEffect(EffectFactory.getSingleton().createDirectedEffect(aPlayer.getPosition(),
					this.getPositionalDirectionFrom(aPlayer.getPosition(), otherDir2), "SFX_RENEGADE_BOD", 20));

	}

	@Override
	public int getCost() {
		Player p = (Player) performer;
		return (int) (p.getCastCost() * 1.3);
	}

	private boolean hit(Position destinationPoint, int i) {
		StringBuilder message = new StringBuilder();
		Level aLevel = performer.getLevel();
		Player aPlayer = aLevel.getPlayer();

		Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
		if (checkIfDestroyable(destinationFeature)) {
			message.append("The fireball hits the ").append(destinationFeature.getDescription());
			drawEffect(EffectFactory.getSingleton().createDirectedEffect(aPlayer.getPosition(),
					this.getPositionalDirectionFrom(aPlayer.getPosition()), "SFX_RENEGADE_BOD", i));
			Feature prize = destinationFeature.damage(aPlayer, 1);
			if (prize != null) {
				message.append(", burning it!");
			}
			aLevel.addMessage(message.toString());
			return true;
		}
		Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
		Cell destinationCell = performer.getLevel().getMapCell(destinationPoint);
		if (targetMonster != null && !(targetMonster.isInWater() && targetMonster.canSwim())
				&& (destinationCell.getHeight() == aLevel.getMapCell(aPlayer.getPosition()).getHeight()
						|| destinationCell.getHeight() - 1 == aLevel.getMapCell(aPlayer.getPosition()).getHeight()
						|| destinationCell.getHeight() == aLevel.getMapCell(aPlayer.getPosition()).getHeight() - 1)) {

			if (targetMonster.wasSeen())
				message.append("The fireball burns the ").append(targetMonster.getDescription());
			// targetMonster.damage(player.getWhipLevel());
			targetMonster.damage(message, 1 + aPlayer.getSoulPower() * 2);
			drawEffect(EffectFactory.getSingleton().createDirectedEffect(aPlayer.getPosition(),
					this.getPositionalDirectionFrom(aPlayer.getPosition()), "SFX_RENEGADE_BOD", i));
			aLevel.addMessage(message.toString());

			return true;
		}
		return false;
	}

	@Override
	public String getPromptDirection() {
		return "Where do you want to throw the fireball?";
	}

	@Override
	public boolean canPerform(Actor a) {
		Player aPlayer = (Player) a;
		Level aLevel = performer.getLevel();
		if (aPlayer.getHearts() < 4) {
			invalidationMessage = "You need more energy!";
			return false;
		}
		return true;
	}
}
package crl.action.manbeast;

import sz.util.Position;
import crl.action.Action;
import crl.action.HeartAction;
import crl.feature.Feature;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;
import crl.ui.UserInterface;
import crl.ui.effects.EffectFactory;

public class EnergyScythe extends HeartAction {
	private static final long serialVersionUID = 1L;

	public int getHeartCost() {
		return 5;
	}

	public String getID() {
		return "EnergyScythe";
	}

	@Override
	public boolean needsDirection() {
		return true;
	}

	@Override
	public String getPromptDirection() {
		return "Where do you want to unleash your energy?";
	}

	@Override
	public void execute() {
		super.execute();
		Player aPlayer = (Player) performer;
		int damage = 10 + aPlayer.getAttack();
		Level aLevel = aPlayer.getLevel();
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
			aLevel.addMessage("Hitting yourself hard?");
			return;
		}
		hit(Position.add(performer.getPosition(), Action.directionToVariation(otherDir1)), damage);
		hit(Position.add(performer.getPosition(), Action.directionToVariation(targetDirection)), damage);
		hit(Position.add(performer.getPosition(), Action.directionToVariation(otherDir2)), damage);
		hit(Position.add(Position.add(performer.getPosition(), Action.directionToVariation(otherDir1)),
				Action.directionToVariation(targetDirection)), damage);
		hit(Position.add(Position.add(performer.getPosition(), Action.directionToVariation(targetDirection)),
				Action.directionToVariation(targetDirection)), damage);
		hit(Position.add(Position.add(performer.getPosition(), Action.directionToVariation(otherDir2)),
				Action.directionToVariation(targetDirection)), damage);

	}

	@Override
	public String getSFX() {
		return "wav/swaashll.wav";
	}

	@Override
	public int getCost() {
		Player p = (Player) performer;
		return (int) (p.getAttackCost() * 1.4);
	}

	private boolean hit(Position destinationPoint, int damage) {
		StringBuilder message = new StringBuilder();
		Level aLevel = performer.getLevel();
		Player aPlayer = aLevel.getPlayer();
		UserInterface.getUI()
				.drawEffect(EffectFactory.getSingleton().createLocatedEffect(destinationPoint, "SFX_RED_HIT"));
		Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
		if (checkIfDestroyable(destinationFeature)) {
			message.append("You crush the ").append(destinationFeature.getDescription());

			Feature prize = destinationFeature.damage(aPlayer, 4);
			if (prize != null) {
				message.append(", breaking it apart!");
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
				message.append("You crush the ").append(targetMonster.getDescription());
			targetMonster.damage(message, 2 * aPlayer.getPunchDamage());

			aLevel.addMessage(message.toString());

			return true;
		}
		return false;
	}
}
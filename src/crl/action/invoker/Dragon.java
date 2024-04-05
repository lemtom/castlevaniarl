package crl.action.invoker;

import sz.util.Position;
import crl.action.Action;
import crl.action.HeartAction;
import crl.feature.Feature;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;
import crl.ui.UserInterface;
import crl.ui.effects.EffectFactory;

public class Dragon extends HeartAction {
	private static final long serialVersionUID = 1L;

	public int getHeartCost() {
		return 8;
	}

	public String getID() {
		return "Dragon";
	}

	@Override
	public boolean needsDirection() {
		return true;
	}

	@Override
	public String getPromptDirection() {
		return "Where do you want to invoke the dragon?";
	}

	@Override
	public String getSFX() {
		return "wav/gurgle.wav";
	}

	@Override
	public int getCost() {
		Player p = (Player) performer;
		return (int) (p.getCastCost() * 1.5);
	}

	@Override
	public void execute() {
		super.execute();
		Player aPlayer = (Player) performer;
		Level aLevel = aPlayer.getLevel();
		aLevel.addMessage("You invoke a dragonfire!");
		int damage = 15 + 3 * aPlayer.getSoulPower();
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
			aLevel.addMessage("The dragonfire rises as a flaming column!");
			hit(Position.add(performer.getPosition(), Action.directionToVariation(Action.UP)), damage);
			hit(Position.add(performer.getPosition(), Action.directionToVariation(Action.UPLEFT)), damage);
			hit(Position.add(performer.getPosition(), Action.directionToVariation(Action.LEFT)), damage);
			hit(Position.add(performer.getPosition(), Action.directionToVariation(Action.DOWNLEFT)), damage);
			hit(Position.add(performer.getPosition(), Action.directionToVariation(Action.DOWN)), damage);
			hit(Position.add(performer.getPosition(), Action.directionToVariation(Action.DOWNRIGHT)), damage);
			hit(Position.add(performer.getPosition(), Action.directionToVariation(Action.RIGHT)), damage);
			hit(Position.add(performer.getPosition(), Action.directionToVariation(Action.UPRIGHT)), damage);
			return;
		}
		Position directionVar = Action.directionToVariation(targetDirection);
		Position runner1 = Position.add(performer.getPosition(), Action.directionToVariation(otherDir1));
		Position runner2 = Position.add(performer.getPosition(), Action.directionToVariation(targetDirection));
		Position runner3 = Position.add(performer.getPosition(), Action.directionToVariation(otherDir2));
		for (int i = 0; i < 10; i++) {
			hit(runner1, damage);
			hit(runner2, damage);
			hit(runner3, damage);
			runner1.add(directionVar);
			runner2.add(directionVar);
			runner3.add(directionVar);
		}
	}

	private boolean hit(Position destinationPoint, int damage) {
		StringBuilder message = new StringBuilder();
		Level aLevel = performer.getLevel();
		Player aPlayer = aLevel.getPlayer();
		UserInterface.getUI()
				.drawEffect(EffectFactory.getSingleton().createLocatedEffect(destinationPoint, "SFX_DRAGONFIRE"));
		Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
		if (checkIfDestroyable(destinationFeature)) {
			message.append("The dragon crushes the ").append(destinationFeature.getDescription());

			Feature prize = destinationFeature.damage(aPlayer, damage);
			if (prize != null) {
				message.append(", breaking it apart!");
			}
			aLevel.addMessage(message.toString());
			return true;
		}
		Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
		if (targetMonster != null && !(targetMonster.isInWater() && targetMonster.canSwim())) {
			if (targetMonster.wasSeen())
				message.append("The dragon slashes the ").append(targetMonster.getDescription());
			targetMonster.damage(message, damage);
			if (targetMonster.isDead()) {
				message.append(", finishing it!");
			}
			aLevel.addMessage(message.toString());

			return true;
		}
		return false;
	}

}
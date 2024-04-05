package crl.action.vkiller;

import java.util.ArrayList;

import sz.util.Position;
import crl.action.HeartAction;
import crl.feature.Feature;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;
import crl.ui.effects.EffectFactory;

public class Bible extends HeartAction {
	private static final long serialVersionUID = 1L;

	public int getHeartCost() {
		return 2;
	}

	public String getID() {
		return "Bible";
	}

	private int getDamage() {
		return 7 + getPlayer().getShotLevel() + getPlayer().getSoulPower() * 2;
	}

	@Override
	public void execute() {
		super.execute();
		Level aLevel = performer.getLevel();
		Player aPlayer = (Player) performer;
		aPlayer.getLevel().addMessage("You open the bible!");
		drawEffect(EffectFactory.getSingleton().createLocatedEffect(performer.getPosition(), "SFX_BIBLE"));

		int damage = getDamage();
		for (Position step : steps) {
			Position destinationPoint = Position.add(performer.getPosition(), step);
			StringBuilder message = new StringBuilder();
			Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
			if (checkIfDestroyable(destinationFeature)) {
				message.append("The ").append(destinationFeature.getDescription()).append(" is slashed");
				Feature prize = destinationFeature.damage(aLevel.getPlayer(), damage);
				if (prize != null) {
					message.append(" and thorn apart!");
				} else
					message.append(".");
				aLevel.addMessage(message.toString());
			}

			Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
			message = new StringBuilder();
			if (targetMonster != null) {
				message.append("The ").append(targetMonster.getDescription()).append(" is slashed");
				targetMonster.damage(message, damage);
				if (targetMonster.isDead()) {
					message.append(" apart!");
					performer.getLevel().removeMonster(targetMonster);
				} else {
					message.append(".");
				}
				if (targetMonster.wasSeen())
					aLevel.addMessage(message.toString());
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
		return "Where do you want to throw the Cross?";
	}

	private static final ArrayList<Position> steps = new ArrayList<>(10);

	static {
		steps.add(new Position(1, 0));
		steps.add(new Position(2, -1));
		steps.add(new Position(1, -2));
		steps.add(new Position(0, -2));
		steps.add(new Position(-1, -2));
		steps.add(new Position(-2, -1));
		steps.add(new Position(-2, 0));
		steps.add(new Position(-2, 1));
		steps.add(new Position(-1, 2));
		steps.add(new Position(0, 2));
		steps.add(new Position(1, 2));
		steps.add(new Position(2, 2));
		steps.add(new Position(3, 1));
		steps.add(new Position(4, 0));
		steps.add(new Position(4, -1));
		steps.add(new Position(4, -2));
		steps.add(new Position(4, -3));
		steps.add(new Position(3, -4));
		steps.add(new Position(2, -4));
		steps.add(new Position(1, -4));
		steps.add(new Position(0, -4));
		steps.add(new Position(-1, -4));
		steps.add(new Position(-2, -4));
		steps.add(new Position(-3, -3));
		steps.add(new Position(-4, -2));
		steps.add(new Position(-4, -1));
		steps.add(new Position(-4, 0));
		steps.add(new Position(-4, 1));
		steps.add(new Position(-4, 2));
		steps.add(new Position(-3, 3));
		steps.add(new Position(-2, 4));
		steps.add(new Position(-1, 4));
		steps.add(new Position(0, 4));
		steps.add(new Position(1, 4));
		steps.add(new Position(2, 4));
		steps.add(new Position(3, 4));
		steps.add(new Position(4, 3));
		steps.add(new Position(5, 2));
		steps.add(new Position(6, 1));
		steps.add(new Position(6, 0));
		steps.add(new Position(6, -1));
		steps.add(new Position(6, -2));
		steps.add(new Position(6, -3));
		steps.add(new Position(6, -4));
		steps.add(new Position(5, -5));
		steps.add(new Position(4, -6));
		steps.add(new Position(3, -7));
		steps.add(new Position(2, -8));
		steps.add(new Position(1, -9));
		steps.add(new Position(0, -10));
		steps.add(new Position(-1, -11));
		steps.add(new Position(-2, -12));
		steps.add(new Position(-3, -13));
		steps.add(new Position(-4, -14));
		steps.add(new Position(-5, -15));
		steps.add(new Position(-6, -16));
	}

	@Override
	public String getSFX() {
		return "wav/loutwarp.wav";
	}

}
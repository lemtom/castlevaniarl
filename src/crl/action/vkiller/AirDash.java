package crl.action.vkiller;

import sz.util.Line;
import sz.util.Position;
import crl.action.HeartAction;
import crl.feature.Feature;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;
import crl.ui.effects.EffectFactory;

public class AirDash extends HeartAction {
	private static final long serialVersionUID = 1L;

	public int getHeartCost() {
		return 5;
	}

	public String getID() {
		return "AirDash";
	}

	@Override
	public boolean needsPosition() {
		return true;
	}

	@Override
	public String getPromptPosition() {
		return "Where do you want to dash?";
	}

	private int getDamage() {
		return 10 + getPlayer().getAttack();
	}

	@Override
	public void execute() {
		super.execute();
		Player aPlayer = (Player) performer;
		Level aLevel = aPlayer.getLevel();
		aLevel.addMessage("You jump and dash forward!");
		if (targetPosition.equals(performer.getPosition())) {
			aLevel.addMessage("You fall back.");
			return;
		}

		int damage = getDamage();

		boolean hit = false;
		Line fireLine = new Line(performer.getPosition(), targetPosition);

		Position previousPoint = aPlayer.getPosition();
		int projectileHeight = aLevel.getMapCell(aPlayer.getPosition()).getHeight();
		for (int i = 0; i < 4; i++) {
			Position destinationPoint = fireLine.next();
			if (aLevel.isSolid(destinationPoint)) {
				drawEffect(EffectFactory.getSingleton().createDirectedEffect(performer.getPosition(), targetPosition,
						"SFX_AIRDASH", i));
				aPlayer.landOn(previousPoint);
				return;
			}

			int destinationHeight = aLevel.getMapCell(destinationPoint).getHeight();

			if (destinationHeight == projectileHeight) {
				Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
				if (checkIfDestroyable(destinationFeature)) {
					tryToDestroy(aPlayer, aLevel, damage, previousPoint, i, destinationFeature);
					return;
				}
			}
			Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);

			if (targetMonster != null) {
				int monsterHeight = destinationHeight + targetMonster.getHoverHeight();
				if (projectileHeight == monsterHeight) {
					if (targetMonster.tryMagicHit(aPlayer, damage, 100, targetMonster.wasSeen(), "dash", false,
							performer.getPosition())) {
						drawEffect(EffectFactory.getSingleton().createDirectedEffect(aPlayer.getPosition(),
								targetPosition, "SFX_AIRDASH", i));
						hit = true;
						loopOverRunner(aLevel, damage, fireLine, destinationPoint, targetMonster);
						aPlayer.landOn(previousPoint);
						return;
					}
				} else if (projectileHeight < monsterHeight) {
					aLevel.addMessage("You dash under the " + targetMonster.getDescription());
				} else {
					aLevel.addMessage("You dash over the " + targetMonster.getDescription());
				}
			}
			previousPoint = destinationPoint;
		}

		drawEffect(EffectFactory.getSingleton().createDirectedEffect(aPlayer.getPosition(), targetPosition,
				"SFX_AIRDASH", 4));
		aPlayer.landOn(previousPoint);
	}

	private void tryToDestroy(Player aPlayer, Level aLevel, int damage, Position previousPoint, int i,
			Feature destinationFeature) {
		String message = "You hit the " + destinationFeature.getDescription();
		drawEffect(EffectFactory.getSingleton().createDirectedEffect(performer.getPosition(), targetPosition,
				"SFX_AIRDASH", i));
		Feature prize = destinationFeature.damage(aPlayer, damage);
		if (prize != null) {
			message += " and destroys it.";
		}
		aLevel.addMessage(message);
		aPlayer.landOn(previousPoint);
	}

	private void loopOverRunner(Level aLevel, int damage, Line fireLine, Position destinationPoint,
			Monster targetMonster) {
		Position runner = new Position(destinationPoint);
		for (int ii = 0; ii < 2; ii++) {
			Cell fly = aLevel.getMapCell(runner);
			if (fly == null)
				break;
			if (!fly.isSolid()) {
				targetMonster.setPosition(runner);
			} else {
				StringBuilder byff = new StringBuilder("You smash the " + targetMonster.getDescription()
						+ " against the " + fly.getDescription() + "!");
				targetMonster.damage(byff, damage);
				aLevel.addMessage(byff.toString());
			}
			runner = fireLine.next();
		}
	}

	@Override
	public int getCost() {
		Player p = (Player) performer;
		return (int) (p.getWalkCost() * 1.4);
	}

	@Override
	public String getSFX() {
		return "wav/scrch.wav";
	}
}
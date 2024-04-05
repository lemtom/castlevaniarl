package crl.action;

import sz.util.Line;
import sz.util.OutParameter;
import sz.util.Position;
import crl.feature.Feature;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;
import crl.ui.effects.EffectFactory;

public abstract class BeamProjectileSkill extends ProjectileSkill {
	private static final long serialVersionUID = 1L;

	public String plottedLocatedEffect() {
		return null;
	}

	@Override
	public boolean allowsSelfTarget() {
		return false;
	}

	private boolean[] deadLines = new boolean[3];

	@Override
	public void execute() {
		if (targetPosition.equals(performer.getPosition()))
			return;
		reduceHearts();
		Level aLevel = performer.getLevel();
		Player aPlayer = aLevel.getPlayer();
		int attackHeight = aLevel.getMapCell(aPlayer.getPosition()).getHeight();
		if (showThrowMessage())
			aLevel.addMessage(getShootMessage());
		targetDirection = getGeneralDirection(performer.getPosition(), targetPosition);

		OutParameter outPosition1 = new OutParameter();
		OutParameter outPosition2 = new OutParameter();
		Action.fillNormalPositions(performer.getPosition(), targetDirection, outPosition1, outPosition2);

		Position start1 = (Position) outPosition1.getObject();
		Position start2 = (Position) outPosition2.getObject();

		Action.fillNormalPositions(targetPosition, targetDirection, outPosition1, outPosition2);
		Position end1 = (Position) outPosition1.getObject();
		Position end2 = (Position) outPosition2.getObject();

		Line fireLine = new Line(performer.getPosition(), targetPosition);
		Line fireLine1 = new Line(start1, end1);
		Line fireLine2 = new Line(start2, end2);
		deadLines[0] = false;
		deadLines[1] = false;
		deadLines[2] = false;
		fireLine.next();
		int projectileHeight = attackHeight;
		loopOverRange(aLevel, aPlayer, start1, start2, end1, end2, fireLine, fireLine1, fireLine2, projectileHeight);
		for (int hits = 0; hits < 3; hits++) {
			Position originPoint = null;
			Position finalPoint = null;
			switch (hits) {
			case 0:
				originPoint = performer.getPosition();
				finalPoint = targetPosition;
				break;
			case 1:
				originPoint = start1;
				finalPoint = end1;
				break;
			case 2:
				originPoint = start2;
				finalPoint = end2;
				break;
			}
			if (!deadLines[hits])
				drawEffect(EffectFactory.getSingleton().createDirectedEffect(originPoint, finalPoint, getSFXID(),
						getRange()));
		}
	}

	private void loopOverRange(Level aLevel, Player aPlayer, Position start1, Position start2, Position end1,
			Position end2, Line fireLine, Line fireLine1, Line fireLine2, int projectileHeight) {
		for (int i = 0; i < getRange(); i++) {
			for (int hits = 0; hits < 3; hits++) {
				if (deadLines[hits])
					continue;
				Position destinationPoint = null;
				Position originPoint = null;
				Position finalPoint = null;
				switch (hits) {
				case 0:
					destinationPoint = fireLine.next();
					originPoint = performer.getPosition();
					finalPoint = targetPosition;
					break;
				case 1:
					destinationPoint = fireLine1.next();
					originPoint = start1;
					finalPoint = end1;
					break;
				case 2:
					destinationPoint = fireLine2.next();
					originPoint = start2;
					finalPoint = end2;
					break;
				}
				if (!aLevel.isValidCoordinate(destinationPoint))
					continue;
				if (plottedLocatedEffect() != null) {
					drawEffect(
							EffectFactory.getSingleton().createLocatedEffect(destinationPoint, plottedLocatedEffect()));
				}
				if (aLevel.isSolid(destinationPoint) && !piercesThru()) {
					drawEffect(
							EffectFactory.getSingleton().createDirectedEffect(originPoint, finalPoint, getSFXID(), i));
					deadLines[hits] = true;
					continue;

				}

				int destinationHeight = aLevel.getMapCell(destinationPoint).getHeight();

				if (destinationHeight == projectileHeight) {
					Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
					if (checkIfDestroyable(destinationFeature)) {
						if (!piercesThru()) {
							drawEffect(EffectFactory.getSingleton().createDirectedEffect(originPoint, finalPoint,
									getSFXID(), i));
						}
						aLevel.addMessage(hitMessage(aPlayer, destinationFeature));
						if (!piercesThru()) {
							deadLines[hits] = true;
							continue;
						}
					}
				}
				Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);

				if (targetMonster != null) {
					int monsterHeight = destinationHeight + targetMonster.getHoverHeight();

					if (projectileHeight == monsterHeight) {
						if (targetMonster.tryMagicHit(aPlayer, getDamage(), getHit(), targetMonster.wasSeen(),
								getSpellAttackDesc(), isWeaponAttack(), performer.getPosition()) && (!piercesThru())) {
							drawEffect(EffectFactory.getSingleton().createDirectedEffect(originPoint, finalPoint,
									getSFXID(), i));
							deadLines[hits] = true;

						}
					} else {
						aLevel.addMessage(
								spellFliesMessage(targetMonster.getDescription(), projectileHeight, monsterHeight));
					}
				}
			}
		}
	}

}
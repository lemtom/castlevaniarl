package crl.action;

import java.util.ArrayList;
import java.util.List;

import sz.util.Line;
import sz.util.Position;
import crl.actor.Actor;
import crl.feature.Feature;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;
import crl.ui.effects.EffectFactory;

public abstract class ProjectileSkill extends HeartAction {
	private static final long serialVersionUID = 1L;

	public abstract String getSelfTargettedMessage();

	public abstract String getShootMessage();

	public abstract int getRange();

	public boolean piercesThru() {
		return false;
	}

	public boolean showThrowMessage() {
		return true;
	}

	public abstract int getPathType();

	public static final int PATH_DIRECT = 0;
	public static final int PATH_CURVED = 1;
	public static final int PATH_LINEAR = 2;
	public static final int PATH_HOVER = 3;
	protected Position finalPoint;

	public abstract String getSFXID();

	public abstract int getDamage();

	public abstract String getSpellAttackDesc();

	@Override
	public abstract String getPromptPosition();

	public abstract int getHit();

	@Override
	public boolean needsPosition() {
		return true;
	}

	private ArrayList<Monster> hitMonsters = new ArrayList<>(10);

	public List<Monster> getHitMonsters() {
		return hitMonsters;
	}

	@Override
	public void execute() {
		super.execute();
		hitMonsters.clear();
		Level aLevel = performer.getLevel();
		Player aPlayer = aLevel.getPlayer();
		int attackHeight = aLevel.getMapCell(aPlayer.getPosition()).getHeight();
		if (targetPosition.equals(performer.getPosition()) && allowsSelfTarget()) {
			handleSelfTarget(aLevel, aPlayer);
			return;
		}
		if (showThrowMessage())
			aLevel.addMessage(getShootMessage());
		boolean hit = false;
		Line fireLine = new Line(performer.getPosition(), targetPosition);

		boolean curved = false;
		int flyStart = 0;
		int flyEnd = 0;
		if (getPathType() == PATH_CURVED) {
			curved = true;
			flyStart = (int) Math.ceil(getRange() / 3.0D);
			flyEnd = (int) Math.ceil(2 * (getRange() / 3.0D));
		}
		int projectileHeight = attackHeight;
		for (int i = 0; i <= getRange(); i++) {
			Position destinationPoint = fireLine.next();
			finalPoint = destinationPoint;
			if (!aLevel.isValidCoordinate(destinationPoint))
				continue;
			if (curved) {
				projectileHeight = calculateProjectileHeight(attackHeight, flyStart, flyEnd, i);
			}
			if (checkIfSolid(aLevel, projectileHeight, destinationPoint)) {
				drawIfSfxEffect(performer, i - 1);
				return;
			}

			if (aLevel.getMapCell(destinationPoint) == null && getPathType() == PATH_HOVER) {
				destinationPoint = aLevel.getDeepPosition(destinationPoint);
				if (destinationPoint == null) {
					drawIfSfxEffect(performer, i);
					return;
				}
				projectileHeight = aLevel.getMapCell(destinationPoint).getHeight();

			}

			int destinationHeight = calculateDestinationHeight(aLevel.getMapCell(destinationPoint));

			if (getPathType() == PATH_HOVER) {
				if (destinationHeight < projectileHeight)
					projectileHeight = destinationHeight;
				else if (destinationHeight > projectileHeight) {
					drawIfSfxEffect(performer, i);
					return;
				}
			}

			if (destinationHeight == projectileHeight) {
				Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
				if (checkIfDestroyable(destinationFeature)) {
					aLevel.addMessage(spellHits(aPlayer, i, destinationFeature));
					if (!piercesThru())
						return;
				}
			}
			Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);

			if (targetMonster != null) {
				int monsterHeight = destinationHeight + targetMonster.getHoverHeight();
				if (checkDirectPathToMonster(projectileHeight, monsterHeight)) {
					if (targetMonster.tryMagicHit(aPlayer, getDamage(), getHit(), targetMonster.wasSeen(),
							getSpellAttackDesc(), isWeaponAttack(), performer.getPosition())) {
						hit = true;
						hitMonsters.add(targetMonster);
						if (!piercesThru()) {
							drawIfSfxEffect(aPlayer, i);
							return;
						}
					}
				} else {
					aLevel.addMessage(
							spellFliesMessage(targetMonster.getDescription(), projectileHeight, monsterHeight));
				}
			}
		}
		drawEffectIfPierced(aPlayer, hit);
	}

	private boolean checkIfDestroyable(Feature destinationFeature) {
		return destinationFeature != null && destinationFeature.isDestroyable();
	}

	private boolean checkDirectPathToMonster(int projectileHeight, int monsterHeight) {
		return projectileHeight == monsterHeight || getPathType() == PATH_DIRECT;
	}

	private void drawEffectIfPierced(Player aPlayer, boolean hit) {
		if (!hit || piercesThru() && (getSFXID() != null))
			drawEffect(EffectFactory.getSingleton().createDirectedEffect(aPlayer.getPosition(), targetPosition,
					getSFXID(), getRange() - 1));
	}

	private boolean checkIfSolid(Level aLevel, int projectileHeight, Position destinationPoint) {
		return aLevel.isSolid(destinationPoint)
				|| projectileHeight < -1 + aLevel.getMapCell(destinationPoint).getHeight();
	}

	private int calculateDestinationHeight(Cell mapCell) {
		if (mapCell != null) {
			return mapCell.getHeight();
		}
		return 0;
	}

	protected String spellFliesMessage(String monsterDescription, int projectileHeight, int monsterHeight) {
		String underOver = projectileHeight < monsterHeight ? "under" : "over";
		return "The " + getSpellAttackDesc() + " flies " + underOver + " the " + monsterDescription;
	}

	private void drawIfSfxEffect(Actor actor, int i) {
		if (getSFXID() != null)
			drawEffect(EffectFactory.getSingleton().createDirectedEffect(actor.getPosition(), targetPosition,
					getSFXID(), i));
	}

	private void handleSelfTarget(Level aLevel, Player aPlayer) {
		aLevel.addMessage(getSelfTargettedMessage());
		Feature destinationFeature = aLevel.getFeatureAt(getPlayer().getPosition());
		if (checkIfDestroyable(destinationFeature)) {
			aLevel.addMessage(hitMessage(aPlayer, destinationFeature));
		}

		Monster targetMonster = performer.getLevel().getMonsterAt(getPlayer().getPosition());

		if (targetMonster != null && targetMonster.tryMagicHit(aPlayer, getDamage(), getHit(), targetMonster.wasSeen(),
				getSpellAttackDesc(), isWeaponAttack(), performer.getPosition())) {
			hitMonsters.add(targetMonster);
		}
	}

	protected String hitMessage(Player aPlayer, Feature destinationFeature) {
		String message = "The " + getSpellAttackDesc() + " hits the " + destinationFeature.getDescription();
		Feature prize = destinationFeature.damage(aPlayer, getDamage());
		if (prize != null) {
			message += " and destroys it.";
		}
		return message;
	}

	private String spellHits(Player aPlayer, int i, Feature destinationFeature) {
		if (!piercesThru() && getSFXID() != null) {
			drawEffect(EffectFactory.getSingleton().createDirectedEffect(performer.getPosition(), targetPosition,
					getSFXID(), i));
		}
		return hitMessage(aPlayer, destinationFeature);
	}

	private int calculateProjectileHeight(int attackHeight, int flyStart, int flyEnd, int i) {
		int projectileHeight;
		if (i > flyStart && i < flyEnd)
			projectileHeight = attackHeight + 1;
		else
			projectileHeight = attackHeight;
		return projectileHeight;
	}

	public boolean allowsSelfTarget() {
		return true;
	}

	public boolean isWeaponAttack() {
		return false;
	}
}
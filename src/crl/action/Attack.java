package crl.action;

import sz.util.Position;
import sz.util.Util;
import crl.actor.Actor;
import crl.feature.Feature;
import crl.item.Item;
import crl.item.ItemDefinition;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Consts;
import crl.player.Player;
import crl.ui.effects.Effect;
import crl.ui.effects.EffectFactory;

public class Attack extends Action {
	private static final long serialVersionUID = 1L;

	public String getID() {
		return "Attack";
	}

	@Override
	public boolean needsDirection() {
		return true;
	}

	private int reloadTime;
	private Item weapon;

	public void execute() {
		Position variation = directionToVariation(targetDirection);
		Player player = null;
		reloadTime = 0;
		try {
			player = (Player) performer;
		} catch (ClassCastException cce) {
			return;
		}

		weapon = player.getWeapon();
		Level aLevel = performer.getLevel();
		if (!player.canAttack()) {
			aLevel.addMessage("You can't attack!");
			return;
		}

		if (weapon == null || weapon.getDefinition().getWeaponCategory().equals(ItemDefinition.CAT_UNARMED)) {
			if (targetDirection == Action.SELF && aLevel.getMonsterAt(player.getPosition()) == null) {
				aLevel.addMessage("Don't hit yourself");
				return;
			}
			tryToDamage(player, aLevel);
			return;
		}

		if (targetDirection == Action.SELF && aLevel.getMonsterAt(player.getPosition()) == null) {
			aLevel.addMessage("That's a coward way to give up!");
			return;
		}

		targetPosition = Position.add(performer.getPosition(), Action.directionToVariation(targetDirection));
		int startHeight = aLevel.getMapCell(player.getPosition()).getHeight() + player.getHoverHeight();
		ItemDefinition weaponDef = weapon.getDefinition();

		if (weapon.getReloadTurns() > 0 && weapon.getRemainingTurnsToReload() == 0 && !reload(weapon, player)) {
			return;
		}

		createEffects(variation, player, aLevel, weaponDef);

		boolean hitsSomebody = tryToHitSomebody(variation, player, aLevel, startHeight);
		attemptFireballWHip(player, hitsSomebody);
		if (weapon.getReloadTurns() > 0 && weapon.getRemainingTurnsToReload() > 0)
			weapon.setRemainingTurnsToReload(weapon.getRemainingTurnsToReload() - 1);
		if (weaponDef.isSingleUse()) {
			handleSingleUseWeapon(player);
		}
	}

	private void attemptFireballWHip(Player player, boolean hitsSomebody) {
		if (!hitsSomebody && player.hasCounter(Consts.C_FIREBALL_WHIP)) {
			Action fireball = new WhipFireball();
			fireball.setPerformer(performer);
			fireball.setPosition(targetPosition);
			fireball.execute();
		}
	}

	private boolean tryToHitSomebody(Position variation, Player player, Level aLevel, int startHeight) {
		boolean hits = false;
		boolean hitsSomebody = false;
		for (int i = 0; i < weapon.getRange(); i++) {
			Position destinationPoint = Position.add(performer.getPosition(), Position.mul(variation, i + 1));

			String message = "";

			Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);

			hits = tryToHit(aLevel, startHeight, hits, aLevel.getMapCell(destinationPoint), targetMonster);

			if (hits) {
				hits = false;
				hitsSomebody = true;
				dealDamage(player, aLevel, targetMonster);
			}

			Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
			if (isDestroyable(destinationFeature)) {
				hitsSomebody = true;
				if (player.sees(destinationPoint))
					message = "You hit the " + destinationFeature.getDescription();
				else
					message = "You hit something";
				destinationFeature.damage(player, weapon.getAttack());
				aLevel.addMessage(message);
			}

			Cell targetMapCell = aLevel.getMapCell(destinationPoint);
			if (targetMapCell != null && targetMapCell.isSolid()) {
				hitsSomebody = true;
			}

			if (hitsSomebody && !weapon.isSlicesThrough())
				break;
		}
		return hitsSomebody;
	}

	private boolean isDestroyable(Feature feature) {
		return feature != null && feature.isDestroyable();
	}

	private void handleSingleUseWeapon(Player player) {
		if (weapon.getReloadTurns() > 0) {
			if (weapon.getRemainingTurnsToReload() == 0) {
				player.setWeapon(null);
			}
		} else {
			if (player.hasItem(weapon))
				player.reduceQuantityOf(weapon);
			else
				player.setWeapon(null);
		}
	}

	private void dealDamage(Player player, Level aLevel, Monster targetMonster) {
		int penalty = Position.distance(targetMonster.getPosition(), player.getPosition()) / 4;
		int attack = player.getWeaponAttack() + Util.rand(0, 2);
		attack -= penalty;
		if (attack < 1)
			attack = 1;
		StringBuilder hitMsg = new StringBuilder();
		if (weapon.isHarmsUndead() && targetMonster.isUndead()) {
			attack *= 2;
			if (targetMonster.wasSeen())
				hitMsg.append("You critically damage the ").append(targetMonster.getDescription()).append("!");
			else
				hitMsg.append("You hit something!");
		} else {
			if (targetMonster.wasSeen())
				hitMsg.append("You hit the ").append(targetMonster.getDescription());
			else
				hitMsg.append("You hit something!");
		}

		targetMonster.damageWithWeapon(hitMsg, attack);
		aLevel.addMessage(hitMsg.toString());
	}

	private boolean tryToHit(Level aLevel, int startHeight, boolean hits, Cell destinationCell, Monster targetMonster) {
		if (targetMonster != null) {
			if ((targetMonster.isInWater() && targetMonster.canSwim())
					|| destinationCell.getHeight() < startHeight - 1) {
				if (targetMonster.wasSeen())
					aLevel.addMessage("The attack passes over the " + targetMonster.getDescription());
			} else {
				if (destinationCell.getHeight() > startHeight + 1) {
					if (weapon.getVerticalRange() > 0) {
						hits = true;
					} else {
						if (targetMonster.wasSeen()) {
							aLevel.addMessage("The attack passes under the " + targetMonster.getDescription());
						}
					}
				} else {
					hits = true;
				}
			}
		}
		return hits;
	}

	private void createEffects(Position var, Player player, Level aLevel, ItemDefinition weaponDef) {
		String[] sfx = weaponDef.getAttackSFX().split(" ");
		if (sfx.length > 0)
			if (sfx[0].equals("MELEE")) {
				Effect me = EffectFactory.getSingleton().createDirectionalEffect(performer.getPosition(),
						targetDirection, weapon.getRange(), "SFX_WP_" + weaponDef.getID());
				aLevel.addEffect(me);
			} else if (sfx[0].equals("BEAM") || sfx[0].equals("MISSILE")) {
				Effect me = EffectFactory.getSingleton().createDirectedEffect(performer.getPosition(), targetPosition,
						"SFX_WP_" + weaponDef.getID(), weapon.getRange());
				if (sfx[0].equals("MISSILE") && !weapon.isSlicesThrough()) {
					me = handleSlice(var, player, aLevel, weaponDef);
				}
				aLevel.addEffect(me);
			}
	}

	private void tryToDamage(Player player, Level aLevel) {
		Position targetPosition = Position.add(player.getPosition(), Action.directionToVariation(targetDirection));
		Monster targetMonster = aLevel.getMonsterAt(targetPosition);
		String attackDescription = player.getPunchDescription();
		int punchDamage = player.getPunchDamage();
		int push = player.getPunchPush();

		if (targetMonster != null && targetMonster.getStandingHeight() == player.getStandingHeight()) {
			StringBuilder buff = new StringBuilder(
					"You " + attackDescription + " the " + targetMonster.getDescription() + "!");
			targetMonster.damageWithWeapon(buff, punchDamage);
			aLevel.addMessage(buff.toString());
			if (push != 0)
				pushMonster(targetMonster, aLevel, push);
		}
		Feature targetFeature = aLevel.getFeatureAt(targetPosition);
		if (isDestroyable(targetFeature)) {
			aLevel.addMessage("You " + attackDescription + " the " + targetFeature.getDescription());
			targetFeature.damage(player, punchDamage);
		}

		Cell targetMapCell = aLevel.getMapCell(targetPosition);
		if (targetMapCell != null && targetMapCell.isSolid()) {
			aLevel.addMessage("You " + attackDescription + " the " + targetMapCell.getShortDescription());
		}
	}

	private Effect handleSlice(Position var, Player player, Level aLevel, ItemDefinition weaponDef) {
		Effect me;
		int i = 0;
		for (i = 0; i < weapon.getRange(); i++) {
			Position destinationPoint = Position.add(performer.getPosition(), Position.mul(var, i + 1));
			Cell destinationCell = aLevel.getMapCell(destinationPoint);
			Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
			if (isDestroyable(destinationFeature))
				break;
			Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
			if (targetMonster != null && !(targetMonster.isInWater() && targetMonster.canSwim())
					&& (destinationCell.getHeight() == aLevel.getMapCell(player.getPosition()).getHeight()
							|| destinationCell.getHeight() - 1 == aLevel.getMapCell(player.getPosition()).getHeight()
							|| destinationCell.getHeight() == aLevel.getMapCell(player.getPosition()).getHeight() - 1))
				break;
		}
		me = EffectFactory.getSingleton().createDirectedEffect(performer.getPosition(), targetPosition,
				"SFX_WP_" + weaponDef.getID(), i);
		return me;
	}

	@Override
	public String getPromptDirection() {
		return "Where do you want to attack?";
	}

	public int getDirection() {
		return targetDirection;
	}

	private boolean reload(Item weapon, Player aPlayer) {
		if (weapon != null) {
			if (aPlayer.getGold() < weapon.getDefinition().getReloadCostGold() || aPlayer.getHearts() < 2) {
				aPlayer.getLevel().addMessage("You can't reload the " + weapon.getDescription());
				return false;
			} else {
				weapon.reload();
				aPlayer.reduceGold(weapon.getDefinition().getReloadCostGold());
				aPlayer.reduceHearts(2);
				aPlayer.getLevel().addMessage("You reload the " + weapon.getDescription() + " ("
						+ weapon.getDefinition().getReloadCostGold() + " gold)");
				reloadTime = 10 * weapon.getDefinition().getReloadTurns();
				return true;
			}
		} else
			aPlayer.getLevel().addMessage("You can't reload yourself");
		return false;
	}

	@Override
	public int getCost() {
		Player player = (Player) performer;
		if (weapon != null) {
			return player.getAttackCost() + weapon.getAttackCost() + reloadTime;
		} else {
			return (int) (player.getAttackCost() * 1.5);
		}
	}

	@Override
	public String getSFX() {
		Player p = (Player) performer;
		weapon = p.getWeapon();
		if (weapon != null && !weapon.getAttackSound().equals("DEFAULT")) {
			return weapon.getAttackSound();
		} else {
			if (((Player) performer).getSex() == Player.MALE)
				return "wav/punch_male.wav";
			else
				return "wav/punch_male.wav";
		}
	}

	private void pushMonster(Monster targetMonster, Level aLevel, int spaces) {
		Position varP = Action.directionToVariation(targetDirection);
		Position runner = Position.add(targetMonster.getPosition(), varP);
		for (int i = 0; i < spaces; i++) {
			Cell fly = aLevel.getMapCell(runner);
			if (fly == null)
				return;
			if (!fly.isSolid()) {
				if (fly.isWater() || fly.isShallowWater()) {
					if (targetMonster.canSwim()) {
						if (i == spaces - 1)
							aLevel.addMessage("You throw the " + targetMonster.getDescription() + " into the water!");
						targetMonster.setPosition(runner);

					} else if (targetMonster.isEthereal()) {
						targetMonster.setPosition(runner);
					} else {
						aLevel.addMessage("You throw the " + targetMonster.getDescription() + " into the water!");
						aLevel.addMessage("The " + targetMonster.getDescription() + " drowns!");
						targetMonster.die();
						aLevel.removeMonster(targetMonster);
						return;
					}
				} else {
					targetMonster.setPosition(runner);
				}
			} else {
				StringBuilder buff = new StringBuilder("You smash the " + targetMonster.getDescription()
						+ " against the " + fly.getDescription() + "!");
				targetMonster.damage(buff, 2);
				aLevel.addMessage(buff.toString());
			}
			runner.add(varP);
		}

	}

	@Override
	public boolean canPerform(Actor a) {
		Player player = getPlayer(a);
		if (!player.canAttack()) {
			invalidationMessage = "You can't attack";
			return false;
		}
		if (player.getWeapon() != null && player.getWeapon().getWeaponCategory().equals(ItemDefinition.CAT_BOWS)) {
			Monster nearest = player.getNearestMonster();
			if (nearest != null && Position.distance(nearest.getPosition(), player.getPosition()) < 2) {
				invalidationMessage = "You can't aim your " + player.getWeapon().getDescription()
						+ " this close to the enemy, get away!";
				return false;
			}
		}
		return true;
	}

	static class WhipFireball extends ProjectileSkill {
		private static final long serialVersionUID = 1L;

		public int getDamage() {
			return 4;
		}

		public int getHeartCost() {
			return 0;
		}

		public int getHit() {
			return 100;
		}

		public int getPathType() {
			return PATH_LINEAR;
		}

		public String getPromptPosition() {
			return null;
		}

		public int getRange() {
			return 15;
		}

		public String getSelfTargettedMessage() {
			return null;
		}

		public String getSFXID() {
			return "SFX_WHIP_FIREBALL";
		}

		public String getShootMessage() {
			return "A fireball flies out from your whip";
		}

		public String getSpellAttackDesc() {
			return "fireball";
		}

		@Override
		public boolean needsPosition() {
			return false;
		}

		public String getID() {
			return null;
		}

	}
}
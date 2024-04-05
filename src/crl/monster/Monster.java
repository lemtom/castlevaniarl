package crl.monster;

import sz.util.*;
import crl.Visible;
import crl.action.*;
import crl.item.*;
import crl.level.Emerger;
import crl.level.EmergerAI;
import crl.npc.NPC;
import crl.feature.*;
import crl.ui.*;
import crl.ui.effects.EffectFactory;
import crl.ai.monster.boss.DraculaAI;
import crl.player.Consts;
import crl.player.Player;
import crl.actor.*;

public class Monster extends Actor implements Cloneable, Visible {
	private static final long serialVersionUID = 1L;

	// Attributes
	private transient MonsterDefinition definition;
	private String defID;

	protected int hits;
	private int maxHits;
	private String featurePrize;
	private boolean visible = true;

	private boolean wasSeen = false;

	private Monster enemy;

	public String getWavOnHit() {
		return getDefinition().getWavOnHit();
	}

	public void setWasSeen(boolean value) {
		wasSeen = true;
	}

	public boolean wasSeen() {
		return wasSeen;
	}

	public void increaseHits(int i) {
		hits += i;
	}

	@Override
	public void act() {
		if (hasCounter(Consts.C_MONSTER_FREEZE) || hasCounter(Consts.C_MONSTER_SLEEP)) {
			setNextTime(50);
			updateStatus();
			return;
		}
		super.act();
		wasSeen = false;
	}

	public boolean isInWater() {
		if (level.getMapCell(getPosition()) != null)
			return level.getMapCell(getPosition()).isShallowWater();
		else
			return false;
	}

	public void freeze(int cont) {
		setCounter(Consts.C_MONSTER_FREEZE, cont);
	}

	public int getFreezeResistance() {
		return 0; // placeholder
	}

	@Override
	public Appearance getAppearance() {
		return getDefinition().getAppearance();
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (Exception x) {
			return null;
		}
	}

	/** returns the direction in which the player is seen */
	public int starePlayer() {
		if (level.getPlayer() == null || level.getPlayer().isInvisible()
				|| level.getPlayer().getPosition().z != getPosition().z)
			return -1;
		if (Position.flatDistance(level.getPlayer().getPosition(), getPosition()) <= getDefinition().getSightRange()) {
			return determinePosition();
		}
		return -1;
	}

	private int determinePosition() {
		Position pp = level.getPlayer().getPosition();
		if (pp.x == getPosition().x) {
			if (pp.y > getPosition().y) {
				return Action.DOWN;
			} else {
				return Action.UP;
			}
		} else if (pp.y == getPosition().y) {
			if (pp.x > getPosition().x) {
				return Action.RIGHT;
			} else {
				return Action.LEFT;
			}
		} else if (pp.x < getPosition().x) {
			if (pp.y > getPosition().y)
				return Action.DOWNLEFT;
			else
				return Action.UPLEFT;
		} else {
			if (pp.y > getPosition().y)
				return Action.DOWNRIGHT;
			else
				return Action.UPRIGHT;
		}
	}

	public void damageWithWeapon(StringBuilder message, int dam) {
		Item wep = level.getPlayer().getWeapon();
		if (wep != null)
			level.getPlayer().increaseWeaponSkill(wep.getDefinition().getWeaponCategory());
		else
			level.getPlayer().increaseWeaponSkill(ItemDefinition.CAT_UNARMED);
		damage(message, dam);
	}

	public void damage(StringBuilder message, int dam) {
		if (getSelector() instanceof DraculaAI) {
			((DraculaAI) getSelector()).setOnBattle(true);
		}
		if (Util.chance(getEvadeChance())) {
			if (wasSeen())
				level.addMessage("The " + getDescription() + " " + getEvadeMessage());
			return;
		}
		if (hasCounter(Consts.C_MONSTER_FREEZE))
			dam *= 2;
		message.append(" (").append(dam).append(")");
		hits -= dam;
		UserInterface.getUI()
				.drawEffect(EffectFactory.getSingleton().createLocatedEffect(getPosition(), "SFX_QUICK_WHITE_HIT"));
		if (getDefinition().getBloodContent() > 0) {
			if (level.getPlayer().hasCounter(Consts.C_BLOOD_THIRST)
					&& Position.flatDistance(getPosition(), level.getPlayer().getPosition()) < 3) {
				int recover = (int) (double) (getDefinition().getBloodContent() / 30);
				level.addMessage(
						"You drink some of the " + getDefinition().getDescription() + " blood! (+" + recover + ")");
				level.getPlayer().recoverHits(recover);
			}
			if (Util.chance(40)) {
				getLevel().addBlood(getPosition(), Util.rand(0, 1));
			}
		}
		if (level.getPlayer().getFlag("HEALTH_REGENERATION") && Util.chance(30)) {
			level.getPlayer().recoverHits(1);
		}

		if (isDead()) {
			if (this == level.getBoss()) {
				level.getPlayer().addKeys(1);
				level.addEffect(EffectFactory.getSingleton().createLocatedEffect(getPosition(), "SFX_BOSS_DEATH"));
				level.addMessage("The whole level trembles with holy energy!");
				level.removeBoss();
				level.getPlayer().addHistoricEvent(
						"vanquished the " + this.getDescription() + " on the " + level.getDescription());
				level.anihilate();
				level.removeRespawner();
			} else {
				level.getPlayer().increaseMUpgradeCount();
				setPrize();
			}
			if (featurePrize != null && !level.getMapCell(getPosition()).isSolid())
				if (level.getMapCell(getPosition()).isShallowWater()) {
					level.addMessage("A " + FeatureFactory.getFactory().getDescriptionForID(featurePrize)
							+ " falls into the " + level.getMapCell(getPosition()).getDescription());
					level.addFeature(featurePrize, getPosition());
				} else
					level.addFeature(featurePrize, getPosition());

			if (getDefinition().isBleedable()) {
				Position runner = new Position(-1, -1, getPosition().z);
				for (runner.x = -1; runner.x <= 1; runner.x++)
					for (runner.y = -1; runner.y <= 1; runner.y++)
						if (Util.chance(70))
							getLevel().addBlood(Position.add(getPosition(), runner), Util.rand(0, 1));
			}

			die();
			level.getPlayer().addScore(getDefinition().getScore());
			level.getPlayer().addXP(getDefinition().getScore());
			level.getPlayer().getGameSessionInfo().addDeath(getDefinition());
		}
	}

	public int getScore() {
		return getDefinition().getScore();

	}

	public boolean isDead() {
		return hits <= 0;
	}

	@Override
	public String getDescription() {
		// This may be flavored with specific monster daya

		return getDefinition().getDescription() + (hasCounter(Consts.C_MONSTER_CHARM) ? " C " : "");
	}

	private MonsterDefinition getDefinition() {
		if (definition == null) {
			if (this instanceof NPC)
				definition = NPC.NPC_MONSTER_DEFINITION;
			else
				definition = MonsterFactory.getFactory().getDefinition(defID);
		}
		return definition;
	}

	public boolean canSwim() {
		return getDefinition().isCanSwim();
	}

	public boolean isUndead() {
		return getDefinition().isUndead();
	}

	public boolean isEthereal() {
		return getDefinition().isEthereal();
	}

	public int getHits() {
		return hits;
	}

	public Monster(MonsterDefinition md) {
		definition = md;
		defID = md.getID();
		// selector = md.getDefaultSelector();
		selector = md.getDefaultSelector().derive();

		hits = md.getMaxHits();
		maxHits = md.getMaxHits();
	}

	/*
	 * public ActionSelector getSelector(){ return selector; //return
	 * definition.getDefaultSelector(); }
	 */

	public String getFeaturePrize() {
		return featurePrize;
	}

	public void setFeaturePrize(String value) {
		featurePrize = value;
	}

	public int getAttack() {
		return getDefinition().getAttack();
	}

	public int getLeaping() {
		return getDefinition().getLeaping();
	}

	public boolean waitsPlayer() {
		return false;
	}

	private void setPrize() {
		Player p = level.getPlayer();
		String[] prizeList = null;

		if (p.deservesMUpgrade()) {
			setFeaturePrize("MUPGRADE");
			return;
		}

		if (p.deservesUpgrade() && Util.chance(50))
			setFeaturePrize("UPGRADE");

		if (Util.chance(60))
			return;

		if (p.getPlayerClass() == Player.CLASS_VAMPIREKILLER) {
			if (Util.chance(20)) {
				prizeList = Util.getMysticWeapon(p, 20);
			} else if (Util.chance(50))
				prizeList = Util.getOtherPrize(40, 10, true);
		} else {
			prizeList = Util.getOtherPrize(50, 50, true);
		}

		if (prizeList != null)
			setFeaturePrize(Util.randomElementOf(prizeList));
	}

	@Override
	public void die() {
		super.die();
		level.removeMonster(this);
		if (getAutorespawncount() > 0) {
			Emerger em = new Emerger(MonsterFactory.getFactory().buildMonster(getDefinition().getID()), getPosition(),
					getAutorespawncount());
			level.addActor(em);
			em.setSelector(new EmergerAI());
			em.setLevel(getLevel());
		}
	}

	public void setVisible(boolean value) {
		visible = value;
	}

	public boolean isVisible() {
		return visible;
	}

	public int getAttackCost() {
		return getDefinition().getAttackCost();
	}

	public int getWalkCost() {
		return getDefinition().getWalkCost();
	}

	public String getID() {
		return getDefinition().getID();
	}

	public int getEvadeChance() {
		return getDefinition().getEvadeChance();
	}

	public String getEvadeMessage() {
		return getDefinition().getEvadeMessage();
	}

	public int getAutorespawncount() {
		return getDefinition().getAutorespawnCount();
	}

	public boolean tryMagicHit(Player attacker, int magicalDamage, int magicalHit, boolean showMsg, String attackDesc,
			boolean isWeaponAttack, Position attackOrigin) {
		int hitChance = 100 - getEvadeChance();
		hitChance = (int) Math.round((hitChance + magicalHit) / 2.0d);
		int penalty = 0;
		if (isWeaponAttack) {
			penalty = Position.distance(getPosition(), attackOrigin) / 4;
			if (attacker.getWeapon().isHarmsUndead() && isUndead())
				magicalDamage *= 2;
			attacker.increaseWeaponSkill(attacker.getWeapon().getDefinition().getWeaponCategory());

		}

		magicalDamage -= penalty;
		int evasion = 100 - hitChance;

		if (evasion < 0)
			evasion = 0;

		if (hasCounter(Consts.C_MONSTER_CHARM))
			setCounter(Consts.C_MONSTER_CHARM, 0);
		if (hasCounter("SLEEP"))
			evasion = 0;
		// see if evades it
		if (Util.chance(evasion)) {
			if (showMsg)
				level.addMessage("The " + getDescription() + " evades the " + attackDesc + "!");
			return false;
		} else {
			if (hasCounter("SLEEP")) {
				level.addMessage("You wake up the " + getDescription() + "!");
				setCounter("SLEEP", 0);
			}
			int baseDamage = magicalDamage;
			double damageMod = 1;
			StringBuilder hitDesc = determineHitDesc(attackDesc, baseDamage, damageMod);

			damage(hitDesc, (int) (baseDamage * damageMod));
			if (showMsg)
				level.addMessage(hitDesc.toString());
			return true;
		}
	}

	private StringBuilder determineHitDesc(String attackDesc, int baseDamage, double damageMod) {
		StringBuilder hitDesc = new StringBuilder();
		int damage = (int) (baseDamage * damageMod);
		double percent = (double) damage / (double) getDefinition().getMaxHits();
		if (percent > 1.0d)
			hitDesc.append("The ").append(attackDesc).append(" whacks the ").append(getDescription())
					.append(" apart!!");
		else if (percent > 0.7d)
			hitDesc.append("The ").append(attackDesc).append(" smashes the ").append(getDescription()).append("!");
		else if (percent > 0.5d)
			hitDesc.append("The ").append(attackDesc).append(" grievously hits the ").append(getDescription())
					.append("!");
		else if (percent > 0.3d)
			hitDesc.append("The ").append(attackDesc).append(" hits the ").append(getDescription()).append(".");
		else
			hitDesc.append("The ").append(attackDesc).append(" barely scratches the ").append(getDescription())
					.append("...");
		return hitDesc;
	}

	public String getLongDescription() {
		return getDefinition().getLongDescription();

	}

	public Monster getEnemy() {
		return enemy;
	}

	public void setEnemy(Monster enemy) {
		this.enemy = enemy;
	}

	/** Returns the direction in which the nearest monster is seen */
	public int stareMonster() {
		Monster nearest = getNearestMonster();
		if (nearest == null)
			return -1;
		else
			return stareMonster(getNearestMonster());
	}

	public Monster getNearestMonster() {
		VMonster monsters = level.getMonsters();
		Monster nearMonster = null;
		int minDist = 150;
		for (int i = 0; i < monsters.size(); i++) {
			Monster monster = monsters.get(i);
			int distance = Position.flatDistance(getPosition(), monster.getPosition());
			if (monster != this && distance < minDist) {
				minDist = distance;
				nearMonster = monster;
			}
		}
		return nearMonster;
	}

	public int stareMonster(Monster who) {
		if (who.getPosition().z != getPosition().z)
			return -1;
		if (Position.flatDistance(who.getPosition(), getPosition()) <= getDefinition().getSightRange()) {
			Position pp = who.getPosition();
			if (pp.x == getPosition().x) {
				if (pp.y > getPosition().y) {
					return Action.DOWN;
				} else {
					return Action.UP;
				}
			} else if (pp.y == getPosition().y) {
				if (pp.x > getPosition().x) {
					return Action.RIGHT;
				} else {
					return Action.LEFT;
				}
			} else if (pp.x < getPosition().x) {
				if (pp.y > getPosition().y)
					return Action.DOWNLEFT;
				else
					return Action.UPLEFT;
			} else {
				if (pp.y > getPosition().y)
					return Action.DOWNRIGHT;
				else
					return Action.UPRIGHT;
			}
		}
		return -1;
	}

	public boolean seesPlayer() {
		if (wasSeen()) {
			Line sight = new Line(getPosition(), level.getPlayer().getPosition());
			Position point = sight.next();
			while (!point.equals(level.getPlayer().getPosition())) {
				if (level.getMapCell(point) != null && level.getMapCell(point).isOpaque()) {
					return false;
				}
				point = sight.next();
				if (!level.isValidCoordinate(point))
					return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean tryHit(Monster attacker) {
		setEnemy(attacker);
		int evasion = getEvadeChance();
		if (hasCounter("SLEEP"))
			evasion = 0;
		// see if evades it
		int weaponAttack = attacker.getDefinition().getAttack();
		if (Util.chance(evasion)) {
			level.addMessage("The " + getDescription() + " dodges the " + attacker.getDescription() + " attack!");
			return false;
		} else {
			if (hasCounter(Consts.C_MONSTER_SLEEP)) {
				level.addMessage("The " + attacker.getDescription() + " wakes up the " + getDescription() + "!");
				setCounter(Consts.C_MONSTER_SLEEP, 0);
			}
			int baseDamage = weaponAttack;
			double damageMod = 1;

			StringBuilder hitDesc = new StringBuilder();
			int damage = (int) (baseDamage * damageMod);
			double percent = (double) damage / (double) getDefinition().getMaxHits();
			if (percent > 1.0d)
				hitDesc.append("The ").append(attacker.getDescription()).append(" whacks the ").append(getDescription())
						.append(" apart!!");
			else if (percent > 0.7d)
				hitDesc.append("The ").append(attacker.getDescription()).append(" smashes the ")
						.append(getDescription()).append("!");
			else if (percent > 0.5d)
				hitDesc.append("The ").append(attacker.getDescription()).append(" grievously hits the ")
						.append(getDescription()).append("!");
			else if (percent > 0.3d)
				hitDesc.append("The ").append(attacker.getDescription()).append(" hits the ").append(getDescription())
						.append(".");
			else
				hitDesc.append("The ").append(attacker.getDescription()).append(" barely scratches the ")
						.append(getDescription()).append("...");

			damage(hitDesc, (int) (baseDamage * damageMod));
			level.addMessage(hitDesc.toString());
			return true;
		}
	}

	public int getMaxHits() {
		return getDefinition().getMaxHits();
	}

	public boolean isFlying() {
		return getDefinition().isCanFly();
	}

	public void recoverHits() {
		hits = maxHits;
	}

}
package crl.action.monster;

import sz.util.Debug;
import sz.util.Line;
import sz.util.Position;
import crl.action.Action;
import crl.game.SFXManager;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Damage;
import crl.player.Player;
import crl.ui.effects.EffectFactory;

public class MonsterMissile extends Action{
	private int range;
	private String message;
	private String effectType;
	private String effectID;
	private String statusEffect;
	private String effectWav;
	private int damage;
	private String type;
	
	public String getEffectWav(){
		return effectWav;
	}
	public static final String TYPE_AXE = "AXE", TYPE_STRAIGHT = "STRAIGHT", TYPE_DIRECT = "DIRECT";
	
	/*public void set(String pStatusEffect, int pRange, String pMessage, String pEffectType, String pEffectID){
		range = pRange;
		statusEffect = pStatusEffect;
		message = pMessage;
		effectType = pEffectType;
		effectID = pEffectID;
		damage = 0;
	}*/
	
	public void set(String pType, String pStatusEffect, int pRange, String pMessage, String pEffectType, String pEffectID, int damage, String pEffectWav){
		type = pType;
		range = pRange;
		statusEffect = pStatusEffect;
		message = pMessage;
		effectType = pEffectType;
		effectID = pEffectID;
		effectWav =  pEffectWav;
		this.damage = damage;
	}

	
	public String getID(){
		return "MONSTER_MISSILE";
	}
	
	@Override
	public boolean needsPosition(){
		return true;
	}

	public void execute(){
		Debug.doAssert(performer instanceof Monster, "Someone not a monster tried to throw a bone");
		Monster aMonster = (Monster) performer;
        Level aLevel = performer.getLevel();
        Player aPlayer = aLevel.getPlayer();
        //Cell playerCell = aLevel.getMapCell(aPlayer.getPosition());
        //Cell monsterCell = aLevel.getMapCell(aMonster.getPosition());
        int targetDirection = solveDirection(performer.getPosition(), targetPosition);
        if (effectWav != null){
        	SFXManager.play(effectWav);
        }
        switch (effectType) {
            case "beam":
                drawEffect(EffectFactory.getSingleton().createDirectedEffect(aMonster.getPosition(), targetPosition, effectID, range));
                break;
            case "melee":
                drawEffect(EffectFactory.getSingleton().createDirectionalEffect(aMonster.getPosition(), targetDirection, range, effectID));
                break;
            case "missile":
                drawEffect(EffectFactory.getSingleton().createDirectedEffect(aMonster.getPosition(), targetPosition, effectID, range));
                break;
            case "directionalmissile":
                drawEffect(EffectFactory.getSingleton().createDirectedEffect(aMonster.getPosition(), targetPosition, effectID, range));
                break;
        }
        Line line = new Line(aMonster.getPosition(), targetPosition);
        boolean hits = false;
        for (int i=0; i<range; i++){
			Position destinationPoint = line.next();
			if (aPlayer.getPosition().equals(destinationPoint)){
				int penalty = Position.distance(aMonster.getPosition(), aPlayer.getPosition())/4;
				int attack = aMonster.getAttack();
				attack -= penalty;
				if (attack < 1)
					attack = 1;
                switch (type) {
                    case TYPE_DIRECT:
                        hits = true;
                        break;
                    case TYPE_STRAIGHT:
                        //if (monsterCell.getHeight() == playerCell.getHeight()){
                        if (aMonster.getStandingHeight() == aPlayer.getStandingHeight()) {
                            hits = true;
                        } else {
                            hits = false;
                        }
                        break;
                    case TYPE_AXE:
                        if (i > (range / 4) && i < (3 * (range / 4))) {
                            //if (playerCell.getHeight() == monsterCell.getHeight()+2 || playerCell.getHeight() == monsterCell.getHeight()+3){
                            if (aMonster.getStandingHeight() + 2 == aPlayer.getStandingHeight() || aMonster.getStandingHeight() + 3 == aPlayer.getStandingHeight()) {
                                hits = true;
                            } else {
                                hits = false;
                            }
                        } else {
                            if (aMonster.getStandingHeight() == aPlayer.getStandingHeight()) {
                                hits = true;
                            } else {
                                hits = false;
                            }
                        }
                        break;
                }
				if (hits){
					aLevel.addBlood(destinationPoint, 1);
					if (aPlayer.damage("The "+aMonster.getDescription()+ " hits you!", aMonster, new Damage((damage==0?aMonster.getAttack():damage), false))) {
						if (statusEffect != null){
                            switch (statusEffect) {
                                case Player.STATUS_STUN:
                                    aLevel.addMessage("You are stunned!");
                                    aPlayer.setStun(8);
                                    break;
                                case Player.STATUS_POISON:
                                    aLevel.addMessage("Your blood is poisoned!");
                                    aPlayer.setPoison(15);
                                    break;
                                case Player.STATUS_PETRIFY:
                                    aLevel.addMessage("Your skin petrifies!");
                                    aPlayer.setPetrify(10);
                                    break;
                            }
						}
					}
				}
				break;
			}
		}
		aLevel.addMessage("The "+aMonster.getDescription()+" "+message+".");
	}

	@Override
	public String getPromptPosition() {
        return super.getPromptPosition();
    }
	
	private int solveDirection(Position old, Position newP){
		if (newP.x() == old.x()){
			if (newP.y() > old.y()){
				return Action.DOWN;
			} else {
                 return Action.UP;
			}
		} else
		if (newP.y() == old.y()){
			if (newP.x() > old.x()){
				return Action.RIGHT;
			} else {
				return Action.LEFT;
			}
		} else
		if (newP.x() < old.x()){
			if (newP.y() > old.y())
				return Action.DOWNLEFT;
			else
				return Action.UPLEFT;
		} else {
            if (newP.y() > old.y())
				return Action.DOWNRIGHT;
			else
				return Action.UPRIGHT;
		}
	}

	@Override
	public int getCost(){
		Monster m = (Monster) performer;
		if (m.getAttackCost() == 0){
			Debug.say("quickie monster");
			return 10;
		}
		return m.getAttackCost();
	}

}
package crl.action.weapon;

import sz.util.Position;
import crl.action.Action;
import crl.actor.Actor;
import crl.feature.Feature;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;

public class FinalSlash extends Action {
private static final long serialVersionUID = 1L;
	public String getID(){
		return "FINAL_SLASH";
	}
	
	@Override
	public boolean needsDirection(){
		return true;
	}

	@Override
	public String getPromptDirection(){
		return "Where do you want to slash at?";
	}

	public void execute(){
		Player aPlayer = (Player)performer;
		Level aLevel = aPlayer.getLevel();
		if (!checkHearts(10)){
	        aLevel.addMessage("You need more power!");
	        return;
	    }
   		int otherDir1 = 0;
		int otherDir2 = 0;
		switch (targetDirection){
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
		}
		Position directionVar = Action.directionToVariation(targetDirection);
		Position runner1 = Position.add(performer.getPosition(), Action.directionToVariation(otherDir1));
		Position runner2 = Position.add(performer.getPosition(), Action.directionToVariation(targetDirection));
		Position runner3 = Position.add(performer.getPosition(), Action.directionToVariation(otherDir2));
		for (int i = 0; i < 15; i++){
			if (!performer.getLevel().isWalkable(runner2)){
				runner2.add(Position.mul(directionVar, -1));
				break;
			}
			hit (runner1);
			hit (runner2);
			hit (runner3);
			runner1.add(directionVar);
			runner2.add(directionVar);
			runner3.add(directionVar);
		}
		if (!performer.getLevel().isWalkable(runner2)){
			runner2.add(Position.mul(directionVar, -1));
		}
		performer.setPosition(runner2);
	}

	private boolean hit (Position destinationPoint){
		StringBuilder message = new StringBuilder();
		Level aLevel = performer.getLevel();
        Player aPlayer = aLevel.getPlayer();
        Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
        if (checkIfDestroyable(destinationFeature)){
	       	message.append("You slash through the ").append(destinationFeature.getDescription());
			aLevel.addMessage(message.toString());
        	return true;
		}
        Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
        if (
			targetMonster != null &&
			!(targetMonster.isInWater() && targetMonster.canSwim())
			){
				message.append("You slash thru the ").append(targetMonster.getDescription());
				targetMonster.damageWithWeapon(message, 20+(int)(aPlayer.getWeaponAttack()*1.5));
	        	if (targetMonster.isDead()){
		        	message.append(", cutting it apart!");
				}
	        	if (targetMonster.wasSeen())
	        		aLevel.addMessage(message.toString());
				return true;
			}
		return false;
	}
	
	@Override
	public boolean canPerform(Actor a){
		Player aPlayer = (Player) a;
        Level aLevel = performer.getLevel();
        if (aPlayer.getHearts() < 10){
        	invalidationMessage = "You need more energy!";
            return false;
		}
        return true;
	}
}
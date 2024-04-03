package crl.action.weapon;

import sz.util.Position;
import crl.action.Action;
import crl.actor.Actor;
import crl.feature.Feature;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;

public class SpinningSlice extends Action {
private static final long serialVersionUID = 1L;
	public String getID(){
		return "SpinningSlice";
	}
	
	public void execute(){
		Player aPlayer = (Player) performer;
        Level aLevel = performer.getLevel();
        if (!checkHearts(8))
        	return;
  		if (aPlayer.getWeapon() == null){
  			aLevel.addMessage("You can't slice without a proper weapon");
  			return;
  		}

  		Position runner = new Position(aPlayer.getPosition().x-1, aPlayer.getPosition().y-1, aPlayer.getPosition().z);
        for (; runner.x <= aPlayer.getPosition().x+1; runner.x++){
        	for (; runner.y <= aPlayer.getPosition().y+1; runner.y++) {
				Cell destinationCell = aLevel.getMapCell(runner);
				//aLevel.addBlood(runner, 8);
	        	if (destinationCell == null)
	        		continue;
				hit(runner, aLevel, aPlayer);
			}
			runner.y = aPlayer.getPosition().y-1;
		}
	}

	private void hit(Position destinationPoint, Level aLevel, Player player){
		StringBuilder message = new StringBuilder();
		Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
		if (destinationFeature != null && destinationFeature.isDestroyable()){
			message.append("You slice the ").append(destinationFeature.getDescription());
			Feature prize = destinationFeature.damage(player, player.getWeapon().getAttack());
			if (prize != null){
		       	message.append(", and cut it apart!");
			}
			aLevel.addMessage(message.toString());
		}

		Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
		message = new StringBuilder();
		if (
			targetMonster != null &&
			!(targetMonster.isInWater() && targetMonster.canSwim())
		){
			message.append("You slice the ").append(targetMonster.getDescription());
			targetMonster.damageWithWeapon(message, player.getWeaponAttack()+player.getAttack());
        	if (targetMonster.isDead()){
	        	message.append(", and cut it apart!");
				//performer.getLevel().removeMonster(targetMonster);
			}
        	if (targetMonster.wasSeen())
        		aLevel.addMessage(message.toString());
		}
	}
	
	@Override
	public boolean canPerform(Actor a){
		Player aPlayer = (Player) a;
        Level aLevel = performer.getLevel();
        if (aPlayer.getHearts() < 8){
        	invalidationMessage = "You need more energy!";
            return false;
		}
        return true;
	}
}
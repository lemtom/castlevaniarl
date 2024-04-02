package crl.action.renegade;

import crl.action.Action;
import crl.actor.Actor;
import crl.player.Damage;
import crl.player.Player;

public class MinorJinx extends Action{
	public String getID(){
		return "MINOR_JINX";
	}
	
	@Override
	public String getSFX() {
        return super.getSFX();
    }
	
	@Override
	public int getCost(){
		Player p = (Player) performer;
		return (int)(p.getCastCost() * 1.1);
	}
	
	public void execute(){
		Player aPlayer = (Player)performer;
		int recover = 3 + aPlayer.getSoulPower();
		aPlayer.addHearts(recover);
		aPlayer.selfDamage("You exchange vitality for power!! (+"+recover+")", Player.DAMAGE_JINX, new Damage(5, true));
	}
	
	@Override
	public boolean canPerform(Actor a){
		Player aPlayer = (Player) a;
        if (aPlayer.getHits() <= 5){
        	invalidationMessage = "That would be suicidal!";
            return false;
		}
        return true;
	}
}
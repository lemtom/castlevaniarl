package crl.action.manbeast;

import crl.action.HeartAction;
import crl.level.Level;
import crl.player.Consts;
import crl.player.Player;

public class PowerBlow extends HeartAction {
private static final long serialVersionUID = 1L;
	public int getHeartCost() {
		return 3;
	}
	
	public String getID(){
		return "PowerBlow";
	}
	
	@Override
	public int getCost(){
		Player p = (Player) performer;
		return (int)(p.getAttackCost() * 1.1);
	}
	
	@Override
	public void execute(){
		super.execute();
		Player aPlayer = (Player)performer;
		Level aLevel = aPlayer.getLevel();
		aLevel.addMessage("WAARGH!!! You build up your power!");
		aPlayer.setCounter(Consts.C_POWERBLOW, 5);
	}
}
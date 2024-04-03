package crl.action.invoker;

import crl.action.HeartAction;
import crl.level.Level;
import crl.player.Consts;
import crl.player.Player;

public class Turtle extends HeartAction {
private static final long serialVersionUID = 1L;
	public int getHeartCost() {
		return 5;
	}
	
	public String getID(){
		return "Turtle";
	}
	
	@Override
	public String getSFX(){
		return "wav/turtleCry.wav";
	}
	
	@Override
	public int getCost(){
		Player p = (Player) performer;
		return (int)(p.getCastCost() * 1.1);
	}
	
	@Override
	public void execute(){
		super.execute();
		Player aPlayer = (Player)performer;
		Level aLevel = aPlayer.getLevel();
		aLevel.addMessage("A cute turtle soul surrounds you");
		aPlayer.setCounter(Consts.C_TURTLESHELL, 50+aPlayer.getSoulPower()*2);
	}
}
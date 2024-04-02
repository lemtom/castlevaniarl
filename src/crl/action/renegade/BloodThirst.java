package crl.action.renegade;

import crl.action.HeartAction;
import crl.player.Consts;
import crl.player.Player;

public class BloodThirst extends HeartAction{
	public String getID(){
		return "BloodThirst";
	}
	
	public int getHeartCost() {
		return 10;
	}
	
	@Override
	public String getSFX(){
		return "wav/alu_dark.wav";
	}
	
	@Override
	public void execute(){
		super.execute();
        Player aPlayer = (Player)performer;
		aPlayer.getLevel().addMessage("Your vampire instinct awakes!");
		aPlayer.setCounter(Consts.C_BLOOD_THIRST, 10+aPlayer.getSoulPower()*4);
	}
	
}
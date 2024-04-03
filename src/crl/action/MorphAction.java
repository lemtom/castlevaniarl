package crl.action;

import crl.actor.Actor;
import crl.player.Player;

public abstract class MorphAction extends HeartAction {
private static final long serialVersionUID = 1L;
	@Override
	public final void execute(){
		super.execute();
        Player aPlayer = (Player)performer;
		aPlayer.getLevel().addMessage(getMorphMessage());
		aPlayer.morph(getMorphID(), getMorphTime(), isSmallMorph(), isBigMorph(), getMorphStrength(), getMadChance());
	}
	
	public abstract String getMorphMessage();
	public abstract String getMorphID();
	public abstract int getMorphTime();
	public abstract boolean isSmallMorph();
	public abstract boolean isBigMorph();
	public abstract int getMadChance();
	public abstract int getMorphStrength();
	
	@Override
	public boolean canPerform(Actor a) {
		if (getPlayer(a).isSwimming()){
			invalidationMessage = "You can't morph here";
			return false;
		}
		return super.canPerform(a);
	}
	
}
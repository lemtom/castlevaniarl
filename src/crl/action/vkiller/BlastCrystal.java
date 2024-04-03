package crl.action.vkiller;

import crl.action.HeartAction;
import crl.level.Level;
import crl.player.Player;

public class BlastCrystal extends HeartAction {
private static final long serialVersionUID = 1L;
	public int getHeartCost() {
		return 5;
	}
	public String getID(){
		return "BLAST_CRYSTAL";
	}
	
	@Override
	public void execute(){
		super.execute();
		Player aPlayer = (Player) performer;
        Level aLevel = performer.getLevel();
        aLevel.addMessage("You release a mystic crystal!");
		aLevel.addSmartFeature("BLAST_CRYSTAL", performer.getPosition());
	}
	
	@Override
	public int getCost(){
		Player p = (Player) performer;
		return 25 / (p.getShotLevel()+1);
	}
}
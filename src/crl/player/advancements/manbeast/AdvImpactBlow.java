package crl.player.advancements.manbeast;

import crl.player.Player;
import crl.player.advancements.Advancement;

public class AdvImpactBlow extends Advancement {
private static final long serialVersionUID = 1L;
	public String getName(){
		return "Power Blow";
	}
	
	public void advance(Player p) {
		p.setFlag("SKILL_POWERBLOW", true);
		p.setFlag(getID(), true);
	}

	public String getID() {
		return "ADV_POWERBLOW";
	}

	public String[] requirements = new String[]{
	};
	
	public String[] bans = new String[]{
	};
	
	public String[] getRequirements() {
		return requirements;
	}

	public String getDescription(){
		return "Builds up energy to perform a powerful blow";
	}
	
	@Override
	public String[] getBans() {
		return bans;
	}
}

package crl.player.advancements.renegade;

import crl.player.Player;
import crl.player.advancements.Advancement;

public class AdvWolfMorph extends Advancement {
private static final long serialVersionUID = 1L;
	public String getName(){
		return "Lupine Metamorphosis";
	}
	
	public void advance(Player p) {
		p.setFlag("SKILL_WOLFMORPH", true);
		p.setFlag(getID(), true);
	}

	public String getID() {
		return "ADV_WOLFMORPH";
	}

	public String[] requirements = new String[]{
		"ADV_MORPH"
	};
	
	public String[] getRequirements() {
		return requirements;
	}

	public String getDescription(){
		return "Turns into a quick and fierce wolf";
	}
}

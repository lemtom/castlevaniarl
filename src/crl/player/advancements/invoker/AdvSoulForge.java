package crl.player.advancements.invoker;

import crl.player.Player;
import crl.player.advancements.Advancement;

public class AdvSoulForge extends Advancement {
private static final long serialVersionUID = 1L;
	public String getName(){
		return "Soul Forge";
	}
	
	public void advance(Player p) {
		p.setFlag("SKILL_SOULFORGE", true);
		p.setFlag(getID(), true);
	}

	public String getID() {
		return "ADV_SOULFORGE";
	}

	public String[] requirements = new String[]{
		"ADV_KIND_SOUL"
	};
	
	public String[] getRequirements() {
		return requirements;
	}

	public String getDescription(){
		return "Raises Tame chance by 20%";
	}

}

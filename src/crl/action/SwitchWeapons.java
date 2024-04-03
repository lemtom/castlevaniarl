package crl.action;

import crl.item.Item;
import crl.player.Player;

public class SwitchWeapons extends Action {
	private static final long serialVersionUID = 1L;

	@Override
	public int getCost() {
		return 25;
	}

	public String getID() {
		return "SwitchWeapons";
	}

	public void execute() {
		Player aPlayer = (Player) performer;
		Item secondary = aPlayer.getSecondaryWeapon();
		if (secondary == null) {
			Item primary = aPlayer.getWeapon();
			aPlayer.setWeapon(null);
			aPlayer.getLevel().addMessage("You attack unarmed");
			if (primary != null) {
				aPlayer.setSecondaryWeapon(primary);
			}
			return;
		}
		Item primary = aPlayer.getWeapon();
		aPlayer.setWeapon(secondary);
		if (primary != null) {
			aPlayer.setSecondaryWeapon(primary);
			aPlayer.getLevel().addMessage(
					"You switch your " + primary.getDescription() + " for your " + secondary.getDescription());
		} else {
			aPlayer.setSecondaryWeapon(null);
			aPlayer.getLevel().addMessage("You equip your " + secondary.getDescription());
		}
	}

}
package crl.npc;

import crl.item.ItemFactory;
import crl.item.Merchant;
import crl.player.Player;
import crl.ui.UserInterface;
import sz.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class NPCFactory {
	private static final NPCFactory singleton = new NPCFactory();
	private final HashMap<String, NPCDefinition> definitions;
	private final ArrayList<String> hostages = new ArrayList<>();

	public static NPCFactory getFactory() {
		return singleton;
	}

	public NPC buildNPC(String id) {
		return new NPC(definitions.get(id));
	}

	public Merchant buildMerchant(int merchandiseType) {
		return new Merchant(definitions.get("MERCHANT"), merchandiseType);
	}

	public Hostage buildHostage() {
		Hostage ret = new Hostage(definitions.get(Util.randomElementOf(hostages)));
		if (UserInterface.getUI().getPlayer().getPlayerClass() != Player.CLASS_VAMPIREKILLER) {

			int artifactCategory = (int) (UserInterface.getUI().getPlayer().getPlayerLevel() / 6.0d);
			ret.setItemReward(ItemFactory.getItemFactory()
					.createWeapon(Util.randomElementOf(hostageArtifacts[artifactCategory]), ""));
		}
		return ret;
	}

	public NPCDefinition getDefinition(String id) {
		return definitions.get(id);
	}

	public void addDefinition(NPCDefinition definition) {
		definitions.put(definition.getID(), definition);
		if (definition.isHostage())
			hostages.add(definition.getID());
	}

	public NPCFactory() {
		definitions = new HashMap<>(40);
	}

	private static final String[][] hostageArtifacts = new String[][] { { "HOLBEIN_DAGGER", "SHOTEL" },
			{ "WEREBANE", "ALCARDE_SPEAR", "ETHANOS_BLADE" }, { "FIREBRAND", "GURTHANG", "HADOR" },
			{ "ICEBRAND", "MORMEGIL", "VORPAL_BLADE" }, { "GRAM", "CRISSAEGRIM" },
			{ "KAISER_KNUCKLE", "OSAFUNE", "MASAMUNE" } };
}
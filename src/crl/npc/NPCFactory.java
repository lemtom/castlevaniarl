package crl.npc;

import java.util.*;
import crl.ui.*;
import crl.player.*;
import crl.item.*;
import sz.util.*;

public class NPCFactory {
	private static final NPCFactory singleton = new NPCFactory();
	private HashMap<String, NPCDefinition> definitions;
	private ArrayList<String> hostages = new ArrayList<>();

	public static NPCFactory getFactory(){
		return singleton;
	}

	public NPC buildNPC (String id){
		return new NPC(definitions.get(id));
	}
	
	public Merchant buildMerchant(int merchandiseType){
		return new Merchant(definitions.get("MERCHANT"), merchandiseType);
	}
	
	public Hostage buildHostage(){
		Hostage ret = new Hostage(definitions.get(Util.randomElementOf(hostages)));
		if (UserInterface.getUI().getPlayer().getPlayerClass() != Player.CLASS_VAMPIREKILLER) {
			int artifactCategory = ((int) (UserInterface.getUI().getPlayer().getPlayerLevel() / 6.0d));
			ret.setItemReward(ItemFactory.getItemFactory().createWeapon(Util.randomElementOf(hostageArtifacts[artifactCategory]),""));
		}
		return ret;
	}

	public NPCDefinition getDefinition (String id){
		return definitions.get(id);
	}

	public void addDefinition(NPCDefinition definition){
		definitions.put(definition.getID(), definition);
		if (definition.isHostage())
			hostages.add(definition.getID());
	}

	public NPCFactory(){
		definitions = new HashMap<>(40);
	}
	
	private static String [][] hostageArtifacts = new String[][]{
		{"HOLBEIN_DAGGER", "SHOTEL"},
		{"WEREBANE", "ALCARDE_SPEAR", "ETHANOS_BLADE"},
		{"FIREBRAND", "GURTHANG", "HADOR"},
		{"ICEBRAND", "MORMEGIL", "VORPAL_BLADE"},
		{"GRAM", "CRISSAEGRIM"},
		{"KAISER_KNUCKLE", "OSAFUNE", "MASAMUNE"}
	};
}
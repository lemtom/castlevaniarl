package crl.action.monster;

import java.util.Vector;

import sz.util.Position;

import crl.action.Action;
import crl.game.SFXManager;
import crl.monster.Monster;
import crl.monster.VMonster;
import crl.npc.Hostage;
import crl.npc.NPC;
import crl.player.Damage;
import crl.ui.effects.EffectFactory;

public class MandragoraScream extends Action {
	public static final int SCREAM_RANGE = 8;
	public static final int SCREAM_DAMAGE = 20;
	public static final String SCREAM_WAV = "wav/scream.wav";

	
	@Override
	public String getID() {
		return "MANDRAGORA_SCREAM";
	}
	
	@Override
	public void execute() {
		if (performer.getFlag("MANDRAGORA_PULLED")){
			SFXManager.play(SCREAM_WAV);
			performer.getLevel().addMessage("* The Mandragora emits an earth-shattering scream!!! *");
			performer.getLevel().addEffect(EffectFactory.getSingleton().createLocatedEffect(performer.getPosition(), "SFX_MANDRAGORA_SCREAM"));

			

			VMonster monsters = performer.getLevel().getMonsters();
			Vector<Monster> removables = new Vector<Monster>();
			for (int i = 0; i < monsters.size(); i++){
				Monster monster = monsters.elementAt(i);
				if (monster == performer)
					continue;
				if (Position.flatDistance(performer.getPosition(), monster.getPosition()) < SCREAM_RANGE){
					if (monster instanceof NPC || monster instanceof Hostage){
						
					} else {
						StringBuffer messages = new StringBuffer("The "+monster.getDescription()+" hears the mandragora scream!");
						//targetMonster.damage(player.getWhipLevel());
						monster.damage(messages, SCREAM_DAMAGE);
						if (monster.wasSeen()){
							performer.getLevel().addMessage(messages.toString());
						}
			        	if (monster.isDead()){
			        		if (monster.wasSeen()){
			        			performer.getLevel().addMessage("The "+monster.getDescription()+" explodes in pain!");
			        		}
				        	removables.add(monster);
						}
					}
				}
			}
			monsters.removeAll(removables);
			
			if (Position.flatDistance(performer.getPosition(), performer.getLevel().getPlayer().getPosition()) < SCREAM_RANGE){
				performer.getLevel().getPlayer().damage("You hear the mandragora scream!", (Monster)performer, new Damage(SCREAM_DAMAGE, true));
			}
			
			performer.die();


		} else {
			performer.getLevel().addMessage("* A skeleton pulls out a mandragora root!!! *");
			performer.setFlag("MANDRAGORA_PULLED", true);
		}
		
	}
}

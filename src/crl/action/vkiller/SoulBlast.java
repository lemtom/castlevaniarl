package crl.action.vkiller;

import sz.util.Position;
import crl.action.HeartAction;
import crl.level.Level;
import crl.monster.VMonster;
import crl.player.Player;
import crl.ui.effects.EffectFactory;

public class SoulBlast extends HeartAction {
private static final long serialVersionUID = 1L;
	
	public String getID(){
		return "Soul Flame";
	}
	
	public int getHeartCost() {
		return 20;
	}
	
	@Override
	public void execute(){
		super.execute();
        Level aLevel = performer.getLevel();
        aLevel.addMessage("Soul Blast!");
        int damage = 50 + aLevel.getPlayer().getShotLevel()*2 + aLevel.getPlayer().getSoulPower()*3;
		Position blastPosition = performer.getPosition();
		aLevel.addEffect(EffectFactory.getSingleton().createLocatedEffect(blastPosition, "SFX_SOUL_BLAST"));
		
		VMonster monsters = aLevel.getMonsters();
		for (int i = 0; i < monsters.size(); i++){
			if (monsters.get(i).getPosition().z == performer.getPosition().z && Position.distance(monsters.get(i).getPosition(), performer.getPosition()) < 5){
				StringBuilder buff = new StringBuilder();
				if (monsters.get(i).wasSeen()) {
					buff.append("The ").append(monsters.get(i).getDescription()).append(" is hit by the holy blast!");
				}
				monsters.get(i).damage(buff, damage);
				aLevel.addMessage(buff.toString());
			}
		}

	}

	public String getPromptoPosition(){
		return "Where do you want to throw the vial?";
	}
	@Override
	public int getCost(){
		Player p = (Player) performer;
		return 25 / (p.getShotLevel()+1);
	}
	
}
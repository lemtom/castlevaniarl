package crl.action.renegade;

import sz.util.Position;
import crl.action.HeartAction;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;
import crl.ui.effects.EffectFactory;

public class SoulsStrike extends HeartAction {
private static final long serialVersionUID = 1L;
	public String getID(){
		return "Souls Strike";
	}
	
	public int getHeartCost() {
		return 8;
	}
	
	@Override
	public void execute(){
		super.execute();
		Level aLevel = performer.getLevel();
        Player aPlayer = aLevel.getPlayer();
		aLevel.addMessage("Three souls come from under your cape");
		
		for (int i = 0; i < 3; i++){
			Monster nearestMonster = aPlayer.getNearestMonster();
			if (nearestMonster == null || Position.flatDistance(nearestMonster.getPosition(), aPlayer.getPosition())>15){
			} else {
				StringBuilder buff = new StringBuilder();
				if (nearestMonster.wasSeen())
					buff.append("The soul impacts the ").append(nearestMonster.getDescription()).append("!");
				nearestMonster.damage(buff, 10+aPlayer.getSoulPower()*2);
				drawEffect(EffectFactory.getSingleton().createDirectedEffect(aPlayer.getPosition(), nearestMonster.getPosition(), "SFX_SOULSSTRIKE", Position.flatDistance(performer.getPosition(), nearestMonster.getPosition())));
				aLevel.addMessage(buff.toString());
			}
		}
	}

	@Override
	public int getCost(){
		Player p = (Player) performer;
		return (int)(p.getCastCost() * 1.5);
	}
	
}
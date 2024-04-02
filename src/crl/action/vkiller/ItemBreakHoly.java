package crl.action.vkiller;

import sz.util.Position;
import sz.util.Util;
import crl.action.HeartAction;
import crl.feature.Feature;
import crl.level.Level;
import crl.monster.Monster;
import crl.monster.VMonster;
import crl.player.Player;
import crl.ui.UserInterface;
import crl.ui.effects.EffectFactory;

public class ItemBreakHoly extends HeartAction{
	public int getHeartCost() {
		return 5;
	}
	public String getID(){
		return "Holy";
	}
	
	@Override
	public void execute(){
		Player aPlayer = (Player) performer;
        Level aLevel = performer.getLevel();
		aLevel.addMessage("You jump! You unleash a rain of holy water!");
		UserInterface.getUI().drawEffect(EffectFactory.getSingleton().createLocatedEffect(aPlayer.getPosition(), "SFX_HOLY_RAINSPLASH"));
		int damage = 6 + 
		getPlayer().getShotLevel()+ 
		getPlayer().getSoulPower();
		
		VMonster monsters = aLevel.getMonsters();
		for (int i = 0; i < monsters.size(); i++){
			
			if (monsters.get(i).getPosition().z == performer.getPosition().z && Position.distance(monsters.get(i).getPosition(), performer.getPosition()) < 4){
				StringBuffer buff = new StringBuffer();
				if (monsters.get(i).wasSeen()) {
					buff.append("The ").append(monsters.get(i).getDescription()).append(" is splashed with holy water!");
				}
				monsters.get(i).damage(buff, damage);
				aLevel.addMessage(buff.toString());
			}
		}
		UserInterface.getUI().refresh();
		
		int drops = Util.rand(20,40);

		for (int i = 0; i < drops; i++){
			int xdif = 5-Util.rand(0,10);
			int ydif = 5-Util.rand(0,10);
			Position destinationPoint = Position.add(aPlayer.getPosition(), new Position(xdif,ydif));
			if (aLevel.isValidCoordinate(destinationPoint)){
				aLevel.addEffect(EffectFactory.getSingleton().createLocatedEffect(destinationPoint, "SFX_HOLY_RAINDROP"));
				UserInterface.getUI().refresh();
				Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
	        	if (destinationFeature != null && destinationFeature.isDestroyable()){
					Feature prize = destinationFeature.damage(aPlayer, damage);
		        	if (prize != null){
		        		aLevel.addMessage ("The "+destinationFeature.getDescription()+" burns until consumption!");
					} else {
						aLevel.addMessage ("The "+destinationFeature.getDescription()+" burns.");
					}
				}
				Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
				if (targetMonster != null){
					StringBuffer buff = new StringBuffer();
					buff.append("The ").append(monsters.get(i).getDescription()).append(" is splashed with holy rain!");
					targetMonster.damage(buff, damage);
					aLevel.addMessage (buff.toString());
		        	if (targetMonster.isDead()){
		        		if (targetMonster.wasSeen())
							aLevel.addMessage ("The "+targetMonster.getDescription()+" catches in flame and is roasted!");
						performer.getLevel().removeMonster(targetMonster);
					} else {
						if (targetMonster.wasSeen())
							aLevel.addMessage ("The "+targetMonster.getDescription()+" catches in flame.");
					}
				}
			}
		}
	}

	@Override
	public int getCost(){
		Player p = (Player) performer;
		return (25 / (p.getShotLevel()+1));
	}

	@Override
	public String getSFX(){
		return "wav/breakpot.wav";
	}
}
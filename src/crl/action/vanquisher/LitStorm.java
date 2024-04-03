package crl.action.vanquisher;

import sz.util.Position;
import crl.action.HeartAction;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;
import crl.ui.effects.EffectFactory;

public class LitStorm extends HeartAction {
	private static final long serialVersionUID = 1L;

	public int getHeartCost() {
		return 10;
	}

	public String getID() {
		return "LitStorm";
	}

	@Override
	public void execute() {
		super.execute();
		Level aLevel = performer.getLevel();
		Player aPlayer = aLevel.getPlayer();
		aLevel.addMessage("You invoke the spell of lighting!");

		for (int i = 0; i < 3; i++) {
			Monster nearestMonster = aPlayer.getNearestMonster();
			if (!(nearestMonster == null
					|| Position.flatDistance(nearestMonster.getPosition(), aPlayer.getPosition()) > 10)) {
				StringBuilder buff = new StringBuilder();
				if (nearestMonster.wasSeen())
					buff.append("Lighting zaps the ").append(nearestMonster.getDescription()).append("!");
				nearestMonster.damage(buff, 5 + aPlayer.getSoulPower() * 2);
				aLevel.addMessage(buff.toString());
				drawEffect(EffectFactory.getSingleton().createDirectedEffect(aPlayer.getPosition(),
						nearestMonster.getPosition(), "SFX_LIT_SPELL",
						Position.flatDistance(performer.getPosition(), nearestMonster.getPosition())));

			}
		}
	}

	@Override
	public int getCost() {
		Player p = (Player) performer;
		return (int) (p.getCastCost() * 1.5);
	}

}
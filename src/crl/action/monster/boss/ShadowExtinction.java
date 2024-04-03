package crl.action.monster.boss;

import sz.util.Position;
import sz.util.Util;
import crl.action.Action;
import crl.feature.Feature;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Damage;
import crl.player.Player;
import crl.ui.effects.EffectFactory;

public class ShadowExtinction extends Action {
	private static final long serialVersionUID = 1L;

	public String getID() {
		return "SHADOW_EXTINCTION";
	}

	public void execute() {
		Level aLevel = performer.getLevel();
		aLevel.addMessage("Dracula Yells: 'Shadow Extinction!'");
		int sickles = Util.rand(2, 5);
		for (int i = 0; i < sickles; i++) {
			int xvar = Util.rand(-10, 10);
			int yvar = Util.rand(-10, 10);
			int xgo = performer.getPosition().x + xvar - 3;
			int ygo = performer.getPosition().y + yvar - 3;
            drawEffect(
					EffectFactory.getSingleton()
							.createLocatedEffect(new Position(xvar + performer.getPosition().x,
									yvar + performer.getPosition().y, performer.getPosition().z),
									"SFX_SHADOW_EXTINCTION"));
			for (int jx = xgo; jx <= xgo + 3; jx++)
				for (int jy = ygo; jy <= ygo + 3; jy++)
					hit(jx, jy, performer.getPosition().z);
		}
	}

	@Override
	public int getCost() {
		return 20;
	}

	private void hit(int x, int y, int z) {
		String message = "";
		Level aLevel = performer.getLevel();
		Player aPlayer = aLevel.getPlayer();
		Position destinationPoint = new Position(x, y, z);
		Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
		if (destinationFeature != null && destinationFeature.isDestroyable()) {
			message = "The " + destinationFeature.getDescription() + " is hit by dark energy!";
			Feature prize = destinationFeature.damage(aPlayer, 4);
			if (prize != null) {
				message += " and turned into dust!";
			}
			aLevel.addMessage(message);
		}
		if (destinationPoint.equals(aPlayer.getPosition())) {
			aPlayer.damage("You are hit by the dark energy!", (Monster) performer, new Damage(2, false));
		}
	}
}
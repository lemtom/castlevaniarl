package crl.action.vkiller;

import sz.util.Position;
import crl.action.Action;
import crl.action.HeartAction;
import crl.level.Level;
import crl.player.Player;

public class ItemBreakBlastCrystal extends HeartAction {
private static final long serialVersionUID = 1L;
	public String getID() {
		return "BLAST_CRYSTAL";
	}

	@Override
	public void execute() {
		super.execute();
		Level aLevel = performer.getLevel();
		aLevel.addMessage("You release a handful of mystic crystals!!");
		aLevel.addSmartFeature("BLAST_CRYSTAL", Position.add(performer.getPosition(), Action.VARUP));
		aLevel.addSmartFeature("BLAST_CRYSTAL", Position.add(performer.getPosition(), Action.VARDN));
		aLevel.addSmartFeature("BLAST_CRYSTAL", Position.add(performer.getPosition(), Action.VARLF));
		aLevel.addSmartFeature("BLAST_CRYSTAL", Position.add(performer.getPosition(), Action.VARRG));
	}

	@Override
	public int getCost() {
		Player p = (Player) performer;
		return 25 / (p.getShotLevel() + 1);
	}

	public int getHeartCost() {
		return 10;
	}

}
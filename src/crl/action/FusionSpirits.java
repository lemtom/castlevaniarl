package crl.action;

import crl.item.Item;
import crl.player.Player;
import sz.csi.textcomponents.MenuItem;

public class FusionSpirits extends Action {
private static final long serialVersionUID = 1L;
	public String getID() {
		return "Fusion Spirits";
	}

	@Override
	public boolean needsSpirits() {
		return true;
	}

	public void execute() {
		Player aPlayer = (Player) performer;
		if (targetMultiItems.size() != 2) {
			performer.getLevel().addMessage("You can only fusion two spirits");
			return;
		}
		/* Checks all the items to be spirits */
        for (MenuItem menuItem : targetMultiItems) {
            Item element = (Item) menuItem;
            if (!element.getDefinition().getID().endsWith("SPIRIT")) {
                performer.getLevel().addMessage(element.getDescription() + " is not an spirit");
                return;
            }
        }

		String principal = "";
		String secondary = "";
		String principalDs = "";
		String secondaryDs = "";

        for (MenuItem multiItem : targetMultiItems) {
            Item element = (Item) multiItem;
            String fx = element.getDefinition().getID();
            /* Looks for attribute spirits */
            if (fx.equals("VENUS_SPIRIT") || fx.equals("MERCURY_SPIRIT") || fx.equals("MARS_SPIRIT")) {
                principal = fx;
                principalDs = element.getDescription();
            } else {
                secondary = fx;
                secondaryDs = element.getDescription();
            }
        }
		if (principal.isEmpty() || secondary.isEmpty()) {
			performer.getLevel().addMessage("That fusion won't work");
			return;
		}

		performer.getLevel().addMessage("You try to fusion " + principalDs + " with " + secondaryDs + "...");
		aPlayer.addHistoricEvent("Fusioned " + principalDs + " with " + secondaryDs);

        switch (principal) {
            case "VENUS_SPIRIT":
                switch (secondary) {
                    case "URANUS_SPIRIT":
                        aPlayer.reduceCastCost(5);
                        performer.getLevel().addMessage("Your spellcasting ability increases!");
                        break;
                    case "NEPTUNE_SPIRIT":
                        aPlayer.increaseHeartMax(7);
                        performer.getLevel().addMessage("You are able to hold more hearts!");
                        break;
                    case "JUPITER_SPIRIT":
                        aPlayer.increaseSoulPower(1);
                        performer.getLevel().addMessage("Your soul power increases!");
                        break;
                }
                break;
            case "MERCURY_SPIRIT":
                switch (secondary) {
                    case "URANUS_SPIRIT":
                        aPlayer.reduceWalkCost(5);
                        performer.getLevel().addMessage("You feel quicker!");
                        break;
                    case "NEPTUNE_SPIRIT":
                        aPlayer.increaseCarryMax(5);
                        performer.getLevel().addMessage("You feel able to carry more!");
                        break;
                    case "JUPITER_SPIRIT":
                        aPlayer.increaseEvadeChance(5);
                        performer.getLevel().addMessage("You feel more nimble!");
                        break;
                }
                break;
            case "MARS_SPIRIT":
                switch (secondary) {
                    case "URANUS_SPIRIT":
                        aPlayer.reduceAttackCost(5);
                        performer.getLevel().addMessage("You feel more able on combat!");
                        break;
                    case "NEPTUNE_SPIRIT":
                        aPlayer.increaseHitsMax(3);
                        performer.getLevel().addMessage("You feel hardy!");
                        break;
                    case "JUPITER_SPIRIT":
                        aPlayer.increaseAttack(1);
                        performer.getLevel().addMessage("You feel stronger!");
                        break;
                }
                break;
        }

        for (MenuItem targetMultiItem : targetMultiItems) {
            Item element = (Item) targetMultiItem;
            aPlayer.reduceQuantityOf(element);
        }
	}
}
package crl.action;

import sz.util.Position;
import sz.util.Util;
import crl.actor.Actor;
import crl.game.SFXManager;
import crl.item.ItemDefinition;
import crl.item.ItemFactory;
import crl.player.Consts;
import crl.player.Damage;
import crl.player.Player;
import crl.ui.UserInterface;

public class Use extends Action {
private static final long serialVersionUID = 1L;
	public String getID(){
		return "Use";
	}
	
	@Override
	public boolean needsItem(){
		return true;
    }

    @Override
    public String getPromptItem(){
    	return "What do you want to use?";
	}

	public void execute(){

		Player aPlayer = (Player) performer;
		ItemDefinition def = targetItem.getDefinition();
		String[] effect = def.getEffectOnUse().split(" ");
		
		if (def.getID().equals("SOUL_RECALL")){
			if (aPlayer.getHostage()!=null){
				UserInterface.getUI().showMessage("Abandon "+aPlayer.getHostage().getDescription()+"? [Y/N]");
				if (UserInterface.getUI().prompt()){
					aPlayer.abandonHostage();
				} else {
					return;
				}
			}
			SFXManager.play("wav/loutwarp.wav");
			aPlayer.informPlayerEvent(Player.EVT_GOTO_LEVEL, "TOWN0");
			// We need to properly relocate the player
			Position exit = aPlayer.getLevel().getExitFor("FOREST0");
			aPlayer.getLevel().setLevelNumber(0);
			aPlayer.landOn(Position.add(exit, new Position(-1,0,0)));
			aPlayer.reduceQuantityOf(targetItem);
			return;
		}
		
		if (def.getID().equals("OXY_HERB")){
			aPlayer.getLevel().addMessage("You bite the oxyherb. Air fills your breast!");
			if (aPlayer.isSwimming()){
				aPlayer.setCounter("OXYGEN", aPlayer.getBreathing());
			}
			aPlayer.reduceQuantityOf(targetItem);
			return;
		}
		
		if (def.getID().startsWith("ART_CARD_")){
			if (aPlayer.getLevel().getMapCell(aPlayer.getPosition()).getID().equals("WEIRD_MACHINE")){
				aPlayer.getLevel().addMessage("You insert the card into the machine!");
				aPlayer.setFlag("HAS_"+def.getID(), true);
				if (aPlayer.getFlag("HAS_ART_CARD_SOL") &&
						aPlayer.getFlag("HAS_ART_CARD_MOONS") &&
						aPlayer.getFlag("HAS_ART_CARD_DEATH") &&
						aPlayer.getFlag("HAS_ART_CARD_LOVE")){
					aPlayer.getLevel().addMessage("The machine opens. A wooden music box plays a mellow melody. You take the jukebox");
					aPlayer.addItem(ItemFactory.getItemFactory().createItem("JUKEBOX"));
				}
				aPlayer.reduceQuantityOf(targetItem);
				return;
			} else {
				performer.getLevel().addMessage("You raise the "+targetItem.getDescription()+" up high! Nothing happens...");
				return;
			}
		}
		
		if (effect[0].isEmpty()){
			performer.getLevel().addMessage("You don't find a use for the " +targetItem.getDescription());
			//aPlayer.addItem(targetItem);
			return;
		}                                
		for (int cmd = 0; cmd < effect.length; cmd+=2){
			String message = targetItem.getUseMessage();
			if (message.isEmpty())
				message = "You use the "+targetItem.getDescription();
            switch (effect[cmd]) {
                case "DAYLIGHT":
                    if (!aPlayer.getLevel().isDay()) {
                        aPlayer.getLevel().addMessage("The card fizzles in a blast of light!");
                        aPlayer.informPlayerEvent(Player.EVT_FORWARDTIME);
                    } else {
                        aPlayer.getLevel().addMessage("Nothing happens.");
                    }
                    break;
                case "MOONLIGHT":
                    if (aPlayer.getLevel().isDay()) {
                        aPlayer.getLevel().addMessage("The card fizzles in a puff of smoke!");
                        aPlayer.informPlayerEvent(Player.EVT_FORWARDTIME);
                    } else {
                        aPlayer.getLevel().addMessage("Nothing happens.");
                    }
                    break;
                case "INCREASE_DEFENSE":
                    aPlayer.increaseDefense(Integer.parseInt(effect[cmd + 1]));
                    break;
                case "INVINCIBILITY":
                    aPlayer.setInvincible(Integer.parseInt(effect[cmd + 1]));
                    break;
                case "ENERGY_FIELD":
                    aPlayer.setEnergyField(Integer.parseInt(effect[cmd + 1]));
                    break;
                case "READ_CLUE":
                    readClue(Integer.parseInt(effect[cmd + 1]));
                    break;
			/*if (effect[cmd].equals("LIGHT"))
				aPlayer.setCounter("LIGHT",Integer.parseInt(effect[cmd+1]));
			else*/
                case "INCREASE_JUMPING":
                    aPlayer.increaseJumping(Integer.parseInt(effect[cmd + 1]));
                    break;
                case "SETWHIP":
                    switch (effect[cmd + 1]) {
                        case "LIT":
                            aPlayer.setLitWhip();
                            break;
                        case "FLAME":
                            aPlayer.setFireWhip();
                            break;
                        case "THORN":
                            aPlayer.setThornWhip();
                            break;
                    }
                    break;
                case "HEAL":
                    if (effect[cmd + 1].equals("NP"))
                        aPlayer.heal();
                    else
                        aPlayer.recoverHits(Integer.parseInt(effect[cmd + 1]));
                    break;
                case "FIREBALL":
                    //aPlayer.setFireballWhip(Integer.parseInt(effect[cmd+1]));
                    aPlayer.setCounter(Consts.C_FIREBALL_WHIP, Integer.parseInt(effect[cmd + 1]));
                    break;
                case "RECOVER":
                    aPlayer.recoverHits(Integer.parseInt(effect[cmd + 1]));
                    break;
                case "DAMAGE":
                    if (aPlayer.isInvincible())
                        aPlayer.getLevel().addMessage("The damage is repelled!");
                    else
                        aPlayer.selfDamage(message, Player.DAMAGE_USING_ITEM, new Damage(Integer.parseInt(effect[cmd + 1]), false));
                    break;
                case "LIGHT":
                    aPlayer.setCounter("LIGHT", 200);
                    break;
            }
			performer.getLevel().addMessage(message);
		}
		if (def.isSingleUse())
			aPlayer.reduceQuantityOf(targetItem);


	}

	private void readClue(int level){
		performer.getLevel().addMessage("The page reads: \""+clues[level][Util.rand(0, clues.length-1)]+"\"");
	}

	private static final String [][] clues = new String[][]{
		{//Lv 0 clues
		"They say stairs are the safest place if you dont want to be moved",
		"The thorn bracelet exchanges pain for power",
		"Every 100 years, the castle rises as its lord revives",
		"They say the cross is the best weapon against Dracula",
		"Bibuti powder is a great weapon against slow enemies",
		"It is said that the powder can be used even in water",
		"The flame lasts less than the powder, but it is more powerful"
		},//Lv 1 clues
		{"..Secret treasures raised when I step on the spots..",
		"..And the books powered the mystic whip, giving it unlimited power..",
		"..Still, it was useless when used on the major monsters, as they were exempt from time..",
		"..And I discovered, the midget was unbeatable, but there was a way...",
		"..but the snakes were unimportant and just a distraction..",
		".. there I saw, always, a pair on them on the dark caverns..",
		".. and the mystic axe, able to cut throw flying and spectres.."
		}, //Lv 2 clues
		{".. saw the Grim Reaper, saw the servant of Dracula..",
		".. and the weapon, the dark sickles to surround him..",
		".. finally, the void, and powered by the big stars..",
		".. and the menbeast were born, using the axe for the ancient skill...",
		".. and there was a pair of rings, and the sacred vanquisher sliced through time..",
		".. and the ultimate whip, born from the crystals..",
		".. confronted the ancient count face to face, certain death.."
		}
	};

	@Override
	public boolean canPerform(Actor a) {
		return !((Player)a).isMorphed();
	}
	
	@Override
	public String getInvalidationMessage() {
		return "You can't use your items right now!";
	}
}
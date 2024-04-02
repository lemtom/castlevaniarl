package crl.action;

import crl.level.Level;
import crl.player.Player;

public class Drop extends Action{
	public String getID(){
		return "Drop";
	}
	
	@Override
	public boolean needsItem(){
		return true;
    }

    @Override
    public String getPromptItem(){
    	return "What do you want to drop?";
	}

	public void execute(){
		Level aLevel = performer.getLevel();
		aLevel.addMessage("You drop a "+targetItem.getDescription());
		((Player)performer).reduceQuantityOf(targetItem);
		aLevel.addItem(performer.getPosition(), targetItem);			
	}
}
package crl.action.monster.boss;

import crl.action.Action;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Damage;

public class MummyStrangle extends Action {
private static final long serialVersionUID = 1L;
	public String getID(){
		return "MUMMY_STRANGLE";
	}
	
	public void execute(){
        Level aLevel = performer.getLevel();
        aLevel.getPlayer().damage("Akmodan strangles you!", (Monster)performer, new Damage(6, false));
	}
}
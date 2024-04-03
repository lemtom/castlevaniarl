package crl.action.invoker;

public class InvokeTurtle extends SummonSkill {
private static final long serialVersionUID = 1L;
	public int getHeartCost() {
		return 5;
	}
	
	public String getID() {
		return "Invoke Turtle";
	}
	
	public String getMonsterID() {
		return "S_TURTLE";
	}
	
	@Override
	public String getSFX(){
		return "wav/turtleCry.wav";
	}
	
	@Override
	public int getHitBonus() {
		return 2*getPlayer().getSoulPower();
	}
}

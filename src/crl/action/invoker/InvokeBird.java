package crl.action.invoker;

public class InvokeBird extends SummonSkill {
private static final long serialVersionUID = 1L;
	public int getHeartCost() {
		return 5;
	}
	
	public String getID() {
		return "Invoke Bird";
	}
	
	public String getMonsterID() {
		return "S_BIRD";
	}
	
	@Override
	public int getHitBonus() {
		return 2*getPlayer().getSoulPower();
	}
}

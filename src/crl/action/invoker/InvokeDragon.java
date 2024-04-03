package crl.action.invoker;

public class InvokeDragon extends SummonSkill {
private static final long serialVersionUID = 1L;
	public int getHeartCost() {
		return 8;
	}
	
	public String getID() {
		return "Invoke Dragon";
	}
	
	public String getMonsterID() {
		return "S_DRAGON";
	}
	
	@Override
	public int getHitBonus() {
		return 5*getPlayer().getSoulPower();
	}
}

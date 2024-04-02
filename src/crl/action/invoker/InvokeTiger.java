package crl.action.invoker;

public class InvokeTiger extends SummonSkill{
	public int getHeartCost() {
		return 5;
	}
	
	public String getID() {
		return "Invoke Tiger";
	}
	
	public String getMonsterID() {
		return "S_TIGER";
	}

	@Override
	public String getSFX(){
		return "wav/tigerGrowl.wav";
	}
	
	@Override
	public int getHitBonus() {
		return 3*getPlayer().getSoulPower();
	}
}

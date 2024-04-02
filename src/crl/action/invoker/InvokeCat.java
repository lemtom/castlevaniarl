package crl.action.invoker;

public class InvokeCat extends SummonSkill{
	public int getHeartCost() {
		return 5;
	}
	
	public String getID() {
		return "Invoke Cat";
	}
	
	public String getMonsterID() {
		return "S_CAT";
	}
	
	@Override
	public String getSFX(){
		return "wav/kitty.wav";
	}
	
	@Override
	public int getHitBonus() {
		return 2*getPlayer().getSoulPower();
	}
}

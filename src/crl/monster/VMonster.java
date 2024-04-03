package crl.monster;

import java.util.*;

import sz.util.*;

public class VMonster implements java.io.Serializable{

	private static final long serialVersionUID = -8389021604207397015L;
	private ArrayList<Monster> monsters;

	public void addMonster(Monster what){
		monsters.add(what);
    }

	public Monster get(int i){
		return monsters.get(i);
	}
	
	public boolean contains(Monster who){
		return monsters.contains(who);
	}

	public void removeAll(Collection<Monster> c){
		monsters.removeAll(c);
	}


    public Monster getMonsterAt(Position p){
        for (Monster monster : monsters)
            if (monster.getPosition().equals(p))
                return monster;
		return null;
	}

	public VMonster(int size){
		monsters = new ArrayList<>(size);
	}

	public List<Monster> getArrayList(){
		return monsters;
	}

	public void remove(Object o){
		monsters.remove(o);
	}

	public int size(){
		return monsters.size();
	}
	
	public void removeAll(){
		monsters.clear();
	}

}
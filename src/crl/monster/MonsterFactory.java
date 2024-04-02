package crl.monster;

import java.util.*;

import crl.level.Level;
import crl.levelgen.MonsterSpawnInfo;
import crl.ui.AppearanceFactory;

import sz.util.*;

public class MonsterFactory {
	private static final MonsterFactory singleton = new MonsterFactory();

	private Hashtable<String, MonsterDefinition> definitions;
	private ArrayList<MonsterDefinition> vDefinitions = new ArrayList<>(50);

	public static MonsterFactory getFactory(){
		return singleton;
	}

	public Monster buildMonster (String id){
		return new Monster(definitions.get(id));
	}

	public MonsterDefinition getDefinition (String id){
		return definitions.get(id);
	}

	/*public void addDefinition(MonsterDefinition definition){
		definitions.put(definition.getID(), definition);
	}*/

	public MonsterFactory(){
		definitions = new Hashtable<>(40);
	}
	
	public void init(MonsterDefinition[] defs) {
        for (MonsterDefinition def : defs) {
            def.setAppearance(AppearanceFactory.getAppearanceFactory().getAppearance(def.getID()));
            definitions.put(def.getID(), def);
            vDefinitions.add(def);

        }
	}
	
	private int lastSpawnLocation;
	
	public int getLastSpawnPosition(){
		return lastSpawnLocation;
	}
	public Monster getMonsterForLevel(Level level){
		MonsterSpawnInfo[] spawnIDs = level.getSpawnInfo();
		if (spawnIDs == null || spawnIDs.length == 0)
			return null;
		while (true){
			int rand = Util.rand(0, spawnIDs.length-1);
			if (Util.chance(spawnIDs[rand].getFrequency())){
				lastSpawnLocation = spawnIDs[rand].getSpawnLocation();
				return new Monster(definitions.get(spawnIDs[rand].getMonsterID()));
			}
		}
	}

	public void printAppearances(){
		Enumeration<String> x = definitions.keys();
		while (x.hasMoreElements()){
			MonsterDefinition d = definitions.get(x.nextElement());
			Debug.say("Monstero "+ d.getDescription()+" app "+d.getAppearance());
		}
	}
}
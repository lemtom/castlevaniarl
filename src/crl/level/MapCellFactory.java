package crl.level;

import java.util.*;
import crl.game.*;

public class MapCellFactory {
	private static MapCellFactory singleton = new MapCellFactory();
	private HashMap<String, Cell> definitions;


    public static MapCellFactory getMapCellFactory(){
		return singleton;
    }

	public Cell getMapCell (String id) throws CRLException{
		Cell ret = definitions.get(id);
		if (ret != null)
			return ret;
		throw new CRLException("MapCellID " +id +" not found");
	}

	public void addDefinition(Cell definition){
		definitions.put(definition.getID(), definition);
	}

	public MapCellFactory(){
		definitions = new HashMap<>(40);
	}

	public void init(Cell[] defs) {
        for (Cell def : defs) definitions.put(def.getID(), def);
	}

}
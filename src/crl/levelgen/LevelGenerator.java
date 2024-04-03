package crl.levelgen;

import sz.util.Debug;
import sz.util.Position;
import sz.util.Util;
import crl.level.*;
import crl.feature.Feature;
import crl.item.*;
import crl.feature.FeatureFactory;
import crl.game.*;

public abstract class LevelGenerator {
	//public abstract Level generateLevel(String param, Dispatcher dispa);

	protected Cell[][] renderLevel(String[][] cellIds) throws CRLException{
		Debug.enterMethod(this, "renderLevel");
		MapCellFactory mcf = MapCellFactory.getMapCellFactory();
		Cell[][] ret = new Cell[cellIds.length][cellIds[0].length];
		for (int x = 0; x < cellIds.length; x++)
			for (int y = 0; y < cellIds[0].length; y++)
				if (cellIds[x][y].startsWith("F_"))
					ret[x][y] = mcf.getMapCell(cellIds[x][y].substring(2).split(" ")[0]);
				else
					ret[x][y] = mcf.getMapCell(cellIds[x][y]);
		Debug.exitMethod(ret);
		return ret;
	}
	
	protected void renderLevelFeatures(Level level, String[][] cellIds) throws CRLException{
		for (int x = 0; x < cellIds.length; x++)
			for (int y = 0; y < cellIds[0].length; y++) {
				if (cellIds[x][y].startsWith("F_")){
					Feature f = FeatureFactory.getFactory().buildFeature(cellIds[x][y].substring(2).split(" ")[1]);
					f.setPosition(x,y,0);
					level.addFeature(f);
				}
			}
	}

	protected int placeKeys(Level ret){
		Debug.enterMethod(this, "placeKeys");
		//Place the magic Keys
		int keys = Util.rand(1,4);
		Position tempPosition = new Position(0,0);
		for (int i = 0; i < keys; i++){
			int keyx = Util.rand(1,ret.getWidth()-1);
			int keyy = Util.rand(1,ret.getHeight()-1);
			int keyz = Util.rand(0, ret.getDepth()-1);
			tempPosition.x = keyx;
			tempPosition.y = keyy;
			tempPosition.z = keyz;
			if (ret.isWalkable(tempPosition) && ! ret.getMapCell(tempPosition).isWater()){
				Feature keyf = FeatureFactory.getFactory().buildFeature("KEY");
				keyf.setPosition(tempPosition.x, tempPosition.y, tempPosition.z);
				ret.addFeature(keyf);
			} else {
				i--;
			}
		}
		Debug.exitMethod(keys);
		return keys;
		
	}
	
	public void placeClue(Level ret, int clueLevel){
		Debug.enterMethod(this, "placeClue");
		//Place the clue
		Position tempPosition = new Position(0,0);
		while(true) {
			int cluex = Util.rand(1,ret.getWidth()-1);
			int cluey = Util.rand(1,ret.getHeight()-1);
			int cluez = Util.rand(0, ret.getDepth()-1);
			tempPosition.x = cluex;
			tempPosition.y = cluey;
			tempPosition.z = cluez;
			if (ret.isWalkable(tempPosition)){
				Item clue = ItemFactory.getItemFactory().createItem("CLUE_PAGE"+clueLevel);
				ret.addItem(tempPosition, clue);
				break;
			}
		}
		Debug.exitMethod();
	}

}
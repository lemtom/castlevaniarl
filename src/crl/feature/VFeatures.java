package crl.feature;

import java.io.Serializable;
import java.util.*;

import sz.util.*;

public class VFeatures implements Serializable{
   	Vector<Feature> features;
	Hashtable<Position, Feature> mLocs;

	private Vector<Feature> temp = new Vector<>();
	public void addFeature(Feature what){
		features.add(what);
		//mLocs.put(what, what.getPosition());
		mLocs.put(what.getPosition(), what);
	}

	public Feature[] getFeaturesAt(Position p){
		temp.clear();
		for (int i=0; i<features.size(); i++){
			if ((features.elementAt(i)).getPosition().equals(p)){
				temp.add(features.elementAt(i));
			}
		}
		if (temp.isEmpty()){
			return null;
		} else {
			return temp.toArray(new Feature[0]);
		}
	}
	
	public Feature getFeatureAt(Position p){
		//return (Feature) mLocs.get(p);
		for (int i=0; i<features.size(); i++){
			if ((features.elementAt(i)).getPosition().equals(p)){
				return features.elementAt(i);
			}
		}
		//Debug.byebye("Feature not found! "+p);
		return null;
	}

	public VFeatures(int size){
		features = new Vector<>(size);
		mLocs = new Hashtable<>(size);
	}

	public void removeFeature(Feature o){
		features.remove(o);
		if (mLocs.containsValue(o)) {
			mLocs.remove(o); //TODO Check, this seems weird
		}
	}
	
	private Vector<Feature> tempVector = new Vector<>();
	public Vector<Feature> getAllOf(String featureID){
		tempVector.removeAllElements();
		for (int i = 0; i < features.size(); i++){
			Feature f = features.elementAt(i);
			if (f.getID().equals(featureID)){
				tempVector.add(f);
			}
		}
		return tempVector;
	}

}
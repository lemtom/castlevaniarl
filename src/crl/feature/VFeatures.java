package crl.feature;

import java.io.Serializable;
import java.util.*;

import sz.util.*;

public class VFeatures implements Serializable {
private static final long serialVersionUID = 1L;
	ArrayList<Feature> features;
	HashMap<Position, Feature> mLocs;

	private ArrayList<Feature> temp = new ArrayList<>();

	public void addFeature(Feature what) {
		features.add(what);
		// mLocs.put(what, what.getPosition());
		mLocs.put(what.getPosition(), what);
	}

	public Feature[] getFeaturesAt(Position p) {
		temp.clear();
        for (Feature feature : features) {
            if (feature.getPosition().equals(p)) {
                temp.add(feature);
            }
        }
		if (temp.isEmpty()) {
			return null;
		} else {
			return temp.toArray(new Feature[0]);
		}
	}

	public Feature getFeatureAt(Position p) {
		// return (Feature) mLocs.get(p);
        for (Feature feature : features) {
            if (feature.getPosition().equals(p)) {
                return feature;
            }
        }
		// Debug.byebye("Feature not found! "+p);
		return null;
	}

	public VFeatures(int size) {
		features = new ArrayList<>(size);
		mLocs = new HashMap<>(size);
	}

	public void removeFeature(Feature o) {
		features.remove(o);
		if (mLocs.containsValue(o)) {
			mLocs.remove(o); // TODO Check, this seems weird
		}
	}

	private ArrayList<Feature> tempArrayList = new ArrayList<>();

	public List<Feature> getAllOf(String featureID) {
		tempArrayList.clear();
        for (Feature f : features) {
            if (f.getID().equals(featureID)) {
                tempArrayList.add(f);
            }
        }
		return tempArrayList;
	}

}
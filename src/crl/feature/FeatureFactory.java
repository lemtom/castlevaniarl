package crl.feature;

import java.util.*;

import sz.util.*;

public class FeatureFactory {
	private static FeatureFactory singleton = new FeatureFactory();

	private Hashtable<String, Feature> definitions;

	public Feature buildFeature(String id) {
		Feature x = definitions.get(id);
		if (x != null)
			return (Feature) x.clone();
		Debug.byebye("Feature " + id + " not found");
		return null;
	}

	public String getDescriptionForID(String id) {
		Feature x = definitions.get(id);
		if (x != null)
			return x.getDescription();
		else
			return "?";
	}

	public void addDefinition(Feature definition) {
		definitions.put(definition.getID(), definition);
	}

	public void init(Feature[] defs) {
        for (Feature def : defs) definitions.put(def.getID(), def);
	}

	public FeatureFactory() {
		definitions = new Hashtable<String, Feature>(40);
	}

	public static FeatureFactory getFactory() {
		return singleton;
	}
}
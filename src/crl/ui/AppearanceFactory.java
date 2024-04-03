package crl.ui;

import sz.util.Debug;

import java.util.HashMap;

public class AppearanceFactory {
	private HashMap<String, Appearance> definitions;
	private static AppearanceFactory singleton = new AppearanceFactory();

    public static AppearanceFactory getAppearanceFactory(){
		return singleton;
	}

	public Appearance getAppearance (String id){
		Appearance ret = definitions.get(id);
		Debug.doAssert(ret != null, "Couldnt find the appearance "+id);
		return ret;
	}

	public void addDefinition(Appearance definition){
		definitions.put(definition.getID(), definition);
	}

	public AppearanceFactory(){
		definitions = new HashMap<>(40);
	}

}
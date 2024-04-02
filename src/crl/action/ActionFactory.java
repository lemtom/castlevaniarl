package crl.action;

import java.util.Hashtable;

import sz.util.Debug;

public class ActionFactory {
	private static final ActionFactory singleton = new ActionFactory();
	private Hashtable<String, Action> definitions = new Hashtable<>(20);

	public static ActionFactory getActionFactory(){
		return singleton;
    }

	public Action getAction (String id){
		Action ret = definitions.get(id);
		Debug.doAssert(ret != null, "Tried to get an invalid "+id+" Action");
		return ret;
	}

	public void addDefinition(Action definition){
		definitions.put(definition.getID(), definition);
	}

	
}

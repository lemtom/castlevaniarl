package crl.ai;

import crl.action.Action;
import crl.actor.Actor;

public interface ActionSelector extends Cloneable, java.io.Serializable{
	Action selectAction(Actor who);
	String getID();

	ActionSelector derive();

}
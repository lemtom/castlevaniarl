package crl.ui;

import crl.actor.Actor;

public class RefreshActor extends Actor {
private static final long serialVersionUID = 1L;
	/** This actor stays in the game queue
	 * and operates on the UI */

	private static RefreshActor singleton = new RefreshActor(0);
	/*{
	 singleton.setSelector(new DumbSelector());*/

	public static final int REFRESHVIEWPORT = 1;
	public static final int NONE = 0;

	private int opCode;

	public RefreshActor(int pCode){
		opCode = pCode;
 	}

 	public int getOpCode(){
 		return opCode;
	}

	public void setOpCode(int code){
		opCode = code;
	}

	public static RefreshActor getRefreshActor(int code){
		singleton.setOpCode(code);
		return singleton;
	}

	@Override
	public void act(){
        if (opCode == REFRESHVIEWPORT) {
        }
		level.removeActor(this);
	}
}
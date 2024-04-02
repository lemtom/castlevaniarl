package crl.ui.consoleUI.cuts;

import java.util.Vector;

public class CharChat {
	private Vector<String> conversations = new Vector<>(5);
	private Vector<String> names = new Vector<>(5);
	
	public void add(String name, String conversation){
		names.add(name);
		conversations.add(conversation);
	}
	
	public int getConversations(){
		return conversations.size();
	}
	
	public String getConversation(int i){
		return conversations.elementAt(i);
	}
	
	public String getName(int i){
		return names.elementAt(i);
	}

}

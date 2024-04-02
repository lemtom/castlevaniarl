package crl.ui.consoleUI.cuts;

import java.util.ArrayList;

public class CharChat {
	private ArrayList<String> conversations = new ArrayList<>(5);
	private ArrayList<String> names = new ArrayList<>(5);
	
	public void add(String name, String conversation){
		names.add(name);
		conversations.add(conversation);
	}
	
	public int getConversations(){
		return conversations.size();
	}
	
	public String getConversation(int i){
		return conversations.get(i);
	}
	
	public String getName(int i){
		return names.get(i);
	}

}

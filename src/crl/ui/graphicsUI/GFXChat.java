package crl.ui.graphicsUI;

import java.awt.*;
import java.util.ArrayList;

public class GFXChat {
	private ArrayList<Image> portraits = new ArrayList<>(10);
	private ArrayList<String> names  = new ArrayList<>(10);
	private ArrayList<String> conversations = new ArrayList<>(10);
	
	public void add(String name, String conversation, Image portrait){
		names.add(name);
		conversations.add(conversation);
		portraits.add(portrait);
	}
	
	public void add(String name, String conversation){
		names.add(name);
		conversations.add(conversation);
		portraits.add(null);
	}
	
	public int getConversations(){
		return conversations.size();
	}
	
	public Image getPortrait(int i){
		return portraits.get(i);
	}
	
	public String getConversation(int i){
		return conversations.get(i);
	}
	
	public String getName(int i){
		return names.get(i);
	}
}

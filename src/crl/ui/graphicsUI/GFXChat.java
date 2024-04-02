package crl.ui.graphicsUI;

import java.awt.Image;
import java.util.Vector;

public class GFXChat {
	private Vector<Image> portraits = new Vector<>(10);
	private Vector<String> names  = new Vector<>(10);
	private Vector<String> conversations = new Vector<>(10);
	
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
		return portraits.elementAt(i);
	}
	
	public String getConversation(int i){
		return conversations.elementAt(i);
	}
	
	public String getName(int i){
		return names.elementAt(i);
	}
}

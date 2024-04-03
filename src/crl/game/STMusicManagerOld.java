package crl.game;

import java.util.HashMap;

import sz.midi.MidisLoader;
import sz.mp3.JLayerMP3Player;

public class STMusicManagerOld {
	//private Thread currentMidiThread;
	private Thread currentMP3Thread;
	private HashMap<String, String> musics = new HashMap<>();
	private boolean enabled;
	private String playing = "__nuthin";
	
	public static STMusicManagerOld thus;
	
	public static void initManager() {
		thus = new STMusicManagerOld();
	}
	
	private static MidisLoader midiPlayer;
	public STMusicManagerOld () {
		midiPlayer = new MidisLoader();
		JLayerMP3Player mp3Player = new JLayerMP3Player();
		//currentMidiThread = new Thread(midiPlayer);
		currentMP3Thread = new Thread(mp3Player);
		currentMP3Thread.start();
	}
	
	public void stopMusic(){
		if (playing.endsWith("mp3")) {
			JLayerMP3Player.setInstruction(JLayerMP3Player.INS_STOP);
			if (currentMP3Thread != null){
				currentMP3Thread.interrupt();
			}
		} else {
			midiPlayer.stop();
		}
	}
	
	public void die(){
		midiPlayer.close();
		JLayerMP3Player.setInstruction(JLayerMP3Player.INS_DIE);
		if (currentMP3Thread != null){
			currentMP3Thread.interrupt();
		}
	}
	
	public void play(String fileName){
		if (!enabled || playing.equals(fileName))
			return;
		stopMusic();
		try {
			playing = fileName;
			if (fileName.endsWith("mp3")){
				JLayerMP3Player.setMP3(fileName);
				JLayerMP3Player.setInstruction(JLayerMP3Player.INS_LOAD);
				if (currentMP3Thread != null){
					currentMP3Thread.interrupt();
				}
			} else {
				midiPlayer.playFile(fileName, true);
			}
		} catch (Exception e){
			Game.crash("Error trying to play "+fileName,e);
		}
	}
	
	public void addMusic(String levelType, String fileName){
		musics.put(levelType, fileName);
	}
	
	public void setEnabled(boolean value){
		enabled = value;
	}
	
	public void playForLevel (int levelNo, String levelType){
		String bgMusic = musics.get(levelType);
		if (bgMusic != null){
			play(bgMusic);
		} else {
			stopMusic();
		}
	}
	
	public void playKey (String key){
		String bgMusic = musics.get(key);
		if (bgMusic != null){
			play(bgMusic);
		} else {
			stopMusic();
		}
	}

}

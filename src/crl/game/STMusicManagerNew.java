package crl.game;

import java.util.HashMap;

import sz.midi.STMidiPlayer;
//import sz.midi.STMidiPlayer;
import sz.mp3.JLayerMP3Player;

public class STMusicManagerNew {
	private Thread currentMidiThread;
	private Thread currentMP3Thread;
	private final HashMap<String, String> musics = new HashMap<>();
	private boolean enabled;
	private String playing = "__nuthin";

	public static STMusicManagerNew thus;

	public static void initManager() {
		thus = new STMusicManagerNew();
	}

	public STMusicManagerNew() {
		STMidiPlayer midiPlayer = new STMidiPlayer();
		JLayerMP3Player mp3Player = new JLayerMP3Player();
		currentMidiThread = new Thread(midiPlayer);
		currentMP3Thread = new Thread(mp3Player);
		currentMP3Thread.start();
		currentMidiThread.start();
	}

	public void stopMusic() {
		if (playing.endsWith("mp3")) {
			JLayerMP3Player.setInstruction(JLayerMP3Player.INS_STOP);
			if (currentMP3Thread != null) {
				currentMP3Thread.interrupt();
			}
		} else {
			STMidiPlayer.setInstruction(STMidiPlayer.INS_STOP);
			if (currentMidiThread != null)
				currentMidiThread.interrupt();
		}
		playing = "__nuthin";
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void die() {
		STMidiPlayer.setInstruction(STMidiPlayer.INS_DIE);
		if (currentMidiThread != null) {
			currentMidiThread.interrupt();
		}
		JLayerMP3Player.setInstruction(JLayerMP3Player.INS_DIE);
		if (currentMP3Thread != null) {
			currentMP3Thread.interrupt();
		}
	}

	public void play(String fileName) {
		if (!enabled || playing.equals(fileName))
			return;
		stopMusic();
		try {
			playing = fileName;
			if (fileName.endsWith("mp3")) {
				JLayerMP3Player.setMP3(fileName);
				JLayerMP3Player.setInstruction(JLayerMP3Player.INS_LOAD);
				if (currentMP3Thread != null) {
					currentMP3Thread.interrupt();
				}
			} else {
				STMidiPlayer.setMidi(fileName);
				STMidiPlayer.setInstruction(STMidiPlayer.INS_LOAD);
				if (currentMidiThread != null) {
					currentMidiThread.interrupt();
				}
			}

		} catch (Exception e) {
			Game.crash("Error trying to play " + fileName, e);
		}
	}

	public void playOnce(String fileName) {
		if (!enabled || playing.equals(fileName))
			return;
		stopMusic();
		try {
			playing = fileName;
			if (fileName.endsWith("mp3")) {
				JLayerMP3Player.setMP3(fileName);
				JLayerMP3Player.setInstruction(JLayerMP3Player.INS_LOAD);
				if (currentMP3Thread != null) {
					currentMP3Thread.interrupt();
				}
			} else {
				STMidiPlayer.setMidi(fileName);
				STMidiPlayer.setInstruction(STMidiPlayer.INS_LOAD_ONCE);
				if (currentMidiThread != null) {
					currentMidiThread.interrupt();
				}
			}

		} catch (Exception e) {
			Game.crash("Error trying to play " + fileName, e);
		}
	}

	public void addMusic(String levelType, String fileName) {
		musics.put(levelType, fileName);
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}

	public void playForLevel(String levelType) {
		String bgMusic = musics.get(levelType);
		if (bgMusic != null) {
			play(bgMusic);
		} else {
			stopMusic();
		}
	}

	public void playKey(String key) {
		String bgMusic = musics.get(key);
		if (bgMusic != null) {
			play(bgMusic);
		} else {
			stopMusic();
		}
	}

	public void playKeyOnce(String key) {
		String bgMusic = musics.get(key);
		if (bgMusic != null) {
			playOnce(bgMusic);
		} else {
			stopMusic();
		}
	}

}

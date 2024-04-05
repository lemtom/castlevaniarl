package sz.midi;

//MidiInfo.java 
//Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Hold a single midi sequence, and allow it to be played, stopped, paused,
 * resumed, and made to loop.
 * <p>
 * Looping is controlled by MidisLoader by calling tryLooping().
 * <p>
 * MidisLoader passes a reference to its sequencer to each MidiInfo object, so
 * that it can play its sequence.
 */
public class MidiInfo {
	private static final String SOUND_DIR = "";

	private String name;
	private String filename;
	private Sequence seq = null;
	private Sequencer sequencer; // passed in from MidisLoader
	private boolean isLooping = false;

	public MidiInfo(String nm, String fnm, Sequencer sqr) {
		name = nm;
		filename = SOUND_DIR + fnm;
		sequencer = sqr;
		loadMidi();
	}

	/**
	 * Load the Midi sequence
	 */
	private void loadMidi() {
		try {
			URL x = getClass().getResource(filename);
			File file = new File(filename);
			seq = MidiSystem.getSequence(file);
		} catch (InvalidMidiDataException e) {
			System.out.println("Unreadable/unsupported midi file: " + filename);
		} catch (IOException e) {
			System.out.println("Could not read: " + filename);
		} catch (Exception e) {
			System.out.println("Problem with " + filename);
			e.printStackTrace();
		}
	}

	public void play(boolean toLoop) {
		if (nullCheck()) {
			try {
				sequencer.setSequence(seq); // load MIDI sequence into the sequencer
				sequencer.setTickPosition(0); // reset to the start
				isLooping = toLoop;
				sequencer.start(); // play it
			} catch (InvalidMidiDataException e) {
				System.out.println("Corrupted/invalid midi file: " + filename);
			}
		}
	}

	/**
	 * Stop the sequence. We want this to trigger an 'end-of-track' meta message, so
	 * we stop the track by winding it to its end. The meta message will be sent to
	 * meta() in MidisLoader, where the sequencer was created.
	 */
	public void stop() {
		if (nullCheck()) {
			isLooping = false;
			if (!sequencer.isRunning()) // the sequence may be paused
				sequencer.start();
			sequencer.setTickPosition(sequencer.getTickLength());
			// move to the end of the sequence to trigger an end-of-track msg
		}
	}

	/**
	 * Pause the sequence by stopping the sequencer
	 */
	public void pause() {
		if (nullCheck() && sequencer.isRunning()) {
			sequencer.stop();
		}
	}

	public void resume() {
		if (nullCheck())
			sequencer.start();
	}

	/**
	 * Loop the music if it's been set to be loopable, and report whether looping
	 * has occurred. Called by MidisLoader from meta() when it has received an
	 * 'end-of-track' meta message.
	 * <p>
	 * In other words, the sequence is not set in 'looping mode' (which is possible
	 * with new methods in J2SE 1.5), but instead is made to play repeatedly by the
	 * MidisLoader.
	 */
	public boolean tryLooping() {
		if (nullCheck()) {
			if (sequencer.isRunning())
				sequencer.stop();
			sequencer.setTickPosition(0);
			if (isLooping) { // play it again
				sequencer.start();
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	private boolean nullCheck() {
		return sequencer != null && seq != null;
	}

}
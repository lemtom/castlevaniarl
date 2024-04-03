package sz.midi;

import crl.game.Game;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class STMidiPlayer implements Runnable {
	private static final String NONE_YET = "__noneYet";
	private static String currentMidiFile = NONE_YET;
	private static int currentInstruction;

	public static final int INS_LOAD = 1;
	public static final int INS_STOP = 0;
	public static final int INS_DIE = 2;
	public static final int INS_LOAD_ONCE = 3;

	private static boolean loop = true;

	public static void setMidi(String pMidiFile) {
		currentMidiFile = pMidiFile;
	}

	public static void setInstruction(int instruction) {
		currentInstruction = instruction;
	}

	public static void setVolume(double gain) {
		Synthesizer synthesizer = (Synthesizer) sequencer;
		MidiChannel[] channels = synthesizer.getChannels();
		// gain is a value between 0 and 1 (loudest)
		for (MidiChannel channel : channels) {
			channel.controlChange(7, (int) (gain * 127.0d));
		}
	}

	public static Sequencer sequencer;

	public synchronized void run() {
		boolean leave = false;
		while (currentInstruction != INS_DIE) {
			if (currentInstruction == INS_STOP) {
				currentMidiFile = NONE_YET;
			}
			if (currentMidiFile.equals(NONE_YET)) {
				try {
					this.wait();
				} catch (InterruptedException ie) {
					continue;
				}
			}
			File midiFile = new File(currentMidiFile);
			if (currentInstruction == INS_LOAD) {
				if (!midiFile.exists() || midiFile.isDirectory() || !midiFile.canRead()) {
					Game.addReport("Invalid Midi file: " + currentMidiFile);
					try {
						this.wait();
					} catch (InterruptedException ie) {
						continue;
					}
				}
				loop = true;
			}
			if (currentInstruction == INS_LOAD_ONCE) {
				if (!midiFile.exists() || midiFile.isDirectory() || !midiFile.canRead()) {
					Game.addReport("Invalid Midi file: " + currentMidiFile);
					try {
						this.wait();
					} catch (InterruptedException ie) {
						continue;
					}
				}
				loop = false;
			}

			leave = false;
			while (!leave) {
				try {
					sequencer.setSequence(MidiSystem.getSequence(midiFile));
					sequencer.start();
					while (true) {
						if (sequencer.isRunning()) {
							try {
								Thread.sleep(1000); // Check every second
							} catch (InterruptedException ignore) {
								leave = true;
								break;
							}
						} else {
							break;
						}
					}
					// Close the MidiDevice & free resources
					sequencer.stop();
					if (!loop) {
						try {
							this.wait();
						} catch (InterruptedException ie) {
							leave = true;
						}
					}
				} catch (InvalidMidiDataException imde) {
					Game.addReport("Invalid Midi data for " + currentMidiFile);
				} catch (IOException ioe) {
					Game.addReport("I/O Error for " + currentMidiFile);
					ioe.printStackTrace();
				}
			}
		}
		sequencer.close();
	}

}

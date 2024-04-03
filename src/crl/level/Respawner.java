package crl.level;

import crl.actor.*;

public class Respawner extends Actor {
	private static final long serialVersionUID = 1L;
	private int freq;
	private int prob;

	public Respawner(int freq, int prob) {
		this.freq = freq;
		this.prob = prob;
	}

	public int getFreq() {
		return freq;
	}

	public int getProb() {
		return prob;
	}

	@Override
	public String getDescription() {
		return "Respawnie";
	}
}
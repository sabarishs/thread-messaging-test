package com.sabscape.threads.messaging.test;

public class Benzene extends Molecule {

	@Override
	public boolean isBuilt() {
		int carbonCount = 0;
		int hydrogenCount = 0;
		for (Atom atom : getAtoms()) {
			if (atom instanceof Carbon) {
				carbonCount++;
			}
			else {
				hydrogenCount++;
			}
		}
		return (carbonCount == 6) && (hydrogenCount == 6);
	}

}

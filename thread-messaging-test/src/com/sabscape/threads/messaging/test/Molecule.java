package com.sabscape.threads.messaging.test;

import java.util.ArrayList;
import java.util.List;

public abstract class Molecule {
	private List<Atom> atoms = new ArrayList<Atom>();
	protected List<Atom> getAtoms() {
		return atoms;
	}
	public void build(Atom atom) {
		atoms.add(atom);
	}
	public abstract boolean isBuilt();
}

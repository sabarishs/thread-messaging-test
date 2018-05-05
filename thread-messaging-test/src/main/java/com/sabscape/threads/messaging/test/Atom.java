package com.sabscape.threads.messaging.test;

public class Atom {
	private String name;
	private boolean stopProcessing;
	public boolean isStopProcessing() {
		return stopProcessing;
	}
	public void setStopProcessing(boolean stopProcessing) {
		this.stopProcessing = stopProcessing;
	}
	public Atom(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}
	
	
}

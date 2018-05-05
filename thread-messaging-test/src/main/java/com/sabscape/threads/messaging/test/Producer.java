package com.sabscape.threads.messaging.test;

import static java.lang.System.out;

public class Producer implements Runnable {
	private GenericPipe<Carbon> pipeline;
	private int numUnits = 0;
	public Producer(GenericPipe<Carbon> pipeline, int productionSize) {
		this.pipeline = pipeline;
		this.numUnits = productionSize;
	}
	public void run() {
		try {
			int i = 0;
			while (i < numUnits) {
				Carbon atom = new Carbon("" + i++);
				while (!pipeline.add(atom)) {}
//				out.println("Produced atom: " + atom.getName());
			}
			while (!Thread.interrupted()) {
				Carbon atom = new Carbon("STOP");
				atom.setStopProcessing(true);
//				out.println("Generating stop");
				boolean interrupted = false;
				while (!pipeline.add(atom) && !interrupted) {
					Thread.yield();
					interrupted = Thread.interrupted();
				}
				Thread.yield();
				if (interrupted) {
					break;
				}
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			out.println("Producer stopping");
		}
	}
}

package com.sabscape.threads.messaging.test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.lmax.disruptor.EventHandler;
import static java.lang.System.out;


public class Consumer implements Runnable, EventHandler<AtomEvent> {
	private GenericPipe<? extends Atom> pipeline;
	private CountDownLatch stopNotifier;
	private Random random = new Random();
	private boolean simulateProcessing;
	private int numProcessedUnits = 0;
	private int totalSleepTime = 0;
	private int id;
	private int totalConsumers;
	
	public Consumer(int id, GenericPipe<? extends Atom> pipeline, CountDownLatch stopNotifier, boolean simulateProcessing, int totalConsumers) {
		this.id = id;
		this.pipeline = pipeline;
		this.stopNotifier = stopNotifier;
		this.simulateProcessing = simulateProcessing;
		this.totalConsumers = totalConsumers;
	}
	public void run() {
		try {
			while (true) {
				Atom atom = null;
				while (atom == null) {
					atom = pipeline.remove();
				}
				if (atom.isStopProcessing()) {
					stopNotifier.countDown();
					return;
				}
				else {
					numProcessedUnits++;
				}
				if (simulateProcessing) {
					int processingTime = random.nextInt(300);
					if (processingTime < 100) {
						processingTime = processingTime + 100;
					}
					totalSleepTime = totalSleepTime + processingTime;
					Thread.sleep(processingTime);
				}
//				out.println("Consumed atom: " + atom.getName());
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			out.println("Consumer stopping. Processed " + numProcessedUnits + " Total sleep time " + totalSleepTime);
		}
	}
	
	@Override
	public void onEvent(AtomEvent ae, long sequence, boolean endOfBatch)
			throws Exception {
		Atom atom = ae.getValue();
		if ((sequence % totalConsumers) == id) {
			if (atom.isStopProcessing()) {
//				out.println("Got stop message, stopping");
				stopNotifier.countDown();
				return;
			}
			else {
				numProcessedUnits++;
//				out.println("Consumed atom: " + atom.getName());
			}
			if (simulateProcessing) {
				try {
					int processingTime = random.nextInt(300);
					if (processingTime < 100) {
						processingTime = processingTime + 100;
					}
					totalSleepTime = totalSleepTime + processingTime;
					Thread.sleep(processingTime);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}
	
	public void printStats() {
		out.println("Consumer: " + id + " Processed: " + numProcessedUnits + " Total sleep time: " + totalSleepTime);
	}
}

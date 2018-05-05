package com.sabscape.threads.messaging.test;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.YieldingWaitStrategy;

public class Fixture {
	private static final int NUM_MANU_UNITS = 10000000;//100;//10000000;//1000
	private static final int QUEUE_SIZE = 4096;//1024;512;4096;16
	private static final boolean SIMULATE_PROCESSING = false;
	
	public static void main(String[] args) {
		int numConsumers = 1;
		StringBuilder timings = new StringBuilder();
		while (numConsumers <= 5) {
			out.println("Trying with " + numConsumers + " consumers");
			timings.append(numConsumers);
			double execTime = 0;
			out.println("Synchronized");
			execTime = executeWithNonDisruptorPipeline(numConsumers, new MessagePipeSynchronizedImpl<Carbon>(QUEUE_SIZE));
			timings.append(",").append(execTime);
			out.println("BlockingQueue");
			execTime = executeWithNonDisruptorPipeline(numConsumers, new MessagePipeBlockingQueueImpl<Carbon>(QUEUE_SIZE));
			timings.append(",").append(execTime);
			out.println("TransferQueue");
			execTime = executeWithNonDisruptorPipeline(numConsumers, new MessagePipeTransferQueueImpl<Carbon>(QUEUE_SIZE));
			timings.append(",").append(execTime);
			out.println("Disruptor");
			execTime = executeWithDisruptorPipeline(numConsumers);
			timings.append(",").append(execTime).append("\n");
			numConsumers++;
		}
		out.println("########Summary statistics########");
		out.println(timings);
	}

	private static double executeWithDisruptorPipeline(int numConsumers) {
		CountDownLatch cdl = new CountDownLatch(numConsumers);
		List<Consumer> consumers = createConsumers(numConsumers, null, cdl, SIMULATE_PROCESSING);
		MessagePipeDisruptorImpl<Carbon> pipeline = new MessagePipeDisruptorImpl<Carbon>(QUEUE_SIZE, AtomEvent.EVENT_FACTORY, new YieldingWaitStrategy(), consumers);
		Producer producer = new Producer(pipeline, NUM_MANU_UNITS);
		Thread producerThread = new Thread(producer);
		producerThread.start();
		long startTime = System.currentTimeMillis();
		try {
			cdl.await();
			printStatistics(consumers);
			long endTime = System.currentTimeMillis();
			out.println("Took " + (endTime - startTime) + " ms");
			pipeline.halt();
			producerThread.interrupt();
			while (producerThread.isAlive()) {
				out.println("Interrupting producer");
				producerThread.interrupt();
				Thread.sleep(300);
			};
			return endTime - startTime;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return Double.POSITIVE_INFINITY;
		}		
	}

	private static double executeWithNonDisruptorPipeline(int numConsumers, GenericPipe<Carbon> pipeline) {
		Producer producer = new Producer(pipeline, NUM_MANU_UNITS);
		CountDownLatch cdl = new CountDownLatch(numConsumers);
		List<Consumer> consumers = createConsumers(numConsumers, pipeline, cdl, SIMULATE_PROCESSING);
		ExecutorService pool = executeWithExecutor(consumers);
		Thread producerThread = new Thread(producer);
		producerThread.start();
		long startTime = System.currentTimeMillis();
		try {
			cdl.await();
			printStatistics(consumers);
			long endTime = System.currentTimeMillis();
			out.println("Took " + (endTime - startTime) + " ms");
			producerThread.interrupt();
			pool.shutdown();
			try {
				pool.awaitTermination(1000, TimeUnit.MILLISECONDS);
				out.println("Pool terminated");
			}
			catch (Exception e) {
				out.println("Awaiting termination failed, shutting down");
				pool.shutdownNow();
			}
			while (producerThread.isAlive()) {};
			return (endTime - startTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return Double.POSITIVE_INFINITY;
		}
	}

	private static void printStatistics(List<Consumer> consumers) {
		for (Consumer c : consumers) {
			c.printStats();
		}
	}

	private static ExecutorService executeWithExecutor(List<Consumer> consumers) {
		ExecutorService pool = Executors.newFixedThreadPool(1 + consumers.size());
		for (int i = 0; i < consumers.size(); i++) {
			pool.submit(consumers.get(i));
		}
		return pool;
	}

	private static List<Consumer> createConsumers(int count, GenericPipe<? extends Atom> pipeline, CountDownLatch cdl, boolean simulateProcessing) {
		List<Consumer> consumers = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			consumers.add(new Consumer(i, pipeline, cdl, simulateProcessing, count));
		}
		return consumers;
	}
}

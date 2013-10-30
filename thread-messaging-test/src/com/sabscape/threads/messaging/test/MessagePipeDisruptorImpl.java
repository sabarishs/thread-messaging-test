package com.sabscape.threads.messaging.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;

public class MessagePipeDisruptorImpl<T> implements GenericPipe<T> {

	private final RingBuffer<AtomEvent> ringBuffer;
	private ExecutorService executor;
	private List<EventProcessor> eventProcessors = new ArrayList<>();
	
	public MessagePipeDisruptorImpl(int capacity, EventFactory<AtomEvent> ef, WaitStrategy ws, List<Consumer> consumers) {
		ringBuffer = RingBuffer.createSingleProducer(ef, capacity, ws);
		SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
		executor = Executors.newFixedThreadPool(consumers.size());
		for (Consumer consumer :  consumers) {
			BatchEventProcessor<AtomEvent> eventProcessor = new BatchEventProcessor<AtomEvent>(ringBuffer, sequenceBarrier, consumer);
			eventProcessors.add(eventProcessor);
			ringBuffer.addGatingSequences(eventProcessor.getSequence());
			executor.submit(eventProcessor);
		}
	}
	
	@Override
	public boolean add(T t) {
		long sequence = ringBuffer.next();
		AtomEvent e = ringBuffer.get(sequence);
		e.setValue((Atom)t);
		ringBuffer.publish(sequence);
		return true;
	}

	@Override
	public T remove() {
		throw new UnsupportedOperationException();
	}

	public void halt() {
		for (EventProcessor ep : eventProcessors) {
			ep.halt();
		}
		executor.shutdownNow();
	}
}

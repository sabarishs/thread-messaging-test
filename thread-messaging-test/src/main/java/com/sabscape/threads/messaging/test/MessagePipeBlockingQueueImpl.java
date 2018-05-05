package com.sabscape.threads.messaging.test;

import java.util.concurrent.ArrayBlockingQueue;

public class MessagePipeBlockingQueueImpl<T> implements GenericPipe<T> {
	
	private final ArrayBlockingQueue<T> queue;
	
	public MessagePipeBlockingQueueImpl(int capacity) {
		queue = new ArrayBlockingQueue<>(capacity);
	}

	@Override
	public boolean add(T t) {
		return queue.offer(t);
	}

	@Override
	public T remove() {
		return queue.poll();
	}

}

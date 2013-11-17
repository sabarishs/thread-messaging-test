package com.sabscape.threads.messaging.test;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

public class MessagePipeTransferQueueImpl<T> implements GenericPipe<T> {
	
	private final TransferQueue<T> queue;
	
	public MessagePipeTransferQueueImpl(int capacity) {
		queue = new LinkedTransferQueue<>();
	}

	@Override
	public boolean add(T t) throws InterruptedException {
		//return queue.tryTransfer(t, 200, TimeUnit.MILLISECONDS);
		return queue.tryTransfer(t);
	}

	@Override
	public T remove() throws InterruptedException {
		return queue.poll(200, TimeUnit.MILLISECONDS);
	}

}

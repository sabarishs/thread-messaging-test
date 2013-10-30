package com.sabscape.threads.messaging.test;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

public class MessagePipeSynchronizedImpl<T> implements GenericPipe<T> {
	private List<T> buffer = null;
	private Object lock = new Object();
	private int maxCapacity;
	
	public MessagePipeSynchronizedImpl() {
		this(20);
	}
	
	public MessagePipeSynchronizedImpl(int maxCapacity) {
		this.maxCapacity = maxCapacity;
		buffer = new ArrayList<T>(maxCapacity);
	}
	
	public boolean add(T atom) throws InterruptedException {
		synchronized(lock) {
			while (buffer.size() >= maxCapacity) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					out.println("Interrupted when waiting");
					throw e;
				}
			}
			buffer.add(atom);
			//out.println("An atom got added: " + atom + " - " + Thread.currentThread().getName());
			lock.notify();
			return true;
		}
	}
	
	public T remove() throws InterruptedException {
		T atom = null;
		synchronized(lock) {
			while (buffer.isEmpty()) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					out.println("Interrupted when waiting");
					throw e;
				}
			}
			atom = buffer.remove(buffer.size()-1);
			//out.println("An atom got consumed: " + atom + " - " + Thread.currentThread().getName());
			lock.notify();
		}
		return atom;
	}
}

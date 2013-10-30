package com.sabscape.threads.messaging.test;

public interface GenericPipe<T> {
	boolean add(T t) throws InterruptedException;
	T remove() throws InterruptedException;
}

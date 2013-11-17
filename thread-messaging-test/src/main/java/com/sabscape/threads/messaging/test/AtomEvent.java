package com.sabscape.threads.messaging.test;

import com.lmax.disruptor.EventFactory;

public class AtomEvent {
	private Atom value;

	public Atom getValue() {
		return value;
	}

	public void setValue(Atom value) {
		this.value = value;
	}
	
	public final static EventFactory<AtomEvent> EVENT_FACTORY = new EventFactory<AtomEvent>() {

		@Override
		public AtomEvent newInstance() {
			return new AtomEvent();
		}
		
	};
}

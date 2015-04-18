package com.ibm.mqtt;

import com.ibm.mqtt.exception.MqttException;

public class MqttTimedEventQueue extends Thread {
	private MqttTimedEvent[] m_array;
	private MqttBaseClient session = null;
	private boolean running = false;
	private boolean stopping = false;
	private boolean canDeliverEvents = false;
	private int m_head = 0;
	private int m_tail = 0;

	public MqttTimedEventQueue(int paramInt, MqttBaseClient paramMqttBaseClient) {
		if (paramInt < 1)
			paramInt = 4;
		this.m_array = new MqttTimedEvent[paramInt];
		this.session = paramMqttBaseClient;
	}

	public synchronized void canDeliverEvents(boolean paramBoolean) {
		this.canDeliverEvents = paramBoolean;
		notifyAll();
	}

	private int adjust(int paramInt) {
		int i = this.m_array.length;
		return paramInt < i ? paramInt : paramInt - i;
	}

	public void close() {
		synchronized (this) {
			this.running = false;
			this.stopping = true;
			notifyAll();
		}
		try {
			join();
		} catch (InterruptedException localInterruptedException) {
		}
	}

	public synchronized void enqueue(MqttTimedEvent paramMqttTimedEvent)
			throws MqttException {
		long l = paramMqttTimedEvent.getTime();
		if ((this.m_head == this.m_tail)
				|| (l < this.m_array[this.m_head].getTime())) {
			if (--this.m_head < 0)
				this.m_head = (this.m_array.length - 1);
			this.m_array[this.m_head] = paramMqttTimedEvent;
			if (this.m_head == this.m_tail)
				expand_array();
			notifyAll();
		} else {
			for (int i = (this.m_tail < this.m_head ? this.m_tail
					+ this.m_array.length : this.m_tail) - 1; i >= this.m_head; i--)
				if (l < this.m_array[adjust(i)].getTime()) {
					this.m_array[adjust(i + 1)] = this.m_array[adjust(i)];
				} else {
					this.m_array[adjust(i + 1)] = paramMqttTimedEvent;
					this.m_tail = adjust(this.m_tail + 1);
					if (this.m_head == this.m_tail)
						expand_array();
					return;
				}
			String str = "MqttTimedEventQueue enqueue out of bounds";
			str = str + "\nAdding event:" + paramMqttTimedEvent.toString();
			str = str + "\nEvent queue:" + toString();
			System.out.println(str);
			throw new MqttException(str);
		}
	}

	private void expand_array() {
		int i = this.m_array.length;
		MqttTimedEvent[] arrayOfMqttTimedEvent = new MqttTimedEvent[i * 2];
		System.arraycopy(this.m_array, this.m_head, arrayOfMqttTimedEvent,
				this.m_head, i - this.m_head);
		System.arraycopy(this.m_array, 0, arrayOfMqttTimedEvent, i, this.m_tail);
		this.m_tail += i;
		this.m_array = arrayOfMqttTimedEvent;
	}

	public synchronized boolean isEmpty() {
		return this.m_head == this.m_tail;
	}

	public void run() {
		MqttTimedEvent localMqttTimedEvent = null;
		if (!this.stopping)
			this.running = true;
		while ((this.running) && (!this.stopping))
			try {
				try {
					synchronized (this) {
						if (this.stopping)
							return;
						while ((this.running)
								&& ((this.m_head == this.m_tail) || (!this.canDeliverEvents)))
							wait();
						if (this.running) {
							long l1 = System.currentTimeMillis();
							long l2 = this.m_array[this.m_head].getTime();
							if ((l1 < l2) && (this.running)) {
								wait(l2 - l1);
							} else if ((this.canDeliverEvents)
									|| (!this.session
											.outstanding(((MqttRetry) this.m_array[this.m_head])
													.getMsgId()))) {
								localMqttTimedEvent = this.m_array[this.m_head];
								this.m_array[(this.m_head++)] = null;
								if (this.m_head == this.m_array.length)
									this.m_head = 0;
							}
						}
					}
				} catch (InterruptedException localInterruptedException) {
				}
				if (localMqttTimedEvent != null) {
					int i = 0;
					try {
						boolean bool = localMqttTimedEvent.notifyEvent();
						if (bool)
							enqueue(localMqttTimedEvent);
					} catch (Exception localException) {
						i = 0;
					}
					while (i == 0)
						try {
							enqueue(localMqttTimedEvent);
							i = 1;
						} catch (MqttException localMqttException) {
						}
					localMqttTimedEvent = null;
				}
			} catch (Throwable localThrowable) {
				if (this.session != null)
					this.session.setRegisteredThrowable(localThrowable);
			}
	}

	public synchronized void resetTimedEventQueue() {
		this.m_head = 0;
		this.m_tail = 0;
		for (int i = 0; i < this.m_array.length; i++)
			this.m_array[i] = null;
	}

	public synchronized String toString() {
        int i = m_head;
        int j = 0;
        int k = m_head > m_tail ? m_array.length : m_tail;
        String s;
        for(s = "["; i < k; s = s + " " + m_array[i++].toString());
        if(k == m_array.length)
            while(j < m_tail) 
                s = s + " " + m_array[j++].toString();
        if(m_head != m_tail)
            s = s + " ";
        return s + "]";
	}
}

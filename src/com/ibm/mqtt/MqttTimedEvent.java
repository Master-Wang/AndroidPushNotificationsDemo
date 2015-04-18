package com.ibm.mqtt;

public interface MqttTimedEvent {
	public abstract long getTime();

	public abstract boolean notifyEvent() throws Exception;
}

package com.ibm.mqtt.callback;

public interface MqttSimpleCallback {
	public abstract void connectionLost() throws Exception;

	public abstract void publishArrived(String s, byte abyte0[], int i,
			boolean flag) throws Exception;
}

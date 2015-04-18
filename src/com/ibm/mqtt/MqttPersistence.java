package com.ibm.mqtt;

import com.ibm.mqtt.exception.MqttPersistenceException;

public interface MqttPersistence {
	public abstract void open(String s, String s1)
			throws MqttPersistenceException;

	public abstract void close();

	public abstract void reset() throws MqttPersistenceException;

	public abstract byte[][] getAllSentMessages()
			throws MqttPersistenceException;

	public abstract byte[][] getAllReceivedMessages()
			throws MqttPersistenceException;

	public abstract void addSentMessage(int i, byte abyte0[])
			throws MqttPersistenceException;

	public abstract void updSentMessage(int i, byte abyte0[])
			throws MqttPersistenceException;

	public abstract void delSentMessage(int i) throws MqttPersistenceException;

	public abstract void addReceivedMessage(int i, byte abyte0[])
			throws MqttPersistenceException;

	public abstract void delReceivedMessage(int i)
			throws MqttPersistenceException;
}

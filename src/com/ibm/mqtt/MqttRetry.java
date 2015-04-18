package com.ibm.mqtt;

import com.ibm.mqtt.packet.MqttPacket;
import com.ibm.mqtt.trace.MQeTrace;

public class MqttRetry implements MqttTimedEvent {

	private MqttPacket packet;
	private MqttBaseClient sessionRef;
	protected long expires;

	public MqttRetry(MqttBaseClient mqttbaseclient, MqttPacket mqttpacket,
			long l) {
		packet = mqttpacket;
		sessionRef = mqttbaseclient;
		expires = System.currentTimeMillis() + l;
	}

	protected void setMessage(MqttPacket mqttpacket) {
		packet = mqttpacket;
	}

	public int getMsgId() {
		return packet.getMsgId();
	}

	public int getQoS() {
		return packet.getQos();
	}

	public int getMsgType() {
		return packet.getMsgType();
	}

	public long getTime() {
		return expires;
	}

	public boolean notifyEvent() throws Exception {
		if (outstanding()) {
			if (sessionRef.isConnected()) {
				sessionRef.writePacket(packet);
				MQeTrace.trace(this, (short) -30031, 2097152L,
						Mqtt.msgTypes[packet.getMsgType()],
						new Integer(packet.getMsgId()));
			}
			if (packet.getMsgType() == 12)
				expires = System.currentTimeMillis()
						+ (long) (sessionRef.getKeepAlivePeriod() * 1000);
			else
				expires = System.currentTimeMillis()
						+ (long) (sessionRef.getRetry() * 1000);
			return true;
		} else {
			return false;
		}
	}

	public synchronized boolean outstanding() {
		return sessionRef.outstanding(packet.getMsgId());
	}

	public String toString() {
		return "[" + Mqtt.msgTypes[packet.getMsgType()] + " MsgId:"
				+ packet.getMsgId() + " Expires:" + getTime() + "]";
	}

}

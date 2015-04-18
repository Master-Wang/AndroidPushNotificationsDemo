package com.ibm.mqtt.packet;

import com.ibm.mqtt.MqttProcessor;

public class MqttDisconnect extends MqttPacket {
	public MqttDisconnect() {
		setMsgType((short) 14);
	}

	public MqttDisconnect(byte abyte0[]) {
		super(abyte0);
		setMsgType((short) 14);
	}

	public void process(MqttProcessor mqttprocessor) {
	}

	public byte[] toBytes() {
		message = new byte[1];
		message[0] = super.toBytes()[0];
		createMsgLength();
		return message;
	}
}

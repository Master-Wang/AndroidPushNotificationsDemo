package com.ibm.mqtt.packet;

import com.ibm.mqtt.MqttProcessor;

public class MqttPingreq extends MqttPacket {
	public MqttPingreq() {
		setMsgType((short) 12);
	}

	public MqttPingreq(byte abyte0[], int i) {
		super(abyte0);
		setMsgType((short) 12);
	}

	public void process(MqttProcessor mqttprocessor) {
		mqttprocessor.process(this);
	}

	public byte[] toBytes() {
		message = new byte[1];
		message[0] = super.toBytes()[0];
		createMsgLength();
		return message;
	}
}

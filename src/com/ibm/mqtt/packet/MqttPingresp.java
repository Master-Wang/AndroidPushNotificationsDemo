package com.ibm.mqtt.packet;

import com.ibm.mqtt.MqttProcessor;

public class MqttPingresp extends MqttPacket{
	public MqttPingresp() {
		setMsgType((short) 13);
	}

	public MqttPingresp(byte abyte0[], int i) {
		super(abyte0);
		setMsgType((short) 13);
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

package com.ibm.mqtt.packet;

import com.ibm.mqtt.MqttProcessor;

public class MqttConnack extends MqttPacket{
	public boolean topicNameCompression;
	public short returnCode;

	public MqttConnack() {
		setMsgType((short) 2);
	}

	public MqttConnack(byte abyte0[], int i) {
		super(abyte0);
		setMsgType((short) 2);
		topicNameCompression = (abyte0[i] & 1) != 0;
		returnCode = abyte0[i + 1];
	}

	public void process(MqttProcessor mqttprocessor) {
		mqttprocessor.process(this);
	}

	public byte[] toBytes() {
		message = new byte[3];
		message[0] = super.toBytes()[0];
		message[1] = ((byte) (topicNameCompression ? 1 : 0));
		message[2] = (byte) returnCode;
		createMsgLength();
		return message;
	}
}

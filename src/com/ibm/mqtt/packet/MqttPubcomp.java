package com.ibm.mqtt.packet;

import com.ibm.mqtt.MqttProcessor;
import com.ibm.mqtt.util.MqttUtils;

public class MqttPubcomp extends MqttPacket{
	public MqttPubcomp() {
		setMsgType((short) 7);
	}

	public MqttPubcomp(byte abyte0[], int i) {
		super(abyte0);
		setMsgType((short) 7);
		setMsgId(MqttUtils.toShort(abyte0, i));
	}

	public void process(MqttProcessor mqttprocessor) {
		mqttprocessor.process(this);
	}

	public byte[] toBytes() {
		message = new byte[3];
		message[0] = super.toBytes()[0];
		int i = getMsgId();
		message[1] = (byte) (i / 256);
		message[2] = (byte) (i % 256);
		createMsgLength();
		return message;
	}
}

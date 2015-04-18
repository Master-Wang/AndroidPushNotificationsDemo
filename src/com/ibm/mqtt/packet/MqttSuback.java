package com.ibm.mqtt.packet;

import com.ibm.mqtt.MqttProcessor;
import com.ibm.mqtt.util.MqttUtils;

public class MqttSuback extends MqttPacket {

	public byte TopicsQoS[];

	public MqttSuback() {
		setMsgType((short) 9);
	}

	public MqttSuback(byte abyte0[], int i) {
		super(abyte0);
		setMsgType((short) 9);
		setMsgId(MqttUtils.toShort(abyte0, i));
		TopicsQoS = MqttUtils.SliceByteArray(abyte0, i + 2, abyte0.length
				- (i + 2));
	}

	public void process(MqttProcessor mqttprocessor) {
		mqttprocessor.process(this);
	}

	public byte[] toBytes() {
		message = new byte[TopicsQoS.length + 3];
		message[0] = super.toBytes()[0];
		int i = getMsgId();
		message[1] = (byte) (i / 256);
		message[2] = (byte) (i % 256);
		for (int j = 0; j < TopicsQoS.length; j++)
			message[j + 3] = TopicsQoS[j];

		createMsgLength();
		return message;
	}
}

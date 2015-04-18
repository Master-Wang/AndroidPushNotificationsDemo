package com.ibm.mqtt.packet;

import java.util.Vector;

import com.ibm.mqtt.MqttProcessor;
import com.ibm.mqtt.util.MqttUtils;

public class MqttSubscribe extends MqttPacket {

	public String topics[];
	public byte topicsQoS[];

	public MqttSubscribe() {
		setMsgType((short) 8);
	}

	public MqttSubscribe(byte abyte0[], int i) {
		super(abyte0);
		setMsgType((short) 8);
		setMsgId(MqttUtils.toShort(abyte0, i));
		Vector vector = MqttUtils.getTopicsWithQoS(MqttUtils.SliceByteArray(
				abyte0, i + 2, abyte0.length - (i + 2)));
		int j = vector.size();
		topicsQoS = new byte[j];
		topics = new String[j];
		for (int k = 0; k < j; k++) {
			String s = vector.elementAt(k).toString();
			topics[k] = s.substring(0, s.length() - 1);
			char c = s.charAt(s.length() - 1);
			topicsQoS[k] = (byte) Character.digit(c, 10);
		}

	}

	public void compressTopic() {
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
		byte byte0 = 3;
		for (int j = 0; j < topics.length; j++) {
			byte abyte0[] = MqttUtils.StringToUTF(topics[j]);
			message = MqttUtils.concatArray(
					MqttUtils.concatArray(message, abyte0), 0, message.length
							+ abyte0.length, topicsQoS, j, 1);
		}

		createMsgLength();
		return message;
	}

	private void uncompressTopic() {
	}

}

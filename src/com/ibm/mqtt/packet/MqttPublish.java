package com.ibm.mqtt.packet;

import com.ibm.mqtt.MqttProcessor;
import com.ibm.mqtt.util.MqttUtils;

public class MqttPublish extends MqttPacket{

	public String topicName;

	public MqttPublish() {
		setMsgType((short) 3);
	}

	public MqttPublish(byte abyte0[], int i) {
		super(abyte0);
		setMsgType((short) 3);
		topicName = MqttUtils.UTFToString(abyte0, i);
		if (getQos() > 0) {
			setMsgId(MqttUtils.toShort(abyte0, i + topicName.length() + 2));
			setPayload(MqttUtils.SliceByteArray(abyte0, i + topicName.length()
					+ 4, abyte0.length - (i + topicName.length() + 4)));
		} else {
			setPayload(MqttUtils.SliceByteArray(abyte0, i + topicName.length()
					+ 2, abyte0.length - (i + topicName.length() + 2)));
		}
	}

	public void compressTopic() {
	}

	public void process(MqttProcessor mqttprocessor) {
		if (mqttprocessor.supportTopicNameCompression())
			uncompressTopic();
		mqttprocessor.process(this);
	}

	public byte[] toBytes() {
		byte abyte0[] = MqttUtils.StringToUTF(topicName);
		if (getQos() > 0)
			message = new byte[abyte0.length + 3];
		else
			message = new byte[abyte0.length + 1];
		int i = 0;
		message[i++] = super.toBytes()[0];
		System.arraycopy(abyte0, 0, message, i, abyte0.length);
		i += abyte0.length;
		if (getQos() > 0) {
			int j = getMsgId();
			message[i++] = (byte) (j / 256);
			message[i++] = (byte) (j % 256);
		}
		createMsgLength();
		return message;
	}

	private void uncompressTopic() {
	}

}

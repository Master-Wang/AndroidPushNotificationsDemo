package com.ibm.mqtt.packet;

import com.ibm.mqtt.MqttProcessor;
import com.ibm.mqtt.exception.MqttException;
import com.ibm.mqtt.util.MqttUtils;


public class MqttConnect extends MqttPacket {
	public String ProtoName;
	public short ProtoVersion;
	public boolean CleanStart;
	public boolean TopicNameCompression;
	public short KeepAlive;
	public boolean Will;
	public int WillQoS;
	public boolean WillRetain;
	public String WillTopic;
	public String WillMessage;
	protected String ClientId;

	public MqttConnect() {
		ProtoName = "MQIsdp";
		ProtoVersion = 3;
		setMsgType((short) 1);
	}

	public MqttConnect(byte abyte0[]) {
		super(abyte0);
		ProtoName = "MQIsdp";
		ProtoVersion = 3;
		setMsgType((short) 1);
	}

	public byte[] toBytes() {
		int i = 0;
		message = new byte[42];
		message[i++] = super.toBytes()[0];
		byte abyte0[] = MqttUtils.StringToUTF(ProtoName);
		System.arraycopy(abyte0, 0, message, i, abyte0.length);
		i += abyte0.length;
		message[i++] = (byte) ProtoVersion;
	    int j = this.TopicNameCompression ? 1 : 0;
	    int k = this.CleanStart ? 2 : 0;
	    int m = this.Will ? (byte)((this.WillRetain ? 32 : 0) | (byte)((this.WillQoS & 0x3) << 3) | 0x4) : 0;
		message[i++] = (byte) (j | k | m);
		message[i++] = (byte) (KeepAlive / 256);
		message[i++] = (byte) (KeepAlive % 256);
		abyte0 = MqttUtils.StringToUTF(ClientId);
		System.arraycopy(abyte0, 0, message, i, abyte0.length);
		i += abyte0.length;
		if (Will) {
			byte abyte1[] = MqttUtils.StringToUTF(WillTopic);
			byte abyte2[] = MqttUtils.StringToUTF(WillMessage);
			message = MqttUtils.concatArray(MqttUtils.concatArray(message, 0,
					i, abyte1, 0, abyte1.length), abyte2);
			i += abyte1.length + abyte2.length;
		}
		message = MqttUtils.SliceByteArray(message, 0, i);
		createMsgLength();
		return message;
	}

	public void process(MqttProcessor mqttprocessor) {
	}

	public String getClientId() {
		return ClientId;
	}

	public void setClientId(String s) throws MqttException {
		if (s.length() > 23) {
			throw new MqttException("MQIsdp ClientId > 23 bytes");
		} else {
			ClientId = s;
			return;
		}
	}

}

package com.ibm.mqtt.packet;

import java.util.Vector;

import com.ibm.mqtt.MqttProcessor;
import com.ibm.mqtt.util.MqttUtils;

public class MqttUnsubscribe extends MqttPacket{
	public MqttUnsubscribe()
    {
        setMsgType((short)10);
    }

    public MqttUnsubscribe(byte abyte0[], int i)
    {
        super(abyte0);
        setMsgType((short)10);
        setMsgId(MqttUtils.toShort(abyte0, i));
        Vector vector = MqttUtils.UTFToStrings(abyte0, i + 2);
        int j = vector.size();
        topics = new String[j];
        for(int k = 0; k < j; k++)
            topics[k] = vector.elementAt(k).toString();

    }

    public void compressTopic()
    {
    }

    public void process(MqttProcessor mqttprocessor)
    {
        mqttprocessor.process(this);
    }

    public byte[] toBytes()
    {
        message = new byte[3];
        message[0] = super.toBytes()[0];
        int i = getMsgId();
        message[1] = (byte)(i / 256);
        message[2] = (byte)(i % 256);
        for(int j = 0; j < topics.length; j++)
        {
            byte abyte0[] = MqttUtils.StringToUTF(topics[j]);
            message = MqttUtils.concatArray(message, abyte0);
        }

        createMsgLength();
        return message;
    }

    private void uncompressTopic()
    {
    }

    public String topics[];
}

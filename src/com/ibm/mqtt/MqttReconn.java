package com.ibm.mqtt;

import com.ibm.mqtt.exception.MqttException;

public class MqttReconn extends Thread{
	public MqttReconn(MqttBaseClient mqttbaseclient)
    {
        client = null;
        client = mqttbaseclient;
    }

    public void run()
    {
        try
        {
            client.connectionLost();
            client.setConnectionLost(false);
        }
        catch(Throwable throwable)
        {
            MqttException mqttexception = new MqttException("ConnectionLost exception caught");
            mqttexception.initCause(throwable);
            client.setRegisteredThrowable(mqttexception);
        }
    }

    private MqttBaseClient client;
}

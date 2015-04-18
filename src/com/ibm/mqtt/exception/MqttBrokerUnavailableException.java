package com.ibm.mqtt.exception;


public class MqttBrokerUnavailableException extends MqttException{
	private static final long serialVersionUID = 9050564262001550186L;

	public MqttBrokerUnavailableException()
    {
    }

    public MqttBrokerUnavailableException(String s)
    {
        super(s);
    }
}

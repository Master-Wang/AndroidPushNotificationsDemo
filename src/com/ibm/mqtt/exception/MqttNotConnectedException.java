package com.ibm.mqtt.exception;

public class MqttNotConnectedException extends MqttException {
	private static final long serialVersionUID = -2300612104463942337L;

	public MqttNotConnectedException() {
	}

	public MqttNotConnectedException(String s) {
		super(s);
	}
}

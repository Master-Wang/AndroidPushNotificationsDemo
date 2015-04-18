package com.ibm.mqtt.util;

public class MqttVersion {

	public static void main(String args[]) {
		printVersion();
		System.exit(0);
	}

	public static void printVersion() {
		System.out.println("MqttClient version  : 1.4.6");
		System.out.println("MQTT protocol version: 3.0");
		System.out.println("");
		System.out.println("Licensed Materials - Property of IBM");
		System.out
				.println("(C) Copyright IBM Corp. 2002, 2009 All Rights Reserved");
	}
}

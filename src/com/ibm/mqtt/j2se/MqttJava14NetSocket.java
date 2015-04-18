package com.ibm.mqtt.j2se;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MqttJava14NetSocket extends Socket {
	public MqttJava14NetSocket(String s, int i, int j) throws IOException {
		InetSocketAddress inetsocketaddress = new InetSocketAddress(s, i);
		connect(inetsocketaddress, j);
	}
}

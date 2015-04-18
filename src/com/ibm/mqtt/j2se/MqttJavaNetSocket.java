package com.ibm.mqtt.j2se;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.ibm.mqtt.MqttAdapter;

public class MqttJavaNetSocket implements MqttAdapter{

	private Socket s;
	private boolean useShutdownMethods;

	public MqttJavaNetSocket() {
		s = null;
		useShutdownMethods = false;
		try {
			(java.net.Socket.class).getMethod("shutdownInput", null);
			useShutdownMethods = true;
		} catch (NoSuchMethodException nosuchmethodexception) {
		}
	}

	public void setConnection(String s1, int i) throws IOException {
		int j = s1.lastIndexOf(':');
		if (j < 6)
			j = s1.indexOf('@');
		try {
			s = new MqttJava14NetSocket(s1.substring(6, j), Integer.parseInt(s1
					.substring(j + 1)), i * 1000);
		} catch (NoClassDefFoundError noclassdeffounderror) {
			s = new Socket(s1.substring(6, j), Integer.parseInt(s1
					.substring(j + 1)));
		}
		if (i > 0) {
			int k = (i + 15) * 1000;
			s.setSoTimeout(k);
		}
	}

	public InputStream getInputStream() throws IOException {
		if (s == null)
			return null;
		else
			return s.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		if (s == null)
			return null;
		else
			return s.getOutputStream();
	}

	public void close() throws IOException {
		if (s != null)
			s.close();
	}

	public void closeInputStream() throws IOException {
		if (useShutdownMethods)
			s.shutdownInput();
		else
			s.getInputStream().close();
	}

	public void closeOutputStream() throws IOException {
		try {
			s.setSoLinger(true, 10);
		} catch (SocketException socketexception) {
		}
		if (useShutdownMethods)
			s.shutdownOutput();
		else
			s.getOutputStream().close();
	}

}

package com.ibm.mqtt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MqttAdapter {
	public abstract void setConnection(String s, int i) throws IOException;

	public abstract InputStream getInputStream() throws IOException;

	public abstract OutputStream getOutputStream() throws IOException;

	public abstract void closeInputStream() throws IOException;

	public abstract void closeOutputStream() throws IOException;

	public abstract void close() throws IOException;
}

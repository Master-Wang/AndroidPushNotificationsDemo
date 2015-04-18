package com.ibm.mqtt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.ibm.mqtt.exception.MqttException;
import com.ibm.mqtt.exception.MqttNotConnectedException;
import com.ibm.mqtt.packet.MqttConnack;
import com.ibm.mqtt.packet.MqttConnect;
import com.ibm.mqtt.packet.MqttDisconnect;
import com.ibm.mqtt.packet.MqttPacket;
import com.ibm.mqtt.packet.MqttPingreq;
import com.ibm.mqtt.packet.MqttPingresp;
import com.ibm.mqtt.packet.MqttPuback;
import com.ibm.mqtt.packet.MqttPubcomp;
import com.ibm.mqtt.packet.MqttPublish;
import com.ibm.mqtt.packet.MqttPubrec;
import com.ibm.mqtt.packet.MqttPubrel;
import com.ibm.mqtt.packet.MqttSuback;
import com.ibm.mqtt.packet.MqttSubscribe;
import com.ibm.mqtt.packet.MqttUnsuback;
import com.ibm.mqtt.packet.MqttUnsubscribe;
import com.ibm.mqtt.trace.MQeTrace;

public class Mqtt implements MqttProcessor {
	private Class socketClass = null;
	private MqttAdapter socket = null;
	private DataInputStream stream_in = null;
	private DataOutputStream stream_out = null;
	private boolean haveWill = false;
	private boolean isSocketConnected = false;
	private boolean connected = false;
	private boolean connectionLost = false;
	private Object streamReadLock = new Object();
	private Object streamWriteLock = new Object();
	private int curMsgId = 0;
	private int keepAlivePeriod;
	private boolean topicNameCompression = false;
	private Hashtable outMsgIdsAllocated = new Hashtable();
	protected String connection;
	public static final short CONNECT = 1;
	public static final short CONNACK = 2;
	public static final short PUBLISH = 3;
	public static final short PUBACK = 4;
	public static final short PUBREC = 5;
	public static final short PUBREL = 6;
	public static final short PUBCOMP = 7;
	public static final short SUBSCRIBE = 8;
	public static final short SUBACK = 9;
//	public static final short UNSUBSCRIBE = 10;
	public static final short UNSUBACK = 11;
	public static final short PINGREQ = 12;
	public static final short PINGRESP = 13;
	public static final short DISCONNECT = 14;
	public static final String[] msgTypes = { null, "CONNECT", "CONNACK",
			"PUBLISH", "PUBACK", "PUBREC", "PUBREL", "PUBCOMP", "SUBSCRIBE",
			"SUBACK", "UNSUBSCRIBE", "UNSUBACK", "PINGREQ", "PINGRESP",
			"DISCONNECT" };
	protected MqttException registeredException = null;

	protected void initialise(String paramString, Class paramClass) {
		this.connection = paramString;
		this.socketClass = paramClass;
	}

	private MqttPacket decodePacket(byte[] bytes, int paramInt, short packetType)
			throws MqttException {
		MqttPacket packet = null;
		if (isSocketConnected())
			switch (packetType) {
			case 12:
				packet = new MqttPingreq(bytes, paramInt);
				break;
			case 13:
				packet = new MqttPingresp(bytes, paramInt);
				break;
			case 3:
				packet = new MqttPublish(bytes, paramInt);
				break;
			case 4:
				packet = new MqttPuback(bytes, paramInt);
				break;
			case 5:
				packet = new MqttPubrec(bytes, paramInt);
				break;
			case 6:
				packet = new MqttPubrel(bytes, paramInt);
				break;
			case 7:
				packet = new MqttPubcomp(bytes, paramInt);
				break;
			case 8:
				packet = new MqttSubscribe(bytes, paramInt);
				break;
			case 9:
				packet = new MqttSuback(bytes, paramInt);
				break;
			case 10:
				packet = new MqttUnsubscribe(bytes, paramInt);
				break;
			case 11:
				packet = new MqttUnsuback(bytes, paramInt);
				break;
			case 14:
				break;
			case 1:
				break;
			case 2:
				packet = new MqttConnack(bytes, paramInt);
				break;
			default:
				throw new MqttException("Mqtt: Unknown message type: "
						+ packetType);
			}
		return packet;
	}

	protected int getKeepAlivePeriod() {
		return this.keepAlivePeriod;
	}

	protected boolean isSocketConnected() {
		synchronized (this.streamWriteLock) {
			return this.isSocketConnected;
		}
	}

	private void setSocketState(boolean paramBoolean) {
		synchronized (this.streamWriteLock) {
			this.isSocketConnected = paramBoolean;
		}
	}

	protected boolean hasKeepAlive() {
		return this.keepAlivePeriod > 0;
	}

	protected boolean hasWill() {
		return this.haveWill;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void initialiseOutMsgIds(Vector paramVector) {
		this.outMsgIdsAllocated.clear();
		this.curMsgId = 1;
		if (paramVector != null) {
			Enumeration localEnumeration = paramVector.elements();
			while (localEnumeration.hasMoreElements()) {
				Integer localInteger = (Integer) localEnumeration.nextElement();
				this.outMsgIdsAllocated.put(localInteger, localInteger);
			}
		}
	}

	protected int nextMsgId() throws MqttException {
		if (this.outMsgIdsAllocated.size() == 65535)
			throw new MqttException("All available msgIds in use:65535");
		int i = 0;
		while (i == 0) {
			if (this.curMsgId < 65535)
				this.curMsgId += 1;
			else
				this.curMsgId = 1;
			Integer localInteger = new Integer(this.curMsgId);
			if (!this.outMsgIdsAllocated.contains(localInteger)) {
				this.outMsgIdsAllocated.put(localInteger, localInteger);
				i = 1;
			}
		}
		return this.curMsgId;
	}

	protected void releaseMsgId(int paramInt) {
		this.outMsgIdsAllocated.remove(new Integer(paramInt));
	}

	public synchronized boolean isConnected() {
		return this.connected;
	}

	public boolean supportTopicNameCompression() {
		return this.topicNameCompression;
	}

	protected void pingOut() throws MqttException {
		MqttPingreq localMqttPingreq = new MqttPingreq();
		writePacket(localMqttPingreq);
	}

	protected void process() throws Exception {
		MqttPacket localMqttPacket = readPacket();
		if (localMqttPacket != null)
			localMqttPacket.process(this);
		else
			System.out.println("Mqtt: Read a null packet from the socket");
	}

	public void process(MqttConnack paramMqttConnack) {
		if (paramMqttConnack.returnCode == 0) {
			this.topicNameCompression = paramMqttConnack.topicNameCompression;
			setConnectionState(true);
		} else if (paramMqttConnack.returnCode == 1) {
			setConnectionState(false);
		} else if (paramMqttConnack.returnCode == 2) {
			setConnectionState(false);
		} else if (paramMqttConnack.returnCode == 3) {
			setConnectionState(false);
		}
		if (paramMqttConnack.returnCode != 0)
			tcpipDisconnect(false);
	}

	public void process(MqttPingreq paramMqttPingreq) {
		try {
			writePacket(new MqttPingresp());
		} catch (Exception localException) {
		}
	}

	public void process(MqttConnect paramMqttConnect) {
	}

	public void process(MqttDisconnect paramMqttDisconnect) {
	}

	public void process(MqttSubscribe paramMqttSubscribe) {
	}

	public void process(MqttUnsubscribe paramMqttUnsubscribe) {
	}

	public void process(MqttPingresp paramMqttPingresp) {
	}

	public void process(MqttPublish paramMqttPublish) {
	}

	public void process(MqttPuback paramMqttPuback) {
	}

	public void process(MqttPubrec paramMqttPubrec) {
	}

	public void process(MqttPubrel paramMqttPubrel) {
	}

	public void process(MqttPubcomp paramMqttPubcomp) {
	}

	public void process(MqttSuback paramMqttSuback) {
	}

	public void process(MqttUnsuback paramMqttUnsuback) {
	}

	protected final MqttPublish genPublishPacket(int paramInt1, int paramInt2,
			String paramString, byte[] paramArrayOfByte, boolean paramBoolean1,
			boolean paramBoolean2) {
		MqttPublish localMqttPublish = new MqttPublish();
		localMqttPublish.setMsgId(paramInt1);
		localMqttPublish.setQos(paramInt2);
		localMqttPublish.topicName = paramString;
		localMqttPublish.setPayload(paramArrayOfByte);
		localMqttPublish.setDup(paramBoolean2);
		localMqttPublish.setRetain(paramBoolean1);
		if (this.topicNameCompression)
			localMqttPublish.compressTopic();
		return localMqttPublish;
	}

	protected final MqttPacket readPacket() throws MqttException,
			InterruptedIOException, IOException {
		byte abyte0[] = new byte[5];
		byte abyte1[] = null;
		boolean flag = false;
		int j = 1;
		int k = 1;
		short word0 = 0;
		synchronized (streamReadLock) {
			int i;
			try {
				i = stream_in.read(abyte0, 0, 1);
			} catch (IOException ioexception) {
				MQeTrace.trace(this, (short) -30033, 2097152L,
						ioexception.getMessage());
				throw ioexception;
			}
			if (i < 0)
				throw new EOFException("DataInputStream.read returned -1");
			int l = 0;
			byte byte0;
			do {
				byte0 = (byte) stream_in.read();
				abyte0[k] = byte0;
				l += (byte0 & 127) * j;
				j *= 128;
				k++;
			} while ((byte0 & 128) != 0);
			abyte1 = new byte[l + k];
			for (int i1 = 0; i1 < k; i1++)
				abyte1[i1] = abyte0[i1];

			MQeTrace.trace(this, (short) -30035, 2097152L, Integer.toString(k),
					Integer.toString(l));
			if (l > 0)
				stream_in.readFully(abyte1, k, l);
			word0 = (short) (abyte1[0] >>> 4 & 15);
		}
		return decodePacket(abyte1, k, word0);
	}

	protected synchronized void setConnectionState(boolean paramBoolean) {
		this.connected = paramBoolean;
	}

	protected void setKeepAlive(int paramInt) {
		this.keepAlivePeriod = paramInt;
	}

	protected void subscribeOut(int paramInt, String[] paramArrayOfString,
			byte[] paramArrayOfByte, boolean paramBoolean) throws Exception {
	}

	protected void unsubscribeOut(int paramInt, String[] paramArrayOfString,
			boolean paramBoolean) throws Exception {
	}

	protected void tcpipConnect(MqttConnect paramMqttConnect)
			throws IOException, Exception {
		synchronized (this.streamWriteLock) {
			tcpipDisconnect(true);
			try {
				this.socket = ((MqttAdapter) this.socketClass.newInstance());
				this.socket.setConnection(this.connection,
						paramMqttConnect.KeepAlive);
				setSocketState(true);
			} catch (IOException localIOException) {
				tcpipDisconnect(true);
				throw localIOException;
			} catch (Exception localException) {
				tcpipDisconnect(true);
				throw localException;
			}
			this.stream_in = new DataInputStream(this.socket.getInputStream());
			this.stream_out = new DataOutputStream(
					this.socket.getOutputStream());
			writePacket(paramMqttConnect);
		}
	}

	protected void tcpipDisconnect(boolean paramBoolean) {
		synchronized (this.streamWriteLock) {
			if (this.stream_out != null) {
				try {
					this.socket.closeOutputStream();
				} catch (IOException localIOException1) {
				}
				this.stream_out = null;
			}
			if (paramBoolean) {
				setSocketState(false);
				if (this.stream_in != null) {
					try {
						this.socket.closeInputStream();
					} catch (IOException localIOException2) {
					}
					this.stream_in = null;
				}
				if (this.socket != null) {
					try {
						this.socket.close();
					} catch (IOException localIOException3) {
					}
					this.socket = null;
				}
			}
		}
	}

	protected final void writePacket(MqttPacket paramMqttPacket)
			throws MqttException {
		synchronized (this.streamWriteLock) {
			if (this.stream_out != null)
				try {
					byte[] arrayOfByte1 = paramMqttPacket.getPayload();
					byte[] arrayOfByte2 = paramMqttPacket.toBytes();
					paramMqttPacket.setDup(true);
					this.stream_out.write(arrayOfByte2);
					if (arrayOfByte1 != null)
						this.stream_out.write(arrayOfByte1);
					this.stream_out.flush();
					arrayOfByte1 = null;
				} catch (IOException localIOException) {
					MQeTrace.trace(this, (short) -30034, 2097152L,
							localIOException.getMessage());
					tcpipDisconnect(true);
					throw new MqttException(localIOException);
				} catch (Exception localException) {
					localException.printStackTrace();
					tcpipDisconnect(true);
					throw new MqttException(localException);
				}
			else
				throw new MqttNotConnectedException();
		}
	}

	protected synchronized boolean isConnectionLost() {
		return this.connectionLost;
	}

	protected synchronized void setConnectionLost(boolean paramBoolean) {
		this.connectionLost = paramBoolean;
	}

	protected void setRegisteredThrowable(Throwable paramThrowable) {
		if ((paramThrowable == null)
				|| ((paramThrowable instanceof MqttException)))
			this.registeredException = ((MqttException) paramThrowable);
		else
			this.registeredException = new MqttException(paramThrowable);
		setConnectionState(false);
		tcpipDisconnect(true);
		setConnectionLost(true);
	}

}

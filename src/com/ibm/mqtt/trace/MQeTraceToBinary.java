package com.ibm.mqtt.trace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class MQeTraceToBinary {
	public static short version[] = { 2, 0, 0, 2 };
	private static final boolean DEBUG = false;
	static final int V1_MAGIC = -17978438;
	static final short DATA_FORMAT_VERSION = 1;
	static String prefixes[] = { "com.ibm.mqe.adapters.MQe", "a:",
			"com.ibm.mqe.administration.MQe", "b:",
			"com.ibm.mqe.attributes.MQe", "c:", "com.ibm.mqe.bindings.MQe",
			"d:", "com.ibm.mqe.communications.MQe", "e:",
			"com.ibm.mqe.messagestore.MQe", "f:", "com.ibm.mqe.mqbridge.MQe",
			"g:", "com.ibm.mqe.registry.MQe", "h:", "com.ibm.mqe.server.MQe",
			"i:", "com.ibm.mqe.mqemqmessage.MQe", "j:",
			"com.ibm.mqe.trace.MQe", "k:", "com.ibm.mqe.validation.MQe", "l:",
			"com.ibm.mqe.MQe", "m:", "com.ibm.mqe.adapters.", "n:",
			"com.ibm.mqe.administration.", "o:", "com.ibm.mqe.attributes.",
			"p:", "com.ibm.mqe.bindings.", "q:", "com.ibm.mqe.communications.",
			"r:", "com.ibm.mqe.messagestore.", "s:", "com.ibm.mqe.mqbridge.",
			"t:", "com.ibm.mqe.registry.", "u:", "com.ibm.mqe.server.", "v:",
			"com.ibm.mqe.mqemqmessage.", "w:", "com.ibm.mqe.trace.", "x:",
			"com.ibm.mqe.validation.", "y:", "com.ibm.mqe.", "z:" };
	private long currentFilter;
	private boolean isOn;
	private int recordsOutput;
	private static final int ASCII_CHARS_IN_MAX_LENGTH_STRING = 20480;
	public static final int UNICODE_CHARS_IN_MAX_LENGTH_STRING = 10240;
	
	private String shortenClassName(String s) {
		String s1 = s;
		if (null != s && s.startsWith("com.ibm.mqe.")) {
			boolean flag = false;
			for (int i = 0; !flag && i < prefixes.length; i += 2)
				if (s.startsWith(prefixes[i])) {
					s1 = prefixes[i + 1] + s.substring(prefixes[i].length());
					flag = true;
				}

		}
		return s1;
	}

	public void traceMessage(Object obj, short word0, long l) {
		traceFilteredMessage(obj, word0, l, new Object[0]);
	}

	public void traceMessage(Object obj, short word0, long l, Object obj1) {
		traceFilteredMessage(obj, word0, l, new Object[] { obj1 });
	}

	public void traceMessage(Object obj, short word0, long l, Object obj1,
			Object obj2) {
		traceFilteredMessage(obj, word0, l, new Object[] { obj1, obj2 });
	}

	public void traceMessage(Object obj, short word0, long l, Object obj1,
			Object obj2, Object obj3) {
		traceFilteredMessage(obj, word0, l, new Object[] { obj1, obj2, obj3 });
	}

	public void traceMessage(Object obj, short word0, long l, Object obj1,
			Object obj2, Object obj3, Object obj4) {
		traceFilteredMessage(obj, word0, l, new Object[] { obj1, obj2, obj3,
				obj4 });
	}

	public void setFilter(long l) {
		if (currentFilter == 0L) {
			if (l != 0L)
				isOn = on();
		} else if (l == 0L) {
			boolean flag = off();
			if (flag)
				isOn = false;
		}
		currentFilter = l;
	}

	private synchronized void traceFilteredMessage(Object obj, short word0,
			long l, Object aobj[]) {
		if (isOn) {
			long l1 = System.currentTimeMillis();
			String s = Thread.currentThread().toString();
			int i = Thread.currentThread().hashCode();
			String s1;
			int j;
			if (null == obj) {
				s1 = "";
				j = 0;
			} else {
				s1 = shortenClassName(obj.getClass().toString());
				j = obj.hashCode();
			}
			String as[] = convertInsertsToStrings(aobj);
			byte abyte0[] = constructRecord(l1, word0, s, i, s1, j, l, as);
			writeRecord(abyte0);
		}
	}

	byte[] constructRecord(long l, short word0, String s, int i, String s1,
			int j, long l1, String as[]) {
		recordsOutput++;
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		try {
			byte abyte0[] = MQe.longToByte(l);
			bytearrayoutputstream.write(abyte0);
			byte abyte1[] = MQe.shortToByte(word0);
			bytearrayoutputstream.write(abyte1);
			encodeAsciiString(bytearrayoutputstream, s);
			byte abyte2[] = MQe.intToByte(i);
			bytearrayoutputstream.write(abyte2);
			encodeAsciiString(bytearrayoutputstream, s1);
			byte abyte3[] = MQe.intToByte(j);
			bytearrayoutputstream.write(abyte3);
			byte abyte4[] = MQe.shortToByte((short) as.length);
			bytearrayoutputstream.write(abyte4);
			for (int k = 0; k < as.length; k++)
				encodeUnicodeString(bytearrayoutputstream, as[k]);

		} catch (IOException ioexception) {
		}
		return bytearrayoutputstream.toByteArray();
	}

	String[] convertInsertsToStrings(Object aobj[]) {
		int i = 0;
		if (aobj == null)
			i = 0;
		else
			i = aobj.length;
		String as[] = new String[i];
		for (int j = 0; j < i; j++)
			if (null == aobj[j])
				as[j] = "";
			else
				as[j] = aobj[j].toString();

		if (i > 0) {
			Object obj = aobj[i - 1];
			if (obj != null && (obj instanceof Throwable))
				as[i - 1] += "\n" + throwableStackTrace((Throwable) obj);
		}
		return as;
	}

	abstract String throwableStackTrace(Throwable throwable);

	MQeTraceToBinary() {
		currentFilter = 0L;
		isOn = false;
		recordsOutput = 0;
	}

	synchronized boolean on() {
		boolean flag = true;
		recordsOutput = 0;
		return flag;
	}

	private static void encodeAsciiString(
			ByteArrayOutputStream bytearrayoutputstream, String s)
			throws IOException {
		if (s.length() > 20480)
			s = s.substring(0, 20479);
		byte abyte0[] = MQe.shortToByte((short) s.length());
		bytearrayoutputstream.write(abyte0);
		byte abyte1[] = MQe.asciiToByte(s);
		bytearrayoutputstream.write(abyte1);
	}

	private static void encodeUnicodeString(
			ByteArrayOutputStream bytearrayoutputstream, String s)
			throws IOException {
		if (s.length() > 10240)
			s = s.substring(0, 10239);
		byte abyte0[] = MQe.shortToByte((short) s.length());
		bytearrayoutputstream.write(abyte0);
		byte abyte1[] = MQe.unicodeToByte(s);
		bytearrayoutputstream.write(abyte1);
	}

	byte[] getHeader() {
		byte abyte0[] = null;
		try {
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			byte abyte1[] = MQe.intToByte(-17978438);
			bytearrayoutputstream.write(abyte1);
			byte abyte2[] = MQe.shortToByte((short) 1);
			bytearrayoutputstream.write(abyte2);
			short aword0[] = MQe.version;
			byte abyte3[] = MQe.shortToByte(aword0[0]);
			bytearrayoutputstream.write(abyte3);
			byte abyte4[] = MQe.shortToByte(aword0[1]);
			bytearrayoutputstream.write(abyte4);
			byte abyte5[] = MQe.shortToByte(aword0[2]);
			bytearrayoutputstream.write(abyte5);
			byte abyte6[] = MQe.shortToByte((short) 0);
			bytearrayoutputstream.write(abyte6);
			encodeAsciiString(bytearrayoutputstream,
					"mqe_java/source/com/ibm/mqe/MQe.java, MQeBase, la000 1.111");
			byte abyte7[] = MQe.longToByte(System.currentTimeMillis());
			bytearrayoutputstream.write(abyte7);
			abyte0 = bytearrayoutputstream.toByteArray();
		} catch (IOException ioexception) {
		}
		return abyte0;
	}

	synchronized boolean off() {
		boolean flag = true;
		return flag;
	}

	byte[] getFooter(short word0) {
		byte abyte0[] = constructRecord(System.currentTimeMillis(), word0,
				Thread.currentThread().toString(), Thread.currentThread()
						.hashCode(), getClass().toString(), hashCode(), 0L,
				new String[0]);
		return abyte0;
	}

	abstract boolean writeRecord(byte abyte0[]);

}

package com.ibm.mqtt.trace;

public class MQe {

	public MQe() {
		traceService = new MQeTrace();
	}

	public static byte[] asciiToByte(String s) {
		if (s == null)
			return null;
		byte abyte0[] = new byte[s.length()];
		char ac[] = new char[s.length()];
		s.getChars(0, s.length(), ac, 0);
		for (int i = 0; i < ac.length; i++)
			abyte0[i] = (byte) ac[i];

		return abyte0;
	}

	public static String byteToAscii(byte abyte0[]) {
		if (abyte0 == null)
			return null;
		char ac[] = new char[abyte0.length];
		for (int i = 0; i < abyte0.length; i++)
			ac[i] = (char) (abyte0[i] & 255);

		return new String(ac);
	}

	public static String byteToHex(byte abyte0[]) {
		return byteToHex(abyte0, 0, abyte0.length);
	}

	public static String byteToHex(byte abyte0[], int i, int j) {
		StringBuffer stringbuffer = new StringBuffer(128);
		if (abyte0 != null) {
			for (int k = i; k < i + j; k++) {
				stringbuffer.append(Hex[abyte0[k] >> 4 & 15]);
				stringbuffer.append(Hex[abyte0[k] & 15]);
			}

		}
		return stringbuffer.toString();
	}

	public static int byteToInt(byte abyte0[], int i) {
		int j = 0;
		for (int k = 0; k < 4; k++)
			j = (j << 8) + (abyte0[i + k] & 255);

		return j;
	}

	public static long byteToLong(byte abyte0[], int i) {
		long l = 0L;
		for (int j = 0; j < 8; j++)
			l = (l << 8) + (long) (abyte0[i + j] & 255);

		return l;
	}

	public static short byteToShort(byte abyte0[], int i) {
		return (short) ((((short) abyte0[i + 0] & 255) << 8) + ((short) abyte0[i + 1] & 255));
	}

	public static String byteToUnicode(byte abyte0[]) {
		if (abyte0 == null)
			return null;
		char ac[] = new char[abyte0.length / 2];
		for (int i = 0; i < ac.length; i++)
			ac[i] = (char) ((abyte0[i * 2] & 255) << 8 | abyte0[i * 2 + 1] & 255);

		return new String(ac, 0, ac.length);
	}

	public static byte[] intToByte(int i) {
		byte abyte0[] = new byte[4];
		for (int j = 0; j < 4; j++) {
			abyte0[3 - j] = (byte) (i & 255);
			i >>= 8;
		}

		return abyte0;
	}

	public static byte[] longToByte(long l) {
		byte abyte0[] = new byte[8];
		for (int i = 0; i < 8; i++) {
			abyte0[7 - i] = (byte) (int) (l & 255L);
			l >>= 8;
		}

		return abyte0;
	}

	public static byte[] shortToByte(short word0) {
		byte abyte0[] = new byte[2];
		abyte0[0] = (byte) (word0 >> 8 & 255);
		abyte0[1] = (byte) (word0 & 255);
		return abyte0;
	}

	public static byte[] unicodeToByte(String s) {
		if (s == null)
			return null;
		char ac[] = new char[s.length()];
		s.getChars(0, s.length(), ac, 0);
		byte abyte0[] = new byte[ac.length * 2];
		for (int i = 0; i < ac.length; i++) {
			abyte0[i * 2] = (byte) (ac[i] >> 8 & 255);
			abyte0[i * 2 + 1] = (byte) (ac[i] & 255);
		}

		return abyte0;
	}

	public static final String copyright = "Licensed Materials - Property of IBM\nProduct number: 5765-E63\nCopyright IBM Corp. 1999,2002 All Rights Reserved.\nUS Government Users Restriced Rights - use, duplication or\ndisclosure restriced by GSA ADP Schedule Contract with IBM Corp.";
	public static final String sccsid = "mqe_java/source/com/ibm/mqe/MQe.java, MQeBase, la000 1.111";
	public static short version[] = { 2, 0, 0, 2 };
	private MQeTrace traceService;
	public static final char Hex[] = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
}

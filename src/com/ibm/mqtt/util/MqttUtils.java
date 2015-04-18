package com.ibm.mqtt.util;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class MqttUtils {

	public static final String STRING_ENCODING = "UTF-8";

	public static final byte[] concatArray(byte abyte0[], byte abyte1[]) {
		byte abyte2[] = new byte[abyte0.length + abyte1.length];
		System.arraycopy(abyte0, 0, abyte2, 0, abyte0.length);
		System.arraycopy(abyte1, 0, abyte2, abyte0.length, abyte1.length);
		return abyte2;
	}

	public static final byte[] concatArray(byte abyte0[], int i, int j,
			byte abyte1[], int k, int l) {
		byte abyte2[] = new byte[j + l];
		System.arraycopy(abyte0, i, abyte2, 0, j);
		System.arraycopy(abyte1, k, abyte2, j, l);
		return abyte2;
	}

	public static final long getExpiry(long l) {
		return System.currentTimeMillis() / 1000L + (l * 3L) / 2L;
	}

	public static final Vector getTopicsWithQoS(byte abyte0[]) {
		if (abyte0 == null)
			return null;
		Vector vector = new Vector();
		int j = 0;
		do {
			if (j > abyte0.length - 4)
				break;
			int k = ((abyte0[j] & 255) << 8) + ((abyte0[j + 1] & 255) << 0);
			StringBuffer stringbuffer = new StringBuffer(abyte0.length);
			j += 2;
			for (k += j; j < k && k < abyte0.length;) {
				int i = abyte0[j++] & 255;
				stringbuffer.append((char) i);
			}

			if (stringbuffer.toString().length() > 0) {
				stringbuffer.append(abyte0[j++]);
				vector.addElement(stringbuffer.toString());
			}
		} while (true);
		return vector;
	}

	public static final byte[] SliceByteArray(byte abyte0[], int i, int j) {
		byte abyte1[] = new byte[j];
		System.arraycopy(abyte0, i, abyte1, 0, j);
		return abyte1;
	}

	public static final byte[] StringToUTF(String s) {
		if (s == null)
			return null;
		try {
			byte[] arrayOfByte1 = s.getBytes("UTF-8");
			byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 2];
			arrayOfByte2[0] = new Integer(arrayOfByte1.length / 256)
					.byteValue();
			arrayOfByte2[1] = new Integer(arrayOfByte1.length % 256)
					.byteValue();
			System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 2,
					arrayOfByte1.length);
			return arrayOfByte2;
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
			System.out
					.println("MQTT Client: Unsupported string encoding - UTF-8");
		}
		return null;
	}

	public static final int toShort(byte abyte0[], int i) {
		return (((short) abyte0[i + 0] & 255) << 8)
				+ ((short) abyte0[i + 1] & 255);
	}

	public static final String UTFToString(byte abyte0[]) {
		return UTFToString(abyte0, 0);
	}

	public static final String UTFToString(byte abyte0[], int i) {
		if (abyte0 == null)
			return null;
		int j = ((abyte0[0 + i] & 255) << 8) + ((abyte0[1 + i] & 255) << 0);
		if (j + 2 > abyte0.length)
			return null;
		String s = null;
		if (j > 0)
			try {
				s = new String(abyte0, i + 2, j, "UTF-8");
			} catch (UnsupportedEncodingException unsupportedencodingexception) {
				System.out
						.println("MQTT Client: Unsupported string encoding - UTF-8");
			}
		else
			s = "";
		return s;
	}

	public static final Vector UTFToStrings(byte abyte0[], int i) {
		if (abyte0 == null)
			return null;
		Vector vector = new Vector();
		int k;
		for (int j = i; j <= abyte0.length - 3; j += k + 2) {
			k = ((abyte0[j] & 255) << 8) + ((abyte0[j + 1] & 255) << 0);
			String s = UTFToString(abyte0, j);
			if (s != null)
				vector.addElement(s);
		}

		return vector;
	}

	public static final String toHexString(byte abyte0[], int i, int j) {
		StringBuffer stringbuffer = new StringBuffer("");
		if (i < 0)
			i = 0;
		for (int k = i; k < i + j && k <= abyte0.length - 1; k++) {
			int l = abyte0[k];
			if (l < 0)
				l += 256;
			if (l < 16)
				stringbuffer.append("0" + Integer.toHexString(l));
			else
				stringbuffer.append(Integer.toHexString(l));
		}

		return stringbuffer.toString();
	}
}

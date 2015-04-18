package com.ibm.mqtt.trace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MQeTraceToBinaryFile extends MQeTraceToBinary implements
		MQeTraceHandler {
	public static short version[] = { 2, 0, 0, 2 };
	private static final String CURRENT_DIRECTORY = ".";
	private static final String DEFAULT_DIRECTORY = ".";
	public static final String DEFAULT_FILE_NAME_PREFIX = "mqe";
	public static final String DEFAULT_FILE_NAME_SUFFIX = ".trc";
	private static final int NEVER_WRAP = -1;
	private static final boolean DEBUG = false;
	private long maxFileSizeBeforeWrap;
	private int filesExistingAtOnce;
	private String fileNameSuffix;
	private String fileNamePrefix;
	private String directoryName;
	private int fileNameIndex;
	public static final long MIN_TRACE_FILE_SIZE = 2096L;
	private static final int DEFAULT_FILES_EXISTING_AT_ONCE = 1;
	static final short FOOTER_BECAUSE_OF_CLOSE = -24001;
	static final short FOOTER_BECAUSE_OF_WRAP = -24000;
	private byte footer[];
	private FileOutputStream currentFileOut;
	File currentFile;
	private long bytesWritten;
	static final short FOOTER_BECAUSE_OF_FINALISE = -24002;

	public MQeTraceToBinaryFile() {
		this(".", "mqe", ".trc", 1, -1L);
	}

	public MQeTraceToBinaryFile(String s, String s1, String s2, int i, long l) {
		maxFileSizeBeforeWrap = -1L;
		filesExistingAtOnce = 1;
		fileNameSuffix = ".trc";
		fileNamePrefix = "mqe";
		directoryName = ".";
		fileNameIndex = 0;
		bytesWritten = 0L;
		if (s == null)
			s = ".";
		directoryName = s;
		if (s1 == null)
			s1 = "mqe";
		fileNamePrefix = s1;
		if (s2 == null)
			s2 = ".trc";
		fileNameSuffix = s2;
		if (i < 1)
			i = 1;
		filesExistingAtOnce = i;
		if (i == 1)
			l = -1L;
		else if (l < 2096L)
			l = 2096L;
		maxFileSizeBeforeWrap = l;
		fileNameIndex = 0;
		footer = super.getFooter((short) -24001);
	}

	String throwableStackTrace(Throwable throwable) {
		StringWriter stringwriter = new StringWriter();
		PrintWriter printwriter = new PrintWriter(stringwriter);
		throwable.printStackTrace(printwriter);
		return stringwriter.toString();
	}

	boolean openFile() {
		boolean flag = true;
		String s = directoryName + System.getProperty("file.separator")
				+ fileNamePrefix + getPaddedIndex() + fileNameSuffix;
		currentFile = new File(s);
		if (currentFile.exists())
			currentFile.delete();
		try {
			bytesWritten = 0L;
			currentFileOut = new FileOutputStream(currentFile);
			writeHeader();
		} catch (IOException ioexception) {
			flag = false;
		}
		return flag;
	}

	String getPaddedIndex() {
		String s = "";
		String s1 = Integer.toString(filesExistingAtOnce);
		int i = s1.length();
		String s2 = Integer.toString(fileNameIndex);
		int j = s2.length();
		for (int k = j; k < i; k++)
			s = s + "0";

		s = s + s2;
		return s;
	}

	boolean writeFooter(short word0) {
		boolean flag = true;
		try {
			currentFileOut.write(getFooter(word0));
			currentFileOut.flush();
		} catch (IOException ioexception) {
			flag = false;
		}
		return flag;
	}

	boolean writeRecord(byte abyte0[]) {
		boolean flag = true;
		if (maxFileSizeBeforeWrap != -1L
				&& (long) abyte0.length + bytesWritten + (long) footer.length > maxFileSizeBeforeWrap) {
			flag = writeFooter((short) -24000);
			if (flag) {
				advanceFileNameIndex();
				flag = openFile();
			}
		}
		if (flag)
			try {
				currentFileOut.write(abyte0);
				bytesWritten += abyte0.length;
				currentFileOut.flush();
			} catch (IOException ioexception) {
				flag = false;
			}
		return flag;
	}

	boolean writeHeader() {
		boolean flag = true;
		byte abyte0[] = getHeader();
		try {
			currentFileOut.write(abyte0);
			currentFileOut.flush();
			bytesWritten += abyte0.length;
		} catch (IOException ioexception) {
			flag = false;
		}
		return flag;
	}

	boolean on() {
		boolean flag = true;
		flag = openFile();
		return flag;
	}

	boolean off() {
		boolean flag = true;
		flag = writeFooter((short) -24001);
		advanceFileNameIndex();
		closeFile();
		return flag;
	}

	void finalise() {
		if (currentFileOut != null) {
			writeFooter((short) -24002);
			closeFile();
		}
	}

	private final void closeFile() {
		try {
			currentFileOut.close();
			currentFileOut = null;
		} catch (IOException ioexception) {
		}
	}

	void advanceFileNameIndex() {
		fileNameIndex++;
		if (fileNameIndex >= filesExistingAtOnce)
			fileNameIndex = 0;
	}

}

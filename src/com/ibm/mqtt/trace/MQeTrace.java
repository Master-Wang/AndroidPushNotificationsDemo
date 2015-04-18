package com.ibm.mqtt.trace;

public class MQeTrace {
	public MQeTrace() {
	}

	public static MQeTraceHandler setHandler(MQeTraceHandler mqetracehandler) {
		MQeTraceHandler mqetracehandler1;
		synchronized (lock) {
			mqetracehandler1 = traceHandler;
			traceHandler = mqetracehandler;
			if (null != traceHandler)
				traceHandler.setFilter(getFilter());
			if (null != mqetracehandler1)
				mqetracehandler1.setFilter(0L);
		}
		return mqetracehandler1;
	}

	public static MQeTraceHandler getHandler() {
		return traceHandler;
	}

	public static void setFilter(long l) {
		synchronized (lock) {
			trace(null, (short) -21300, 1048580L);
			long l1 = filterMask;
			filterMask = l;
			if (traceHandler != null)
				traceHandler.setFilter(l);
			trace(null, (short) -21301, 1048584L);
		}
	}

	public static long getFilter() {
		return filterMask;
	}

	public static void trace(Object obj, short word0, long l) {
		if ((l & filterMask) != 0L) {
			MQeTraceHandler mqetracehandler = traceHandler;
			if (null != mqetracehandler)
				mqetracehandler.traceMessage(obj, word0, l);
		}
	}

	public static void trace(Object obj, short word0, long l, Object obj1) {
		if ((l & filterMask) != 0L) {
			MQeTraceHandler mqetracehandler = traceHandler;
			if (null != mqetracehandler) {
				if (null == obj1)
					obj1 = nullStr;
				mqetracehandler.traceMessage(obj, word0, l, obj1);
			}
		}
	}

	public static void trace(Object obj, short word0, long l, Object obj1,
			Object obj2) {
		if ((l & filterMask) != 0L) {
			MQeTraceHandler mqetracehandler = traceHandler;
			if (null != mqetracehandler) {
				if (null == obj1)
					obj1 = nullStr;
				if (null == obj2)
					obj2 = nullStr;
				mqetracehandler.traceMessage(obj, word0, l, obj1, obj2);
			}
		}
	}

	public static void trace(Object obj, short word0, long l, Object obj1,
			Object obj2, Object obj3) {
		if ((l & filterMask) != 0L) {
			MQeTraceHandler mqetracehandler = traceHandler;
			if (null != mqetracehandler) {
				if (null == obj1)
					obj1 = nullStr;
				if (null == obj2)
					obj2 = nullStr;
				if (null == obj3)
					obj3 = nullStr;
				mqetracehandler.traceMessage(obj, word0, l, obj1, obj2, obj3);
			}
		}
	}

	public static void trace(Object obj, short word0, long l, Object obj1,
			Object obj2, Object obj3, Object obj4) {
		if ((l & filterMask) != 0L) {
			MQeTraceHandler mqetracehandler = traceHandler;
			if (null != mqetracehandler) {
				if (null == obj1)
					obj1 = nullStr;
				if (null == obj2)
					obj2 = nullStr;
				if (null == obj3)
					obj3 = nullStr;
				if (null == obj4)
					obj4 = nullStr;
				mqetracehandler.traceMessage(obj, word0, l, obj1, obj2, obj3,
						obj4);
			}
		}
	}

	public static short version[] = { 2, 0, 0, 2 };
	public static final short TRACE_NUMBER_USER_DEFINED_MIN = 1;
	public static final short TRACE_NUMBER_USER_DEFINED_MAX = 32767;
	public static final long GROUP_MASK_NONE = 0L;
	public static final long GROUP_ERROR = 1L;
	public static final long GROUP_WARNING = 2L;
	public static final long GROUP_ENTRY = 4L;
	public static final long GROUP_EXIT = 8L;
	public static final long GROUP_ADAPTER_CALLING = 16L;
	public static final long GROUP_ADAPTER_RETURNED = 32L;
	public static final long GROUP_RULE_CALLING = 64L;
	public static final long GROUP_RULE_RETURNED = 128L;
	public static final long GROUP_BRIDGE = 256L;
	public static final long GROUP_COMMS_OUTGOING = 512L;
	public static final long GROUP_CHANNEL_MANAGEMENT = 1024L;
	public static final long GROUP_MESSAGE_STATE = 2048L;
	public static final long GROUP_THREAD_CONTROL = 4096L;
	public static final long GROUP_MESSAGE_STORE = 8192L;
	public static final long GROUP_ADMINISTRATION = 16384L;
	public static final long GROUP_EXCEPTION = 32768L;
	public static final long GROUP_JMS = 65536L;
	public static final long GROUP_SECURITY = 131072L;
	public static final long GROUP_COMMS_INCOMING = 262144L;
	public static final long GROUP_TRANSACTION = 524288L;
	public static final long GROUP_API = 1048576L;
	public static final long GROUP_INFO = 2097152L;
	public static final long GROUP_QUEUE_MANAGER = 4194304L;
	public static final long GROUP_MQSERIES = 8388608L;
	public static final long GROUP_MASK_USER_DEFINED = -281474976710656L;
	public static final long GROUP_USER_DEFINED_1 = 281474976710656L;
	public static final long GROUP_USER_DEFINED_2 = 562949953421312L;
	public static final long GROUP_USER_DEFINED_3 = 1125899906842624L;
	public static final long GROUP_USER_DEFINED_4 = 2251799813685248L;
	public static final long GROUP_MASK_IBM_DEFINED = 281474976710655L;
	public static final long GROUP_MASK_ALL = -1L;
	public static final long GROUP_MASK_DEFAULT = -8388609L;
	public static final boolean DEBUG = false;
	private static volatile MQeTraceHandler traceHandler = null;
	private static String lock = "";
	private static String nullStr = "<null>";
	private static volatile long filterMask = -8388609L;
}

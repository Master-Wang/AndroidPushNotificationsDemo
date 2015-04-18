package com.ibm.mqtt.trace;

public interface MQeTraceHandler {
	public abstract void traceMessage(Object obj, short word0, long l);

	public abstract void traceMessage(Object obj, short word0, long l,
			Object obj1);

	public abstract void traceMessage(Object obj, short word0, long l,
			Object obj1, Object obj2);

	public abstract void traceMessage(Object obj, short word0, long l,
			Object obj1, Object obj2, Object obj3);

	public abstract void traceMessage(Object obj, short word0, long l,
			Object obj1, Object obj2, Object obj3, Object obj4);

	public abstract void setFilter(long l);

	public static final short version[] = { 2, 0, 0, 2 };
}

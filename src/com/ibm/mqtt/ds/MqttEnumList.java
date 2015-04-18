package com.ibm.mqtt.ds;

import java.util.Enumeration;

public class MqttEnumList implements Enumeration<Object> {

	private MqttHashTable mqtt_enum;
	private MqttListItem ptr;
	private int count;
	private int size;
	private int index;
	private boolean keys;

	public MqttEnumList(MqttHashTable mqtthashtable, boolean flag) {
		mqtt_enum = mqtthashtable;
		size = mqtt_enum.size();
		keys = flag;
		index = count = 0;
		if ((ptr = mqtt_enum.hashTable[index]) == null)
			ptr = advance(ptr);
	}

	private MqttListItem advance(MqttListItem mqttlistitem) {
		do
			if (index + 1 < mqtt_enum.m_capacity)
				index++;
			else
				return null;
		while ((mqttlistitem = mqtt_enum.hashTable[index]) == null);
		return mqttlistitem;
	}

	public boolean hasMoreElements() {
		return count < size;
	}

	public Object nextElement() {
		MqttListItem mqttlistitem = ptr;
		ptr = ptr.isEnd() ? advance(ptr) : ptr.next;
		count++;
		return keys ? new Long(mqttlistitem.key) : mqttlistitem.data;
	}
}

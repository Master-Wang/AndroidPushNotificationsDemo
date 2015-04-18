package com.ibm.mqtt.ds;

public final class MqttListItem {
	public MqttListItem next;
	public Object data;
	public long key;

	public MqttListItem(long l, MqttListItem mqttlistitem, Object obj) {
		data = obj;
		next = mqttlistitem;
		key = l;
	}

	public boolean isEnd() {
		return next == null;
	}

	public boolean keysMatch(long l) {
		return key == l;
	}

}

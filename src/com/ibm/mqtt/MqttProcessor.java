package com.ibm.mqtt;

import com.ibm.mqtt.packet.MqttConnack;
import com.ibm.mqtt.packet.MqttConnect;
import com.ibm.mqtt.packet.MqttDisconnect;
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

public interface MqttProcessor {
	public abstract boolean supportTopicNameCompression();

	public abstract void process(MqttConnect mqttconnect);

	public abstract void process(MqttConnack mqttconnack);

	public abstract void process(MqttPublish mqttpublish);

	public abstract void process(MqttPuback mqttpuback);

	public abstract void process(MqttPubrec mqttpubrec);

	public abstract void process(MqttPubrel mqttpubrel);

	public abstract void process(MqttPubcomp mqttpubcomp);

	public abstract void process(MqttSubscribe mqttsubscribe);

	public abstract void process(MqttSuback mqttsuback);

	public abstract void process(MqttUnsubscribe mqttunsubscribe);

	public abstract void process(MqttUnsuback mqttunsuback);

	public abstract void process(MqttPingreq mqttpingreq);

	public abstract void process(MqttPingresp mqttpingresp);

	public abstract void process(MqttDisconnect mqttdisconnect);
}

package com.ibm.mqtt.callback;

public interface MqttAdvancedCallback extends MqttSimpleCallback{

    public abstract void published(int i);

    public abstract void subscribed(int i, byte abyte0[]);

    public abstract void unsubscribed(int i);
}

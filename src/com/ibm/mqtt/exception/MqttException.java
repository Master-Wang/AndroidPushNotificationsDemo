package com.ibm.mqtt.exception;

public class MqttException extends Exception{

	private static final long serialVersionUID = 8978311784636402202L;

	private Throwable le;
    
	public MqttException()
    {
        le = null;
    }

    public MqttException(String s)
    {
        super(s);
        le = null;
    }

    public MqttException(Throwable throwable)
    {
        super(throwable != null ? throwable.toString() : null);
        le = null;
        le = throwable;
    }

    public Throwable getCause()
    {
        return le;
    }

    public Throwable initCause(Throwable throwable)
    {
        if(le != null)
            throw new IllegalStateException();
        if(throwable == this)
        {
            throw new IllegalArgumentException();
        } else
        {
            le = throwable;
            return this;
        }
    }

}

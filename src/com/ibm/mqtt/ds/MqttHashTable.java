package com.ibm.mqtt.ds;

import java.util.Enumeration;

public class MqttHashTable
{

    public MqttHashTable()
    {
        this(101);
    }

    public MqttHashTable(int i)
    {
        size = 0;
        recycle_length = 0;
        m_init_capacity = m_capacity = findPower(i);
        m_ceiling = (m_capacity * 3) / 4;
        hashTable = new MqttListItem[m_capacity];
    }

    public void clear()
    {
        for(int i = 0; i < m_capacity; i++)
            hashTable[i] = null;

        size = 0;
    }

    public boolean contains(Object obj)
    {
        for(int i = 0; i < m_capacity; i++)
        {
            MqttListItem mqttlistitem = hashTable[i];
            for(MqttListItem mqttlistitem1 = mqttlistitem; mqttlistitem1 != null; mqttlistitem1 = mqttlistitem1.next)
                if(mqttlistitem1.data.equals(obj))
                    return true;

        }

        return false;
    }

    public boolean containsKey(long l)
    {
        return get(l) != null;
    }

    public Enumeration elements()
    {
        return new MqttEnumList(this, false);
    }

    private int findPower(int i)
    {
        int j = 2;
        do
            j *= 2;
        while(j < i);
        return j;
    }

    public Object get(long l)
    {
        int i = (int)((l >>> 32 ^ l) & (long)(m_capacity - 1));
        MqttListItem mqttlistitem = hashTable[i];
        for(MqttListItem mqttlistitem1 = hashTable[i]; mqttlistitem1 != null; mqttlistitem1 = mqttlistitem1.next)
            if(mqttlistitem1.keysMatch(l))
                return mqttlistitem1.data;

        return null;
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public Enumeration keys()
    {
        return new MqttEnumList(this, true);
    }

    public Object put(long l, Object obj)
    {
        if(size > m_ceiling)
        {
            int i = m_capacity;
            m_capacity = m_capacity << 1;
            m_ceiling = (m_capacity * 3) / 4;
            rehash(i);
            return put(l, obj);
        }
        int j = (int)((l >>> 32 ^ l) & (long)(m_capacity - 1));
        MqttListItem mqttlistitem = hashTable[j];
        if(mqttlistitem == null)
        {
            hashTable[j] = recycleCreate(l, null, obj);
            size++;
            return null;
        }
        for(MqttListItem mqttlistitem1 = mqttlistitem; mqttlistitem1 != null; mqttlistitem1 = mqttlistitem1.next)
            if(mqttlistitem1.keysMatch(l))
            {
                Object obj1 = mqttlistitem1.data;
                mqttlistitem1.data = obj;
                return obj1;
            }

        hashTable[j] = recycleCreate(l, mqttlistitem, obj);
        size++;
        return null;
    }

    private MqttListItem recycleCreate(long l, MqttListItem mqttlistitem, Object obj)
    {
        if(recycle_bin != null)
        {
            MqttListItem mqttlistitem1 = recycle_bin;
            recycle_bin = recycle_bin.next;
            recycle_length--;
            mqttlistitem1.key = l;
            mqttlistitem1.next = mqttlistitem;
            mqttlistitem1.data = obj;
            return mqttlistitem1;
        } else
        {
            return new MqttListItem(l, mqttlistitem, obj);
        }
    }

    private void rehash(int i)
    {
        MqttListItem amqttlistitem[] = hashTable;
        MqttListItem amqttlistitem1[] = new MqttListItem[m_capacity];
        hashTable = amqttlistitem1;
        for(int j = 0; j < i; j++)
        {
            for(MqttListItem mqttlistitem = amqttlistitem[j]; mqttlistitem != null;)
            {
                MqttListItem mqttlistitem1 = mqttlistitem;
                mqttlistitem = mqttlistitem.next;
                long l = mqttlistitem1.key;
                int k = (int)((l >>> 32 ^ l) & (long)(m_capacity - 1));
                mqttlistitem1.next = amqttlistitem1[k];
                amqttlistitem1[k] = mqttlistitem1;
            }

        }

    }

    public Object remove(long l)
    {
        if(size < m_ceiling / 4 && size >= m_init_capacity << 1)
        {
            int i = m_capacity;
            m_capacity = m_capacity >> 1;
            m_ceiling = (m_capacity * 3) / 4;
            rehash(i);
            return remove(l);
        }
        int j = (int)((l >>> 32 ^ l) & (long)(m_capacity - 1));
        MqttListItem mqttlistitem = null;
        for(MqttListItem mqttlistitem1 = hashTable[j]; mqttlistitem1 != null; mqttlistitem1 = mqttlistitem1.next)
        {
            if(mqttlistitem1.keysMatch(l))
            {
                if(mqttlistitem == null)
                    hashTable[j] = mqttlistitem1.next;
                else
                    mqttlistitem.next = mqttlistitem1.next;
                size--;
                if(recycle_length < size / 8)
                {
                    mqttlistitem1.next = recycle_bin;
                    recycle_bin = mqttlistitem1;
                    recycle_length++;
                }
                return mqttlistitem1.data;
            }
            mqttlistitem = mqttlistitem1;
        }

        return null;
    }

    public int size()
    {
        return size;
    }

    public final void view()
    {
        for(int i = 0; i < m_capacity; i++)
        {
            System.out.print("\nBucket " + i + ":");
            for(MqttListItem mqttlistitem = hashTable[i]; mqttlistitem != null; mqttlistitem = mqttlistitem.next)
                System.out.print(" " + mqttlistitem.data.toString());

        }

        System.out.println("\nSize = " + size + "\n");
    }

    private static final int INITIAL_CAPACITY = 101;
    private static final int LOAD_FACTOR_NUMERATOR = 3;
    private static final int LOAD_FACTOR_DENOMINATOR = 4;
    private static int m_init_capacity;
    public int m_capacity;
    private int m_ceiling;
    private int size;
    public MqttListItem hashTable[];
    private MqttListItem recycle_bin;
    private int recycle_length;
}


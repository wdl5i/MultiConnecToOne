package com.wdl.common;

import io.vertx.core.buffer.Buffer;

/**
 * Created by wdl on 2017/11/18.
 */
public class DataEvent {
    private byte length;
    private short id;
    private String content;

    public DataEvent() {
    }

    public DataEvent buildFromOther(DataEvent other) {
        if(!other.idUnAssigned()) {
            this.setId(other.getId());
        }
        this.setLength(other.getLength());
        this.setContent(other.getContent());
        return this;
    }

    public DataEvent(byte length, String content) {
        this.length = length;
        this.content = content;
        this.id = Short.MAX_VALUE;
    }

    public DataEvent(byte length, short id, String content) {
        this.length = length;
        this.id = id;
        this.content = content;
    }

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public static Buffer toBuffer(DataEvent event) {
        return Buffer.buffer().appendByte(event.length).appendShort(event.getId()).appendString(event.content);
    }

    public static DataEvent fromBuffer(Buffer buffer) {
        byte length = buffer.getByte(0);
        Short id = buffer.getShort(1);
        String content = buffer.getString(3, 3 + length);
        DataEvent dataEvent = new DataEvent(length, id, content);
        return dataEvent;
    }

    public boolean idUnAssigned() {
        return this.getId() == Short.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "DataEvent{" +
                "length=" + length +
                ", id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}

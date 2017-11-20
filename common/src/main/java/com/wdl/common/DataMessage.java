package com.wdl.common;

/**
 * Created by wdl on 2017/11/20.
 */
public class DataMessage {
    public static final String TYPE_CONNECTION = "Connection";
    public static final String TYPE_MESSAGE = "Message";

    public static final byte ACTION_MESSAGE = 0;
    public static final byte ACTION_CONNECTION_ADD = 1;
    public static final byte ACTION_CONNECTION_REMOVE = 2;

    private byte action;
    private short id;
    private String content;

    public DataMessage(byte action, short id) {
        this.action = action;
        this.id = id;
    }

    public DataMessage(byte action, short id, String content) {
        this.action = action;
        this.id = id;
        this.content = content;
    }

    public byte getAction() {
        return action;
    }

    public void setAction(byte action) {
        this.action = action;
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
}

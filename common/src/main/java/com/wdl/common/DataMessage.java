package com.wdl.common;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;

import java.io.*;

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

    public static class DataMessageCodec implements MessageCodec<DataMessage, DataMessage> {
        @Override
        public void encodeToWire(Buffer buffer, DataMessage dataMessage) {
            final ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o;
            try {
                o = new ObjectOutputStream(b);
                o.writeObject(dataMessage);
                o.close();
                buffer.appendBytes(b.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public DataMessage decodeFromWire(int pos, Buffer buffer) {
            final ByteArrayInputStream b = new ByteArrayInputStream(buffer.getBytes());
            ObjectInputStream o = null;
            DataMessage msg = null;
            try {
                o = new ObjectInputStream(b);
                msg = (DataMessage) o.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return msg;
        }

        @Override
        public DataMessage transform(DataMessage dataMessage) {
            return dataMessage;
        }

        @Override
        public String name() {
            return "DataMessageCodec";
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }
    }
}

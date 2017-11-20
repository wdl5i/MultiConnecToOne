package com.wdl.common;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

import java.io.*;

/**
 * Created by wdl on 2017/11/20.
 */
public class DataMessage implements Serializable {
    public static final String TYPE_CONNECTION = "Connection";
    public static final String TYPE_MESSAGE = "Message";

    public static final byte ACTION_MESSAGE = 0;
    public static final byte ACTION_CONNECTION_ADD = 1;
    public static final byte ACTION_CONNECTION_REMOVE = 2;

    private int action;
    private int id;
    private String content;

    public DataMessage(int action, int id) {
        this.action = action;
        this.id = id;
    }

    public DataMessage(int action, int id, String content) {
        this.action = action;
        this.id = id;
        this.content = content;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
            /*final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(dataMessage);
                oos.close();
                buffer.appendBytes(baos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            // Easiest ways is using JSON object
            JsonObject jsonToEncode = new JsonObject();
            jsonToEncode.put("action", dataMessage.getAction());
            jsonToEncode.put("id", dataMessage.getId());
            jsonToEncode.put("content", dataMessage.getContent());

            // Encode object to string
            String jsonToStr = jsonToEncode.encode();

            // Length of JSON: is NOT characters count
            int length = jsonToStr.getBytes().length;

            // Write data into given buffer
            buffer.appendInt(length);
            buffer.appendString(jsonToStr);
        }

        @Override
        public DataMessage decodeFromWire(int pos, Buffer buffer) {
            /*byte[] bufferBytes = buffer.getBytes();
            final ByteArrayInputStream bais = new ByteArrayInputStream(bufferBytes);
            ObjectInputStream ois = null;
            DataMessage msg = null;
            try {
                ois = new ObjectInputStream(bais);
                msg = (DataMessage) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return msg;*/
            int _pos = pos;

            // Length of JSON
            int length = buffer.getInt(_pos);

            // Get JSON string by it`s length
            // Jump 4 because getInt() == 4 bytes
            String jsonStr = buffer.getString(_pos+=4, _pos+=length);
            JsonObject contentJson = new JsonObject(jsonStr);

            // Get fields
            int action = contentJson.getInteger("action");
            int id = contentJson.getInteger("id");
            String content = contentJson.getString("content");

            // We can finally create custom message object
            return new DataMessage(action, id, content);
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

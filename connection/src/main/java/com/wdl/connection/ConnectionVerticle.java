package com.wdl.connection;

import com.wdl.common.ClientId;
import com.wdl.common.DataEvent;
import com.wdl.common.DataMessage;
import com.wdl.common.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

import java.util.HashMap;
import java.util.Map;

public class ConnectionVerticle extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    final DataEvent event = new DataEvent();

    Map<Short, NetSocket> idSocketMap = new HashMap<>();

    @Override
    public void start() {
        NetServer server = vertx.createNetServer();
        EventBus eventBus = vertx.eventBus();
        eventBus.registerDefaultCodec(DataMessage.class, new DataMessage.DataMessageCodec());

        server.connectHandler(socket -> {
            socket.handler(buffer -> {
                //logger.debug(socket.remoteAddress().host());
                try {
                    DataEvent received = DataEvent.fromBuffer(buffer);
                    if (received.idUnAssigned()) {
                        received.setId(ClientId.pull());
                    }
                    event.buildFromOther(received);
                    //新连接
                    if (!idSocketMap.containsKey(event.getId())) {
                        eventBus.send(DataMessage.TYPE_CONNECTION,
                                new DataMessage(DataMessage.ACTION_CONNECTION_ADD, event.getId()),
                                asyncResult -> {
                                    socket.write(Buffer.buffer().appendShort(event.getId()));
                                }
                        );
                    }
                    //回复数据

                    eventBus.send(DataMessage.TYPE_MESSAGE,
                            new DataMessage(DataMessage.ACTION_MESSAGE, event.getId(), event.getContent()),
                            asyncResult -> {
                                Message message = asyncResult.result();
                                if(message != null && socket != null)
                                    socket.write(Buffer.buffer().appendString(message.body().toString()));
                            });
                    idSocketMap.put(event.getId(), socket);
                    logger.info("I received some bytes: " + event);
                } catch (IllegalArgumentException e) {
                    socket.close();
                }
            }).closeHandler(v -> {
                logger.info("a client has been closed with id : " + event.getId());
                ClientId.offer(event.getId());
                idSocketMap.remove(event.getId());
                socket.close();
                eventBus.send(DataMessage.TYPE_CONNECTION,
                        new DataMessage(DataMessage.ACTION_CONNECTION_REMOVE, event.getId())
                );
            });
        });

        server.listen(1234, "localhost", res -> {
            if (res.succeeded()) {
                logger.info("Server is now listening!");
            } else {
                logger.info("Failed to bind!");
            }
        });

        //接受来自backend的指令
        eventBus.<DataMessage>consumer(DataMessage.TYPE_CONNECTION,
                message -> {
                    DataMessage dataMessage = message.body();
                    if(dataMessage.getAction() == DataMessage.ACTION_CONNECTION_REMOVE) {
                        NetSocket socket = idSocketMap.get(dataMessage.getId());
                        if(socket != null)
                            socket.close();
                    }
                });


        logger.info("Connection started");
    }

    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        options.setClusterHost("192.168.1.55");
        Runner.runClusteredExample(ConnectionVerticle.class, options);
    }

}

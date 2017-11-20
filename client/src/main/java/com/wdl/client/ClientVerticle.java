package com.wdl.client;

import com.wdl.common.DataEvent;
import com.wdl.common.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

public class ClientVerticle extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DataEvent event = new DataEvent();

    @Override
    public void start() {
        /*getVertx().setPeriodic(1000L, id -> {
            log.info("sending message ping...");
            getVertx().eventBus().send("ping-pong", "ping", response -> {
                if (response.succeeded()) log.info("success");
                else log.info("error?");
            });
        });*/
        NetClientOptions options = new NetClientOptions()
                .setConnectTimeout(10000)
                .setReconnectAttempts(10)
                .setReconnectInterval(500);

        NetClient client = vertx.createNetClient(options);
            client.connect(1234, "localhost", res -> {
                if (res.succeeded()) {
                    logger.debug("Connected!");
                    NetSocket socket = res.result();
                    DataEvent other = new DataEvent( (byte) 5, "hello");
                    event.buildFromOther(other);
                    socket.write(DataEvent.toBuffer(event));
                    socket.handler(buffer -> {
                        short receivedId = buffer.getShort(0);
                        if(event.idUnAssigned()) {
                            event.setId(receivedId);
                        }
                        logger.info("recv from sever : " + receivedId);
                    });
                } else {
                    logger.info("Failed to connect: " + res.cause().getMessage());
                }
            });

        logger.info("Client started");
    }

    public static void main(String[] args) {
        Runner.runExample(ClientVerticle.class);
    }

}

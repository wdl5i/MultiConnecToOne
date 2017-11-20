package com.wdl.logic;

import com.wdl.common.DataMessage;
import com.wdl.common.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class LogicVerticle extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();
        eventBus
                .<DataMessage>consumer(DataMessage.TYPE_CONNECTION, message -> {
                    DataMessage dataMessage = message.body();
                    if (dataMessage.getAction() == DataMessage.ACTION_CONNECTION_ADD) {
                        logger.info("A client connected, " + dataMessage.getId());
                    } else if (dataMessage.getAction() == DataMessage.ACTION_CONNECTION_REMOVE) {
                        logger.info("A client disConnected, " + dataMessage.getId());
                    }
                });
        eventBus
                .<DataMessage>consumer(DataMessage.TYPE_MESSAGE, message -> {
                    DataMessage dataMessage = message.body();
                    logger.info("client " + dataMessage.getId() + "send message:" + dataMessage.getContent());
                });
        eventBus.send(DataMessage.TYPE_MESSAGE, new DataMessage(DataMessage.ACTION_CONNECTION_REMOVE, (short)-32768));


        logger.info("Logic server started");
    }

    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        options.setClusterHost("192.168.1.102");
        Runner.runClusteredExample(LogicVerticle.class, options);
    }

}

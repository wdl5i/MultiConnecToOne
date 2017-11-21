package com.wdl.logic;

import io.vertx.core.*;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Created by wdl on 2017/11/20.
 */
@RunWith(VertxUnitRunner.class)
public class LogicTest {
    private Vertx vertx;

    final AtomicBoolean loaded = new AtomicBoolean(false);

    @Before
    public void setUp(TestContext context) throws IOException {
        String exampleDir = "logic/src/main/java/";
        VertxOptions options = new VertxOptions();
        options.setClusterHost("192.168.1.102");
        try {
            // We need to use the canonical file. Without the file name is .
            File current = new File(".").getCanonicalFile();
            if (exampleDir.startsWith(current.getName())  && ! exampleDir.equals(current.getName())) {
                exampleDir = exampleDir.substring(current.getName().length() + 1);
            }
        } catch (IOException e) {
            // Ignore it.
        }

        System.setProperty("vertx.cwd", exampleDir);
        Consumer<Vertx> runner = vertx -> {
            try {
                vertx.deployVerticle(LogicVerticle.class.getName());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        if (options.isClustered()) {
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    vertx = res.result();
                    runner.accept(vertx);
                } else {
                    res.cause().printStackTrace();
                }
            });
        }
    }

    @Test
    public void printSomething(TestContext context) {

        Awaitility.waitAtMost(Duration.ONE_MINUTE).await().untilTrue(loaded);
        System.out.println("Print from method printSomething()");
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        vertx.close(context.asyncAssertSuccess());

    }

    @Test
    public void closeClient(TestContext context) throws Exception {

    }
}

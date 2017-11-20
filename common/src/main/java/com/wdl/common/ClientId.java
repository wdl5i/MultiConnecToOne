package com.wdl.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by wdl on 2017/11/19.
 */
public class ClientId {
    public static final int MAX_SIZE = 64000;

    private static BlockingQueue<Short> freeIdQueue = new ArrayBlockingQueue(MAX_SIZE);

    static {
        for(short i = Short.MIN_VALUE; i < Short.MIN_VALUE + MAX_SIZE; i++) {
            try {
                freeIdQueue.add(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static short pull() {
        return freeIdQueue.poll();
    }

    public static boolean offer(short s) {
        return freeIdQueue.offer(s);
    }

}

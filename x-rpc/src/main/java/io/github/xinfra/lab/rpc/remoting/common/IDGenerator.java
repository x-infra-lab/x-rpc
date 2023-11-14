package io.github.xinfra.lab.rpc.remoting.common;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private static AtomicInteger requestIdGenerator = new AtomicInteger(0);

    public static Integer nextRequestId() {
        return requestIdGenerator.getAndIncrement();
    }

}

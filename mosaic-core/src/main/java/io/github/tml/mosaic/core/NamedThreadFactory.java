package io.github.tml.mosaic.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    private final String poolName;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public NamedThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName("mosaic-"+poolName + "-" + threadNumber.getAndIncrement());
        return t;
    }
}

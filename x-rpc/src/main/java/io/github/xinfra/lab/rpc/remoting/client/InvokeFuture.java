package io.github.xinfra.lab.rpc.remoting.client;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class InvokeFuture<Message> {

    private final CountDownLatch countDownLatch;

    private Message result;

    public InvokeFuture() {
        this.countDownLatch = new CountDownLatch(1);
    }

    public void finish(Message result) {
        this.result = result;
        countDownLatch.countDown();
    }

    public Message get() throws InterruptedException {
        countDownLatch.await();
        return result;
    }

    public Message get(long timeout, TimeUnit unit) throws InterruptedException {
        boolean finished = countDownLatch.await(timeout, unit);
        if (!finished) {
            return null;
        }
        return result;
    }
}

package io.github.xinfra.lab.rpc.remoting.client;


import io.github.xinfra.lab.rpc.remoting.protocol.Message;
import io.netty.util.Timeout;
import lombok.Getter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class InvokeFuture {

    @Getter
    private int requestId;

    private final CountDownLatch countDownLatch;

    private Message result;

    private Timeout timeout;

    private InvokeCallBack invokeCallBack;

    public InvokeFuture(int requestId) {
        this.requestId = requestId;
        this.countDownLatch = new CountDownLatch(1);
    }

    public void addTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    public void addCallBack(InvokeCallBack invokeCallBack) {
        this.invokeCallBack = invokeCallBack;
    }

    public void tryExecuteCallBack() {
        // TODO
    }

    public void finish(Message result) {
        this.result = result;
        countDownLatch.countDown();
    }

    public boolean isDone() {
        return countDownLatch.getCount() <= 0;
    }

    public Message await() throws InterruptedException {
        countDownLatch.await();
        return result;
    }

    /**
     * @param timeout
     * @param unit
     * @return null if timeout
     * @throws InterruptedException
     */
    public Message await(long timeout, TimeUnit unit) throws InterruptedException {
        boolean finished = countDownLatch.await(timeout, unit);
        if (!finished) {
            return null;
        }
        return result;
    }

    public boolean cancelTimeout() {
        if (timeout != null) {
            return timeout.cancel();
        }
        return false;
    }
}

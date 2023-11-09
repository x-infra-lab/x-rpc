package io.github.xinfra.lab.rpc.remoting.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class InvokeFuture implements Future {


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        // TODO
        return false;
    }

    @Override
    public boolean isCancelled() {
        // TODO
        return false;
    }

    @Override
    public boolean isDone() {
        // TODO
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        // TODO
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // TODO
        return null;
    }
}

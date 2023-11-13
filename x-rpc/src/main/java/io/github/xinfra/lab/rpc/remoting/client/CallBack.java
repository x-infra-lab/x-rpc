package io.github.xinfra.lab.rpc.remoting.client;

public interface CallBack<R> {
    void complete(R result);
}

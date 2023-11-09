package io.github.xinfra.lab.rpc.remoting.client;

public interface CallBack<R> {

    void onSuccess(R result);

    void onException(Throwable t);
}

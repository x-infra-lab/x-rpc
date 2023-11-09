package io.github.xinfra.lab.rpc.remoting.client;

import io.github.xinfra.lab.rpc.remoting.Endpoint;

import java.util.concurrent.Future;

public interface RemotingClient {
    <R> R syncCall(Object request, Endpoint endpoint);

    <R> Future<R> asyncCall(Object request, Endpoint endpoint);

    <R> void asyncCall(Object request, Endpoint endpoint, CallBack<R> callBack);

    void oneway(Object request, Endpoint endpoint);
}

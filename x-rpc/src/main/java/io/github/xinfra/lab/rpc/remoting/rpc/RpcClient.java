package io.github.xinfra.lab.rpc.remoting.rpc;

import io.github.xinfra.lab.rpc.remoting.client.CallBack;
import io.github.xinfra.lab.rpc.remoting.Endpoint;

import java.util.concurrent.Future;

public class RpcClient {

    public <R> R syncCall(Object request, Endpoint endpoint) {
        // todo
        return null;
    }


    public <R> Future<R> asyncCall(Object request, Endpoint endpoint) {
        // todo
        return null;
    }


    public <R> void asyncCall(Object request, Endpoint endpoint, CallBack<R> callBack) {
        // todo
    }


    public void oneway(Object request, Endpoint endpoint) {
        // todo
    }

}

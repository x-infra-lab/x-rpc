package io.github.xinfra.lab.rpc.remoting.client;

import io.github.xinfra.lab.rpc.remoting.protocol.Message;

public interface InvokeCallBack {
    void complete(Message result);
}

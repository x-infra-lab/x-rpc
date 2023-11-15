package io.github.xinfra.lab.rpc.remoting.client;

import io.github.xinfra.lab.rpc.remoting.message.Message;

public interface InvokeCallBack {
    void complete(Message result);
}

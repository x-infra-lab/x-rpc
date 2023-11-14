package io.github.xinfra.lab.rpc.remoting.protocol;

import io.github.xinfra.lab.rpc.remoting.codec.Decoder;
import io.github.xinfra.lab.rpc.remoting.codec.Encoder;

import java.util.concurrent.Executor;

public interface Protocol {

    Encoder encoder();

    Decoder decoder();

    Executor callBackExecutor();
    
}

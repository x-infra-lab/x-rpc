package io.github.xinfra.lab.rpc.remoting.protocol;

import io.github.xinfra.lab.rpc.remoting.codec.Decoder;
import io.github.xinfra.lab.rpc.remoting.codec.Encoder;

public interface Protocol {

    ProtocolType type();

    Encoder encoder();

    Decoder decoder();
    
}

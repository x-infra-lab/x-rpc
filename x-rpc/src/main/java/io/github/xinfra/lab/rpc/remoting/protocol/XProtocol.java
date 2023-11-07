package io.github.xinfra.lab.rpc.remoting.protocol;

import io.github.xinfra.lab.rpc.remoting.codec.Decoder;
import io.github.xinfra.lab.rpc.remoting.codec.Encoder;

/**
 * x-protocol
 * <p>
 * request definition:
 * <p>
 * ｜protocol:x byte|protocol-version:1byte|message-type:1byte|request-id:int|serialization-type:1byte|type-length:short|header-length:short]content-length:int|type|header|content|
 */
public class XProtocol implements Protocol {

    @Override
    public Encoder encoder() {
        return null;
    }

    @Override
    public Decoder decoder() {
        return null;
    }
}

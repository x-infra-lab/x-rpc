package io.github.xinfra.lab.rpc.remoting.protocol;

import io.github.xinfra.lab.rpc.remoting.codec.Decoder;
import io.github.xinfra.lab.rpc.remoting.codec.Encoder;

/**
 * x-protocol
 * <p>
 * request definition:
 * <p>
 * ｜protocol:bytes|message-type:byte|request-id:int|serialization-type:byte|content-type-length:short|header-length:short|content-length:int|content-type|header|content|
 * <p>
 * response definition:
 * <p>
 * ｜protocol:bytes|message-type:byte|request-id:int|serialization-type:byte|status:short|content-type-length:short|header-length:short]content-length:int|content-type|header|content|
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

package io.github.xinfra.lab.rpc.remoting.message;

import io.github.xinfra.lab.rpc.remoting.serialization.SerializationType;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * request definition:
 * <p>
 * ｜protocol:bytes|message-type:byte|request-id:int|serialization-type:byte|content-type-length:short|header-length:short|content-length:int|content-type|header|content|
 * <p>
 */

@Setter
@Getter
public class RPCRequestMessage extends RPCMessage {

    public RPCRequestMessage(int id) {
        this(id, SerializationType.HESSION);
    }

    public RPCRequestMessage(int id, SerializationType serializationType) {
        super(id, MessageType.request, serializationType);
    }
}

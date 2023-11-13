package io.github.xinfra.lab.rpc.remoting.protocol;

import io.github.xinfra.lab.rpc.remoting.serialization.SerializationType;
import lombok.Getter;
import lombok.Setter;

/**
 * response definition:
 * <p>
 * ｜protocol:bytes|message-type:byte|request-id:int|serialization-type:byte|status:short|content-type-length:short|header-length:short]content-length:int|content-type|header|content|
 */

@Setter
@Getter
public class RPCResponseMessage extends RPCMessage {

    @Setter
    @Getter
    private short status;

    public RPCResponseMessage(int id) {
        this(id, SerializationType.HESSION);
    }

    public RPCResponseMessage(int id, SerializationType serializationType) {
        super(id, MessageType.response, serializationType);
    }
}

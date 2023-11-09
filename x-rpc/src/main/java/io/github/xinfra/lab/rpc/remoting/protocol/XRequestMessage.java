package io.github.xinfra.lab.rpc.remoting.protocol;

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
public class XRequestMessage implements Message {

    private ProtocolType protocolType;

    private MessageType messageType;

    private int requestId;

    private SerializationType serializationType;

    private String contentType;

    private String header;

    private Object content;
}

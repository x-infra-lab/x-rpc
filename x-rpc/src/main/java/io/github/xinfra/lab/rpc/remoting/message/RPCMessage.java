package io.github.xinfra.lab.rpc.remoting.message;

import io.github.xinfra.lab.rpc.remoting.message.Message;
import io.github.xinfra.lab.rpc.remoting.message.MessageType;
import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolType;
import io.github.xinfra.lab.rpc.remoting.serialization.SerializationType;
import lombok.Getter;
import lombok.Setter;

public abstract class RPCMessage implements Message {
    private int id;

    private MessageType messageType;

    private ProtocolType protocolType;

    private SerializationType serializationType;

    @Setter
    @Getter
    private String contentType;

    @Setter
    @Getter
    private String header;

    @Setter
    @Getter
    private Object content;

    public RPCMessage(int id, MessageType messageType, SerializationType serializationType) {
        this.id = id;
        this.messageType = messageType;
        this.protocolType = ProtocolType.RPC;
        this.serializationType = serializationType;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public MessageType messageType() {
        return messageType;
    }

    @Override
    public ProtocolType protocolType() {
        return protocolType;
    }

    @Override
    public SerializationType serializationType() {
        return serializationType;
    }
}

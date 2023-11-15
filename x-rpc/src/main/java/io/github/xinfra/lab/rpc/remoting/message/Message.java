package io.github.xinfra.lab.rpc.remoting.message;

import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolType;
import io.github.xinfra.lab.rpc.remoting.serialization.SerializationType;

public interface Message {

    int id();

    MessageType messageType();

    ProtocolType protocolType();

    SerializationType serializationType();

}

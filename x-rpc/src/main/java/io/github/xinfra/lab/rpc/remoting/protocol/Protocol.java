package io.github.xinfra.lab.rpc.remoting.protocol;

import io.github.xinfra.lab.rpc.remoting.message.MessageHandler;
import io.github.xinfra.lab.rpc.remoting.codec.Decoder;
import io.github.xinfra.lab.rpc.remoting.codec.Encoder;
import io.github.xinfra.lab.rpc.remoting.message.HeartbeatTrigger;
import io.github.xinfra.lab.rpc.remoting.message.MessageFactory;


public interface Protocol {

    Encoder encoder();

    Decoder decoder();

    MessageHandler messageHandler();

    MessageFactory messageFactory();

    HeartbeatTrigger heartbeatTrigger();
}

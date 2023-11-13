package io.github.xinfra.lab.rpc.remoting.protocol;


public interface MessageFactory {

    Message createSendFailMessage(Throwable cause);

    Message createTimeoutMessage();
}

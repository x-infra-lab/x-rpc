package io.github.xinfra.lab.rpc.remoting.protocol;


import java.net.SocketAddress;

public interface MessageFactory {

    Message createSendFailMessage(Throwable cause);

    Message createSendFailMessage(SocketAddress remoteAddress, Throwable cause);

    Message createTimeoutMessage(SocketAddress remoteAddress);

}

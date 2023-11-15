package io.github.xinfra.lab.rpc.remoting.message;

public enum MessageType {
    request,
    response,
    onewayRequest,
    heartbeatRequest,
    heartbeatResponse,
    ;

    public byte data() {
        return (byte) this.ordinal();
    }

    public MessageType valueOf(byte data) {
        return MessageType.values()[data];
    }
}

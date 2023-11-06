package io.github.xinfra.lab.rpc.remoting.protocol;

public enum ProtocolType {
    X;


    public byte toProtocolCode() {
        return (byte) this.ordinal();
    }

    public static ProtocolType valueOfProtocolCode(byte protocolCode) {
        return ProtocolType.values()[protocolCode];
    }
}

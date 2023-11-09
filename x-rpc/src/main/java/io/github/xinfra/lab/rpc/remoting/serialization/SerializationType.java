package io.github.xinfra.lab.rpc.remoting.serialization;

public enum SerializationType {

    HESSION;

    public byte data() {
        return (byte) this.ordinal();
    }

    public SerializationType valueOf(byte data) {
        return SerializationType.values()[data];
    }
}

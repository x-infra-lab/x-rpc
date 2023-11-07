package io.github.xinfra.lab.rpc.remoting.protocol;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public enum ProtocolType {
    X("x".getBytes(StandardCharsets.UTF_8));

    byte[] protocolCode;

    ProtocolType(byte[] protocolCode) {
        this.protocolCode = protocolCode;
    }

    public byte[] protocolCode() {
        return this.protocolCode;
    }

    public static ProtocolType valueOf(byte[] protocolCode) {
        for (ProtocolType type : values()) {
            if (Arrays.equals(type.protocolCode, protocolCode)) {
                return type;
            }
        }
        return null;
    }
}

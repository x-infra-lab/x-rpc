package io.github.xinfra.lab.rpc.remoting;


import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolType;
import io.github.xinfra.lab.rpc.remoting.serialization.SerializationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Properties;

@EqualsAndHashCode
@Getter
@Setter
public class Endpoint {

    private String ip;

    private int port;

    private int connectTimeoutMills;

    private ProtocolType protocolType;

    private SerializationType serializationType;

    private int connNum = 1;

    private boolean connWarmup;

    private Properties properties;
}

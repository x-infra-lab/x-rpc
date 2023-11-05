package io.github.xinfra.lab.rpc.registry;


import lombok.Getter;
import lombok.Setter;

import java.util.Properties;

@Getter
@Setter
public class ProviderInfo {

    private String ip;

    private int port;

    private String protocol;

    private byte protocolVersion;

    private String group;

    private Properties properties;
}

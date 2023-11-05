package io.github.xinfra.lab.rpc.remoting;


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

    private long connectTimeoutMills;

    private byte protocol;

    private byte version = 0;

    private int connNum = 1;

    private boolean connWarmup;

    private Properties properties;
}

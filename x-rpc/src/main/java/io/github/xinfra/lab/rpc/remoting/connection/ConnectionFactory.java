package io.github.xinfra.lab.rpc.remoting.connection;

import java.net.URL;

public interface ConnectionFactory {
    Connection create(URL url);
}

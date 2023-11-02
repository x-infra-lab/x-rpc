package io.github.xinfra.lab.rpc.remoting.connection;

import java.net.URL;

public interface ConnectionManager {

    Connection getConnection(URL url);
}

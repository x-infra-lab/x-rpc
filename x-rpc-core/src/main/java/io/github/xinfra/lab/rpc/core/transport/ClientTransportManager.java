/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.xinfra.lab.rpc.core.transport;

import io.github.xinfra.lab.rpc.config.TransportClientConfig;
import io.github.xinfra.lab.rpc.config.TransportConfig;
import io.github.xinfra.lab.rpc.transport.ClientTransport;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClientTransportManager implements Closeable {
  private Map<TransportClientConfig, ClientTransport> clientTransportMap = new HashMap<>();

  public synchronized ClientTransport getClientTransport(TransportConfig transportConfig) {
    TransportClientConfig transportClientConfig = transportConfig.transportClientConfig();
    ClientTransport clientTransport = clientTransportMap.get(transportClientConfig);
    if (clientTransport == null) {
      clientTransport =
          ClientTransportFactory.create(transportConfig.transportType(), transportClientConfig);
      clientTransportMap.put(transportClientConfig, clientTransport);
    }
    return clientTransport;
  }

  @Override
  public void close() throws IOException {
    IOException ex = null;
    for (ClientTransport clientTransport : clientTransportMap.values()) {
      try {
        clientTransport.close();
      } catch (IOException ioe) {
        if (ex == null) {
          ex = new IOException("ClientTransportManager close fail.");
        }
        ex.addSuppressed(new IOException(clientTransport + " close fail.", ioe));
      }
    }
    if (ex != null) {
      throw ex;
    }
  }
}

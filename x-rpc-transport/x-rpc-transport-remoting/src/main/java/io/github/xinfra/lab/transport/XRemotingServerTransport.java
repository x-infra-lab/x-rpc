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
package io.github.xinfra.lab.transport;

import io.github.xinfra.lab.remoting.rpc.server.RpcServer;
import io.github.xinfra.lab.remoting.rpc.server.RpcServerConfig;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.config.TransportServerConfig;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.transport.ServerTransport;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XRemotingServerTransport implements ServerTransport {
  private XRemotingTransportServerConfig transportServerConfig;
  private RpcServer rpcServer;
  protected Map<String, Invoker> invokerMap = new ConcurrentHashMap<>();

  public XRemotingServerTransport(TransportServerConfig transportServerConfig) {
    if (!(transportServerConfig instanceof XRemotingTransportServerConfig)) {
      throw new IllegalArgumentException(
          "transportServerConfig must be XRemotingTransportServerConfig");
    }
    this.transportServerConfig = (XRemotingTransportServerConfig) transportServerConfig;

    RpcServerConfig rpcServerConfig = new RpcServerConfig();
    rpcServerConfig.setPort(transportServerConfig.port());
    this.rpcServer = new RpcServer(rpcServerConfig);
    rpcServer.registerUserProcessor(new RpcRequestProcessor(this));
    this.rpcServer.startup();
  }

  @Override
  public void register(ServiceConfig<?> serviceConfig, Invoker invoker) {
    String serviceInterfaceName = serviceConfig.getServiceInterfaceName();
    Invoker prevInvoker = invokerMap.putIfAbsent(serviceInterfaceName, invoker);
    if (prevInvoker != null) {
      throw new IllegalStateException("duplicate register service: " + serviceInterfaceName);
    }
  }

  @Override
  public void unRegister(ServiceConfig<?> serviceConfig, Invoker invoker) {
    // todo
  }

  @Override
  public InetSocketAddress address() {
    return (InetSocketAddress) rpcServer.localAddress();
  }

  @Override
  public void close() throws IOException {
    rpcServer.shutdown();
  }
}

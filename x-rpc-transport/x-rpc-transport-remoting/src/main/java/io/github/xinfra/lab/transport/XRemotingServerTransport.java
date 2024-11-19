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

import io.github.xinfra.lab.remoting.processor.UserProcessor;
import io.github.xinfra.lab.remoting.rpc.server.RpcServer;
import io.github.xinfra.lab.remoting.rpc.server.RpcServerConfig;
import io.github.xinfra.lab.rpc.config.ProtocolConfig;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.transport.ServerTransport;
import java.io.IOException;

public class XRemotingServerTransport implements ServerTransport {
  private XRemotingProtocolConfig protocolConfig;
  private RpcServer rpcServer;

  public XRemotingServerTransport(ProtocolConfig protocolConfig) {
    if (!(protocolConfig instanceof XRemotingProtocolConfig)) {
      throw new IllegalArgumentException("protocolConfig must be XRemotingProtocolConfig");
    }
    this.protocolConfig = (XRemotingProtocolConfig) protocolConfig;

    RpcServerConfig rpcServerConfig = new RpcServerConfig();
    rpcServerConfig.setPort(protocolConfig.port());
    this.rpcServer = new RpcServer(rpcServerConfig);
    rpcServer.registerUserProcessor(new RpcProcessor());
    this.rpcServer.startup();
  }

  @Override
  public void register(ServiceConfig<?> serviceConfig, Invoker invoker) {
    // todo
  }

  @Override
  public void unRegister(ServiceConfig<?> serviceConfig, Invoker invoker) {
    // todo
  }

  @Override
  public void close() throws IOException {
    rpcServer.shutdown();
  }

  static class RpcProcessor implements UserProcessor<RpcRequest> {
    @Override
    public String interest() {
      return RpcRequest.class.getName();
    }

    @Override
    public Object handRequest(RpcRequest request) {
      // todo
      return null;
    }
  }
}

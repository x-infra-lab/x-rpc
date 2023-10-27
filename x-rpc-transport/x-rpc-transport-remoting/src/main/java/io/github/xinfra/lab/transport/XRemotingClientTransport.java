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

import io.github.xinfra.lab.remoting.connection.Connection;
import io.github.xinfra.lab.remoting.connection.ConnectionEvent;
import io.github.xinfra.lab.remoting.connection.ConnectionEventListener;
import io.github.xinfra.lab.remoting.connection.ConnectionManager;
import io.github.xinfra.lab.remoting.rpc.client.RpcClient;
import io.github.xinfra.lab.remoting.rpc.client.RpcInvokeCallBack;
import io.github.xinfra.lab.rpc.config.TransportClientConfig;
import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.invoker.RpcResponse;
import io.github.xinfra.lab.rpc.transport.ClientTransport;
import io.github.xinfra.lab.rpc.transport.TransportEvent;
import io.github.xinfra.lab.rpc.transport.TransportEventListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class XRemotingClientTransport implements ClientTransport {
  private XRemotingTransportClientConfig transportClientConfig;
  private RpcClient rpcClient;
  private ConnectionManager connectionManager;

  public XRemotingClientTransport(TransportClientConfig transportClientConfig) {
    if (!(transportClientConfig instanceof XRemotingTransportClientConfig)) {
      throw new IllegalArgumentException(
          "transportClientConfig must be XRemotingTransportClientConfig");
    }
    this.rpcClient = new RpcClient();
    this.rpcClient.startup();
    this.connectionManager = rpcClient.getConnectionManager();
  }

  @Override
  public void connect(InetSocketAddress socketAddress) throws Exception {
    this.connectionManager.connect(socketAddress);
  }

  @Override
  public void disconnect(InetSocketAddress socketAddress) {
    connectionManager.disconnect(socketAddress);
  }

  @Override
  public void reconnect(InetSocketAddress socketAddress) {
    connectionManager.reconnector().reconnect(socketAddress);
  }

  @Override
  public void addTransportEventListener(TransportEventListener listener) {
    connectionManager
        .connectionEventProcessor()
        .addConnectionEventListener(
            new ConnectionEventListener() {
              @Override
              public void onEvent(ConnectionEvent connectionEvent, Connection connection) {
                if (connectionEvent == ConnectionEvent.CONNECT) {
                  listener.onEvent(
                      TransportEvent.CONNECT, (InetSocketAddress) connection.remoteAddress());
                }
                if (connectionEvent == ConnectionEvent.CLOSE) {
                  listener.onEvent(
                      TransportEvent.DISCONNECT, (InetSocketAddress) connection.remoteAddress());
                }
              }
            });
  }

  @Override
  public CompletableFuture<RpcResponse> sendAsync(
      InetSocketAddress socketAddress,
      RpcRequest request,
      int timeoutMills,
      ExecutorService invokeCallBackExecutor)
      throws Exception {
    CompletableFuture completableFuture = new CompletableFuture();
    RpcInvokeCallBack<RpcResponse> rpcResponseRpcInvokeCallBack =
        new RpcInvokeCallBack<RpcResponse>() {

          @Override
          public void onException(Throwable t) {
            completableFuture.completeExceptionally(t);
          }

          @Override
          public void onResponse(RpcResponse response) {
            completableFuture.complete(response);
          }

          @Override
          public Executor executor() {
            return invokeCallBackExecutor;
          }
        };

    rpcClient.asyncCall(request, socketAddress, timeoutMills, rpcResponseRpcInvokeCallBack);
    return completableFuture;
  }

  @Override
  public void close() throws IOException {
    rpcClient.shutdown();
  }
}

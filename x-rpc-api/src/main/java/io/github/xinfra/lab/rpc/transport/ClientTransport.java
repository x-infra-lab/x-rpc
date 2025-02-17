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
package io.github.xinfra.lab.rpc.transport;

import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.invoker.RpcResponse;
import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface ClientTransport extends Closeable {

  void connect(InetSocketAddress socketAddress) throws Exception;

  void disconnect(InetSocketAddress socketAddress);

  void reconnect(InetSocketAddress socketAddress);

  void addTransportEventListener(TransportEventListener listener);

  CompletableFuture<RpcResponse> sendAsync(
      InetSocketAddress socketAddress,
      RpcRequest request,
      int timeoutMills,
      ExecutorService executor)
      throws Exception;
}

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
package io.github.xinfra.lab.rpc.registry;

import io.github.xinfra.lab.rpc.metadata.MetadataInfo;
import java.net.InetSocketAddress;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class ServiceInstance {

  @EqualsAndHashCode.Include private String appName;
  private Boolean enabled = true;
  @EqualsAndHashCode.Include private String address;
  @EqualsAndHashCode.Include private Integer port;
  private long registrationTimestamp;

  private InetSocketAddress socketAddress;
  private MetadataInfo metadataInfo;
  @EqualsAndHashCode.Include private String revision;
  // todo

  public ServiceInstance(String appName, String address, Integer port) {
    // todo validate
    this.appName = appName;
    this.address = address;
    this.port = port;
    this.socketAddress = new InetSocketAddress(address, port);
  }
}

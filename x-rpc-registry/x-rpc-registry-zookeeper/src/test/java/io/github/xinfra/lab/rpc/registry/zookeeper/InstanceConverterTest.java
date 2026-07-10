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
package io.github.xinfra.lab.rpc.registry.zookeeper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class InstanceConverterTest {

  @Test
  void convertFromZookeeperInstance() throws Exception {
    Map<String, String> props = new HashMap<>();
    props.put("group", "default");

    ZookeeperInstancePayload payload = new ZookeeperInstancePayload();
    payload.setRevision("rev-001");
    payload.setProtocol("x-rpc");
    payload.setProps(props);

    org.apache.curator.x.discovery.ServiceInstance<ZookeeperInstancePayload> zkInstance =
        org.apache.curator.x.discovery.ServiceInstance.<ZookeeperInstancePayload>builder()
            .name("my-app")
            .id("id-1")
            .address("192.168.1.10")
            .port(8080)
            .payload(payload)
            .registrationTimeUTC(1000L)
            .enabled(true)
            .build();

    ServiceInstance result = InstanceConverter.convert(zkInstance);

    assertEquals("my-app", result.getAppName());
    assertEquals("192.168.1.10", result.getAddress());
    assertEquals(8080, result.getPort());
    assertTrue(result.getEnabled());
    assertEquals(1000L, result.getRegistrationTimestamp());
    assertEquals("rev-001", result.getRevision());
    assertEquals("x-rpc", result.getProtocol());
    assertEquals("default", result.getProps().get("group"));
  }

  @Test
  void convertFromZookeeperInstanceNullProps() throws Exception {
    ZookeeperInstancePayload payload = new ZookeeperInstancePayload();
    payload.setRevision("rev-002");

    org.apache.curator.x.discovery.ServiceInstance<ZookeeperInstancePayload> zkInstance =
        org.apache.curator.x.discovery.ServiceInstance.<ZookeeperInstancePayload>builder()
            .name("app2")
            .id("id-2")
            .address("10.0.0.1")
            .port(9090)
            .payload(payload)
            .enabled(false)
            .build();

    ServiceInstance result = InstanceConverter.convert(zkInstance);

    assertEquals("app2", result.getAppName());
    assertEquals("10.0.0.1", result.getAddress());
    assertEquals(9090, result.getPort());
    assertEquals(false, result.getEnabled());
    assertEquals("rev-002", result.getRevision());
    assertNull(result.getProtocol());
    assertTrue(result.getProps().isEmpty());
  }

  @Test
  void convertToZookeeperInstance() throws Exception {
    ServiceInstance serviceInstance = new ServiceInstance("my-app", "127.0.0.1", 8080);
    serviceInstance.setEnabled(true);
    serviceInstance.setRevision("rev-100");
    serviceInstance.setProtocol("x-rpc");
    serviceInstance.getProps().put("weight", "10");

    org.apache.curator.x.discovery.ServiceInstance<ZookeeperInstancePayload> result =
        InstanceConverter.convert(serviceInstance);

    assertEquals("my-app", result.getName());
    assertEquals("127.0.0.1", result.getAddress());
    assertEquals(8080, result.getPort());
    assertTrue(result.isEnabled());
    assertEquals("rev-100", result.getPayload().getRevision());
    assertEquals("x-rpc", result.getPayload().getProtocol());
    assertEquals("10", result.getPayload().getProps().get("weight"));
  }

  @Test
  void roundTripConversion() throws Exception {
    ServiceInstance original = new ServiceInstance("echo-service", "10.0.0.5", 12345);
    original.setEnabled(false);
    original.setRevision("abc123");
    original.setProtocol("grpc");
    original.getProps().put("zone", "us-east-1");

    org.apache.curator.x.discovery.ServiceInstance<ZookeeperInstancePayload> zkInstance =
        InstanceConverter.convert(original);
    ServiceInstance roundTripped = InstanceConverter.convert(zkInstance);

    assertEquals(original.getAppName(), roundTripped.getAppName());
    assertEquals(original.getAddress(), roundTripped.getAddress());
    assertEquals(original.getPort(), roundTripped.getPort());
    assertEquals(original.getEnabled(), roundTripped.getEnabled());
    assertEquals(original.getRevision(), roundTripped.getRevision());
    assertEquals(original.getProtocol(), roundTripped.getProtocol());
    assertEquals(original.getProps(), roundTripped.getProps());
  }
}

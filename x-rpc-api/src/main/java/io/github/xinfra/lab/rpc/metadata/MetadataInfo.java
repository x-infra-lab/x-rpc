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
package io.github.xinfra.lab.rpc.metadata;

import io.github.xinfra.lab.rpc.config.ServiceConfig;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@NoArgsConstructor
public class MetadataInfo {
  public static final String EMPTY_REVISION = "-";

  private TreeMap<String, ServiceInfo> serviceInfos = new TreeMap<>();

  public void addService(ServiceConfig<?> serviceConfig) {
    if (serviceInfos.containsKey(serviceConfig.getServiceInterfaceName())) {
      throw new IllegalStateException("duplicate register service:" + serviceConfig);
    }
    ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setInterfaceName(serviceConfig.getServiceInterfaceName());
    serviceInfo.setProtocol(new TreeSet<>(serviceConfig.getProtocol()));
    serviceInfo.setGroup(new TreeSet<>(serviceConfig.getGroup()));
    serviceInfo.setVersion(new TreeSet<>(serviceConfig.getVersion()));
    serviceInfos.put(serviceConfig.getServiceInterfaceName(), serviceInfo);
  }

  /** @return revision is changed or not */
  public String calculateRevision() {
    if (serviceInfos.isEmpty()) {
      return EMPTY_REVISION;
    }
    String value = serviceInfos.toString();
    String newRevision = DigestUtils.md5Hex(value);
    return newRevision;
  }
}

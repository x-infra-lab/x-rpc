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
package io.github.xinfra.lab.rpc.core.cluster.router;

import io.github.xinfra.lab.rpc.cluster.router.Router;
import io.github.xinfra.lab.rpc.common.Constants;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceGroupRouter implements Router {

  @Override
  public List<ServiceInstance> route(
      Invocation invocation, List<ServiceInstance> serviceInstanceList) {
    Object groupObj = invocation.getAttachment(Constants.GROUP_KEY);
    if (groupObj == null) {
      return serviceInstanceList;
    }

    String targetGroup = groupObj.toString();
    if (targetGroup.isEmpty()) {
      return serviceInstanceList;
    }

    List<ServiceInstance> filtered =
        serviceInstanceList.stream()
            .filter(
                instance -> targetGroup.equals(instance.getProps().get(Constants.GROUP_KEY)))
            .collect(Collectors.toList());

    if (filtered.isEmpty()) {
      log.warn(
          "No instances found for group: {}, falling back to all instances. service: {}",
          targetGroup,
          invocation.getServiceName());
      return serviceInstanceList;
    }

    return filtered;
  }
}

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
package io.github.xinfra.lab.rpc.console.controller;

import io.github.xinfra.lab.rpc.console.model.InstanceVO;
import io.github.xinfra.lab.rpc.console.service.ConsoleRegistryService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apps/{appName}/instances")
public class InstanceController {

  private final ConsoleRegistryService registryService;

  public InstanceController(ConsoleRegistryService registryService) {
    this.registryService = registryService;
  }

  @GetMapping
  public List<InstanceVO> listInstances(@PathVariable String appName) throws Exception {
    return registryService.listInstances(appName);
  }

  @GetMapping("/{instanceId}")
  public ResponseEntity<InstanceVO> getInstance(
      @PathVariable String appName, @PathVariable String instanceId) throws Exception {
    InstanceVO instance = registryService.getInstance(appName, instanceId);
    if (instance == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(instance);
  }

  @PutMapping("/{instanceId}/disable")
  public ResponseEntity<String> disableInstance(
      @PathVariable String appName, @PathVariable String instanceId) throws Exception {
    registryService.disableInstance(appName, instanceId);
    return ResponseEntity.ok("disabled");
  }

  @PutMapping("/{instanceId}/enable")
  public ResponseEntity<String> enableInstance(
      @PathVariable String appName, @PathVariable String instanceId) throws Exception {
    registryService.enableInstance(appName, instanceId);
    return ResponseEntity.ok("enabled");
  }
}

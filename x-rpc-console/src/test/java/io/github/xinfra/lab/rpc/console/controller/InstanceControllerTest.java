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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.xinfra.lab.rpc.console.model.InstanceVO;
import io.github.xinfra.lab.rpc.console.service.ConsoleRegistryService;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InstanceController.class)
class InstanceControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ConsoleRegistryService registryService;

  @Test
  void listInstancesEmpty() throws Exception {
    when(registryService.listInstances("app1")).thenReturn(Collections.emptyList());

    mockMvc
        .perform(get("/api/apps/app1/instances"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void listInstancesReturnsData() throws Exception {
    InstanceVO inst = new InstanceVO();
    inst.setId("id-1");
    inst.setAppName("app1");
    inst.setAddress("127.0.0.1");
    inst.setPort(8080);
    inst.setEnabled(true);

    when(registryService.listInstances("app1")).thenReturn(Arrays.asList(inst));

    mockMvc
        .perform(get("/api/apps/app1/instances"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].address", is("127.0.0.1")));
  }

  @Test
  void getInstanceFound() throws Exception {
    InstanceVO inst = new InstanceVO();
    inst.setId("id-1");
    inst.setAppName("app1");
    inst.setAddress("127.0.0.1");
    inst.setPort(8080);
    inst.setEnabled(true);

    when(registryService.getInstance("app1", "id-1")).thenReturn(inst);

    mockMvc
        .perform(get("/api/apps/app1/instances/id-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is("id-1")));
  }

  @Test
  void getInstanceNotFound() throws Exception {
    when(registryService.getInstance("app1", "nonexistent")).thenReturn(null);

    mockMvc.perform(get("/api/apps/app1/instances/nonexistent")).andExpect(status().isNotFound());
  }

  @Test
  void disableInstance() throws Exception {
    mockMvc
        .perform(put("/api/apps/app1/instances/id-1/disable"))
        .andExpect(status().isOk());

    verify(registryService).disableInstance("app1", "id-1");
  }

  @Test
  void enableInstance() throws Exception {
    mockMvc
        .perform(put("/api/apps/app1/instances/id-1/enable"))
        .andExpect(status().isOk());

    verify(registryService).enableInstance("app1", "id-1");
  }

  @Test
  void disableInstanceNotFound() throws Exception {
    doThrow(new IllegalArgumentException("Instance not found: app1/bad-id"))
        .when(registryService)
        .disableInstance("app1", "bad-id");

    mockMvc
        .perform(put("/api/apps/app1/instances/bad-id/disable"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Instance not found: app1/bad-id")));
  }
}

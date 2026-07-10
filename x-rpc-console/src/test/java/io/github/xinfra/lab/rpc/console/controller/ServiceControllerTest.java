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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.xinfra.lab.rpc.console.model.InstanceVO;
import io.github.xinfra.lab.rpc.console.model.ServiceVO;
import io.github.xinfra.lab.rpc.console.service.ConsoleRegistryService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ServiceController.class)
class ServiceControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ConsoleRegistryService registryService;

  @Test
  void listServicesEmpty() throws Exception {
    when(registryService.listServices()).thenReturn(Collections.emptyList());

    mockMvc
        .perform(get("/api/services"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void listServicesReturnsData() throws Exception {
    ServiceVO svc = new ServiceVO();
    svc.setInterfaceName("com.example.EchoService");
    svc.setProviderCount(2);
    svc.setProviders(new ArrayList<>());

    when(registryService.listServices()).thenReturn(Arrays.asList(svc));

    mockMvc
        .perform(get("/api/services"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].interfaceName", is("com.example.EchoService")));
  }

  @Test
  void getServiceProviders() throws Exception {
    InstanceVO inst = new InstanceVO();
    inst.setId("id-1");
    inst.setAddress("127.0.0.1");
    inst.setPort(8080);
    inst.setEnabled(true);

    when(registryService.getServiceProviders("my-app")).thenReturn(Arrays.asList(inst));

    mockMvc
        .perform(get("/api/services/my-app/providers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].address", is("127.0.0.1")));
  }
}

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

import io.github.xinfra.lab.rpc.console.model.AppInfo;
import io.github.xinfra.lab.rpc.console.service.ConsoleRegistryService;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AppController.class)
class AppControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ConsoleRegistryService registryService;

  @Test
  void listAppsEmpty() throws Exception {
    when(registryService.listApps()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/apps")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void listAppsReturnsData() throws Exception {
    AppInfo app1 = new AppInfo();
    app1.setAppName("service-a");
    app1.setInstanceCount(3);
    AppInfo app2 = new AppInfo();
    app2.setAppName("service-b");
    app2.setInstanceCount(1);

    when(registryService.listApps()).thenReturn(Arrays.asList(app1, app2));

    mockMvc
        .perform(get("/api/apps"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].appName", is("service-a")))
        .andExpect(jsonPath("$[0].instanceCount", is(3)));
  }
}

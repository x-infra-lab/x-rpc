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
package io.github.xinfra.lab.rpc.spring.boot.consumer;

import io.github.xinfra.lab.rpc.spring.annotation.EnableXRpc;
import io.github.xinfra.lab.rpc.spring.annotation.XRpcReference;
import io.github.xinfra.lab.rpc.spring.boot.api.EchoService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableXRpc(basePackageClasses = ConsumerApplication.class)
public class ConsumerApplication {

  @XRpcReference(appName = "spring-provider-app")
  @Getter
  @Setter
  public EchoService echoService;

  public static ConfigurableApplicationContext applicationContext;

  public static void main(String[] args) {
    applicationContext = SpringApplication.run(ConsumerApplication.class, args);
  }
}

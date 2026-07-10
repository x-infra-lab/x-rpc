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
package io.github.xinfra.lab.rpc.example.consumer;

import io.github.xinfra.lab.rpc.example.api.EchoService;
import io.github.xinfra.lab.rpc.spring.annotation.XRpcReference;
import io.github.xinfra.lab.rpc.spring.boot.autoconfigure.EnableXRpc;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableXRpc(basePackageClasses = ConsumerApplication.class)
public class ConsumerApplication implements CommandLineRunner {

  @XRpcReference(appName = "example-provider")
  private EchoService echoService;

  public static void main(String[] args) {
    SpringApplication.run(ConsumerApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    String result = echoService.hello("x-rpc");
    System.out.println("=== RPC call result: " + result + " ===");
  }
}

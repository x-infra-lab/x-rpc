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
package io.github.xinfra.lab.rpc.spring.boot;

import io.github.xinfra.lab.rpc.spring.boot.consumer.ConsumerApplication;
import io.github.xinfra.lab.rpc.spring.boot.provider.ProviderApplication;
import org.apache.curator.test.TestingServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XRpcTestApplicationTest {

  @Test
  public void baseTest1() throws Exception {
    TestingServer testingServer = new TestingServer(2666);

    ProviderApplication.main(new String[] {"--spring.config.name=provider-application"});
    ConsumerApplication.main(new String[] {"--spring.config.name=consumer-application"});

    ConsumerApplication consumerApplication =
        ConsumerApplication.applicationContext.getBean(ConsumerApplication.class);
    String result = consumerApplication.echoService.hello("spring-boot-starter");
    Assertions.assertNotNull(result);
  }
}

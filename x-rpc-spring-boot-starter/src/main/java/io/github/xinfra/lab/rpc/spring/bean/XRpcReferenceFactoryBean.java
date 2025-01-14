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
package io.github.xinfra.lab.rpc.spring.bean;

import io.github.xinfra.lab.rpc.bootstrap.ConsumerBootstrap;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class XRpcReferenceFactoryBean<T> implements FactoryBean<T>, DisposableBean {

  private Class<?> objectType;

  private ConsumerBootstrap consumerBootstrap;

  private ReferenceConfig<T> referenceConfig;

  private T object;

  public XRpcReferenceFactoryBean(Class<?> objectType) {
    this.objectType = objectType;
  }

  @Override
  public T getObject() throws Exception {
    if (object == null) {
      object = consumerBootstrap.refer(referenceConfig);
    }
    return object;
  }

  @Override
  public Class<?> getObjectType() {
    return objectType;
  }

  @Override
  public void destroy() throws Exception {
    consumerBootstrap.unRefer(referenceConfig);
  }
}

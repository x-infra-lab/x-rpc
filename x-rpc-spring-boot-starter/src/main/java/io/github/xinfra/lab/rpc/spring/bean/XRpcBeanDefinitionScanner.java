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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.Getter;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

public class XRpcBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

  @Getter
  private final ConcurrentMap<String, Set<BeanDefinitionHolder>> beanDefinitionHolderMap =
      new ConcurrentHashMap<>();

  public XRpcBeanDefinitionScanner(
      BeanDefinitionRegistry registry,
      boolean useDefaultFilters,
      Environment environment,
      ResourceLoader resourceLoader) {
    super(registry, useDefaultFilters, environment, resourceLoader);
  }

  @Override
  protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();

    for (String basePackage : basePackages) {
      if (!beanDefinitionHolderMap.containsKey(basePackage)) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        beanDefinitionHolderMap.put(basePackage, beanDefinitionHolders);
      }
      beanDefinitions.addAll(beanDefinitionHolderMap.get(basePackage));
    }

    return beanDefinitions;
  }
}

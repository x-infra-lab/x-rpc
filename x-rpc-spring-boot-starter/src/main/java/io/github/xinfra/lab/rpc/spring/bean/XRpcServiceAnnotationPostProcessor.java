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

import io.github.xinfra.lab.rpc.spring.annotation.XRpcService;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.CollectionUtils;

@Slf4j
public class XRpcServiceAnnotationPostProcessor implements BeanDefinitionRegistryPostProcessor {

  private final Set<String> packagesToScan;

  public XRpcServiceAnnotationPostProcessor(Set<String> packagesToScan) {
    this.packagesToScan = packagesToScan;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    // do nothing
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
      throws BeansException {
    scanXRpcServiceBeans(registry);
  }

  private void scanXRpcServiceBeans(BeanDefinitionRegistry registry) {
    if (packagesToScan == null || packagesToScan.isEmpty()) {
      log.warn("No packages to scan for X RPC services");
      return;
    }
    Set<BeanDefinition> candidateBeanDefinitions = new HashSet<>();
    ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false);
    scanner.addIncludeFilter(new AnnotationTypeFilter(XRpcService.class));
    for (String packageToScan : packagesToScan) {
      Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(packageToScan);
      log.info("Found {} X RPC services in package: {}", candidateComponents.size(), packageToScan);
      candidateBeanDefinitions.addAll(candidateComponents);
    }

    if (CollectionUtils.isEmpty(candidateBeanDefinitions)) {
      log.warn("No X RPC services found in packages: {}", String.join(", ", packagesToScan));
      return;
    }

    for (BeanDefinition beanDefinition : candidateBeanDefinitions) {
      registerXRpcServiceBean(registry, beanDefinition);
    }
  }

  private void registerXRpcServiceBean(
      BeanDefinitionRegistry registry, BeanDefinition beanDefinition) {
    // todo
  }
}

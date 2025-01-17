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

import io.github.xinfra.lab.rpc.config.ExporterConfig;
import io.github.xinfra.lab.rpc.spring.annotation.XRpcService;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

@Slf4j
public class XRpcServiceAnnotationPostProcessor
    implements BeanDefinitionRegistryPostProcessor,
        EnvironmentAware,
        ResourceLoaderAware,
        BeanClassLoaderAware {

  private final Set<String> packagesToScan;
  private Environment environment;
  private ResourceLoader resourceLoader;
  private ClassLoader classLoader;

  public XRpcServiceAnnotationPostProcessor(Set<String> packagesToScan) {
    this.packagesToScan = packagesToScan;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
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
      log.warn("XRpc no packages to scan for XRpc services");
      return;
    }
    XRpcBeanDefinitionScanner scanner =
        new XRpcBeanDefinitionScanner(registry, false, environment, resourceLoader);
    scanner.addIncludeFilter(new AnnotationTypeFilter(XRpcService.class));

    for (String packageToScan : packagesToScan) {
      // register XRpcService beanDefinition
      int num = scanner.scan(packageToScan);
      log.info("XRpc found {} services in package: {}", num, packageToScan);
    }

    if (CollectionUtils.isEmpty(scanner.getBeanDefinitionHolderMap())) {
      log.warn("XRpc no services found in packages: {}", String.join(", ", packagesToScan));
      return;
    }

    Set<BeanDefinitionHolder> beanDefinitionHolders =
        scanner.getBeanDefinitionHolderMap().values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
      registerExporterConfigBeans(registry, beanDefinitionHolder);
    }
  }

  private void registerExporterConfigBeans(
      BeanDefinitionRegistry registry, BeanDefinitionHolder rpcServiceBeanDefinitionHolder) {
    // get service interface
    Class<?> beanClass =
        ClassUtils.resolveClassName(
            rpcServiceBeanDefinitionHolder.getBeanDefinition().getBeanClassName(), classLoader);
    Class<?>[] allInterfaces = ClassUtils.getAllInterfacesForClass(beanClass);
    if (allInterfaces.length == 0) {
      log.warn("No interfaces found for class: {}", beanClass);
      throw new IllegalArgumentException("No interfaces found for class: " + beanClass);
    }
    Class<?> serviceInterface = allInterfaces[0];

    BeanDefinitionBuilder xRpcServiceBeanBuilder =
        BeanDefinitionBuilder.rootBeanDefinition(XRpcServiceBean.class);
    xRpcServiceBeanBuilder.addPropertyReference("providerBoostrap", "providerBoostrap");

    // todo resolve @XRpcService attrs
    BeanDefinitionBuilder exporterConfigBuilder =
        BeanDefinitionBuilder.rootBeanDefinition(ExporterConfig.class);
    exporterConfigBuilder.addConstructorArgValue(serviceInterface);
    exporterConfigBuilder.addPropertyReference(
        "serviceImpl", rpcServiceBeanDefinitionHolder.getBeanName());
    String exporterConfigBeanName =
        BeanDefinitionReaderUtils.registerWithGeneratedName(
            exporterConfigBuilder.getBeanDefinition(), registry);

    xRpcServiceBeanBuilder.addPropertyReference("exporterConfig", exporterConfigBeanName);

    BeanDefinitionReaderUtils.registerWithGeneratedName(
        xRpcServiceBeanBuilder.getBeanDefinition(), registry);
  }
}

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

import io.github.xinfra.lab.rpc.spring.annotation.EnableXRpc;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

public class XRpcBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
  @Override
  public void registerBeanDefinitions(
      AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    Set<String> packageToScanSet = packageToScan(importingClassMetadata);

    registerXRpcServiceAnnotationPostProcessor(registry, packageToScanSet);
    registerXRpcReferenceAnnotationPostProcessor(registry);
  }

  private void registerXRpcReferenceAnnotationPostProcessor(BeanDefinitionRegistry registry) {
    BeanDefinitionBuilder builder =
        BeanDefinitionBuilder.rootBeanDefinition(XRpcReferenceAnnotationPostProcessor.class);
    AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
    BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
  }

  private void registerXRpcServiceAnnotationPostProcessor(
      BeanDefinitionRegistry registry, Set<String> packageToScanSet) {
    BeanDefinitionBuilder builder =
        BeanDefinitionBuilder.rootBeanDefinition(XRpcServiceAnnotationPostProcessor.class);
    builder.addConstructorArgValue(packageToScanSet);
    AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
    BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
  }

  private Set<String> packageToScan(AnnotationMetadata importingClassMetadata) {
    Set<String> packageToScanSet = new HashSet<>();
    AnnotationAttributes attributes =
        AnnotationAttributes.fromMap(
            importingClassMetadata.getAnnotationAttributes(EnableXRpc.class.getName()));
    if (attributes == null) {
      return packageToScanSet;
    }

    String[] basePackages = attributes.getStringArray("basePackages");
    Arrays.stream(basePackages).forEach(packageToScanSet::add);

    Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
    Arrays.stream(basePackageClasses)
        .map(ClassUtils::getPackageName)
        .forEach(packageToScanSet::add);

    if (!packageToScanSet.isEmpty()) {
      return packageToScanSet;
    }

    // default to scan the package of the annotated class
    packageToScanSet.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
    return packageToScanSet;
  }
}

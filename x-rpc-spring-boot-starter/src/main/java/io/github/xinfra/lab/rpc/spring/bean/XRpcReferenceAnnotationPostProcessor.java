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

import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.spring.annotation.XRpcReference;
import java.lang.reflect.Field;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.ReflectionUtils;

public class XRpcReferenceAnnotationPostProcessor implements InstantiationAwareBeanPostProcessor {

  @Override
  public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
      throws BeansException {
    MutablePropertyValues mpvs = new MutablePropertyValues(pvs);

    Class<?> beanClass = bean.getClass();
    ReflectionUtils.doWithFields(
        beanClass,
        field -> {
          String fieldName = field.getName();
          mpvs.add(fieldName, buildRpcReference(field));
        },
        field -> field.isAnnotationPresent(XRpcReference.class));

    return mpvs;
  }

  private BeanDefinition buildRpcReference(Field field) {

    BeanDefinitionBuilder builder =
        BeanDefinitionBuilder.rootBeanDefinition(XRpcReferenceFactoryBean.class);

    Class<?> referenceClass = field.getType();
    builder.addConstructorArgValue(referenceClass);
    builder.addPropertyReference("consumerBootstrap", "consumerBootstrap");

    ReferenceConfig<?> referenceConfig = new ReferenceConfig<>(referenceClass);

    XRpcReference xRpcReference = field.getAnnotation(XRpcReference.class);
    referenceConfig.setAppName(xRpcReference.appName());

    // todo resolve @XRpcReference attrs
    builder.addPropertyValue("referenceConfig", referenceConfig);

    return builder.getBeanDefinition();
  }
}

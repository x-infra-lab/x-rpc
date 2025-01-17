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

import static org.springframework.beans.factory.BeanFactory.FACTORY_BEAN_PREFIX;

import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.spring.annotation.XRpcReference;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ReflectionUtils;

public class XRpcReferenceAnnotationPostProcessor
    implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
  private static final Logger log =
      LoggerFactory.getLogger(XRpcReferenceAnnotationPostProcessor.class);
  private DefaultListableBeanFactory beanFactory;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    if (!(beanFactory instanceof DefaultListableBeanFactory)) {
      throw new IllegalArgumentException(
          "beanFactory must be DefaultListableBeanFactory, but is " + beanFactory.getClass());
    }
    this.beanFactory = (DefaultListableBeanFactory) beanFactory;
  }

  @Override
  public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
      throws BeansException {
    Class<?> beanClass = bean.getClass();
    ReflectionUtils.doWithFields(
        beanClass,
        field -> {
          field.setAccessible(true);
          try {
            field.set(bean, buildRpcReferenceObject(field));
            log.info("XRpc resolve @XRpcReference filed:{}", field);
          } catch (Exception e) {
            throw new BeanCreationException(
                "XRpc resolve @XRpcReference filed fail. filed:" + field, e);
          }
        },
        field -> field.isAnnotationPresent(XRpcReference.class));
    return pvs;
  }

  private Object buildRpcReferenceObject(Field field) throws Exception {
    String beanName = field.getType().getSimpleName() + "XRpcReferenceFactoryBean";
    if (!beanFactory.containsBean(FACTORY_BEAN_PREFIX + beanName)) {
      registerReferenceFactoryBean(field, beanName);
    }
    return beanFactory
        .getBean(FACTORY_BEAN_PREFIX + beanName, XRpcReferenceFactoryBean.class)
        .getObject();
  }

  private void registerReferenceFactoryBean(Field field, String beanName) {
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

    BeanDefinition beanDefinition = builder.getBeanDefinition();
    beanFactory.registerBeanDefinition(beanName, beanDefinition);
  }
}

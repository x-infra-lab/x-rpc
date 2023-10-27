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
package io.github.xinfra.lab.rpc.common;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectCache {
  /** key: service class name value: classloader */
  public Map<String, ClassLoader> classLoaderMap = new ConcurrentHashMap<>();

  /** key: methodName value: key: methodSign - value: method */
  private Map<String, Map<String, Method>> overrideMethodMap = new ConcurrentHashMap<>();

  public void loadClass(Class<?> serviceInterfaceClass) {
    classLoaderMap.put(serviceInterfaceClass.getName(), serviceInterfaceClass.getClassLoader());

    for (Method method : serviceInterfaceClass.getMethods()) {
      loadMethod(serviceInterfaceClass, method);
    }
  }

  private void loadMethod(Class<?> clazz, Method method) {
    Map<String, Method> methodSignMap =
        overrideMethodMap.computeIfAbsent(clazz.getName(), k -> new ConcurrentHashMap<>());
    methodSignMap.put(ClassUtils.genMethodSign(method), method);
  }

  public Method find(String serviceName, String methodName, String[] methodArgTypes) {
    Map<String, Method> methodMap = overrideMethodMap.get(serviceName);
    if (methodMap == null) {
      return null;
    }
    return methodMap.get(ClassUtils.genMethodSign(methodName, methodArgTypes));
  }
}

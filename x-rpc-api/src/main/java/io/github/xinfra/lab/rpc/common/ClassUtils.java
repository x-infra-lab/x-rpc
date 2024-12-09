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
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClassUtils {

  public static Object getDefaultPrimitiveValue(Class<?> clazz) {
    if (clazz == int.class) {
      return 0;
    } else if (clazz == boolean.class) {
      return false;
    } else if (clazz == long.class) {
      return 0L;
    } else if (clazz == byte.class) {
      return (byte) 0;
    } else if (clazz == double.class) {
      return 0d;
    } else if (clazz == short.class) {
      return (short) 0;
    } else if (clazz == float.class) {
      return 0f;
    } else if (clazz == char.class) {
      return (char) 0;
    } else {
      return null;
    }
  }

  public static String genMethodSign(Method method){
      return method.getName() +
              "(" +
              Arrays.stream(method.getParameterTypes()).map(Class::getName)
                      .collect(Collectors.joining(";")) +
              ")";
  }

  public static String genMethodSign(String methodName, String[] methodArgTypes) {
    return methodName + "(" + String.join(";", methodArgTypes) + ")";
  }
}

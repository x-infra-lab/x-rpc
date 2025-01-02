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
package io.github.xinfra.lab.rpc.extention.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Jsons {

  private static boolean useJackson = false;
  private static boolean useGson = false;
  private static Gson gson;
  private static ObjectMapper mapper;

  static {
    if (!loadJackson()) {
      loadGson();
    }
  }

  private static boolean loadJackson() {
    // load jackson
    try {
      Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
      useJackson = true;
      mapper = new ObjectMapper();
      return true;
    } catch (ClassNotFoundException e) {
      // ignore
      log.warn("jackson lib not found");
      return false;
    }
  }

  private static boolean loadGson() {
    // load gson
    try {
      Class.forName("com.google.gson.Gson");
      useGson = true;
      gson = new Gson();
      return true;
    } catch (ClassNotFoundException e) {
      // ignore
      log.warn("gson lib not found");
      return false;
    }
  }

  public <T> T fromJson(String json, Type typeOfT) throws Exception {
    if (!useGson && !useJackson) {
      throw new IllegalStateException("no available json lib(gson or jackson).");
    }

    if (useJackson) {
      return fromJsonByJackson(json, typeOfT);
    }

    if (useGson) {
      return fromJsonByGson(json, typeOfT);
    }

    // should not reach here
    throw new IllegalAccessError("unsupported");
  }

  private <T> T fromJsonByJackson(String json, Type typeOfT) throws JsonProcessingException {
    return mapper.readValue(
        json,
        new TypeReference<T>() {
          @Override
          public Type getType() {
            return typeOfT;
          }
        });
  }

  private <T> T fromJsonByGson(String json, Type typeOfT) {
    Type type = TypeToken.get(typeOfT).getType();
    return gson.fromJson(json, type);
  }
}

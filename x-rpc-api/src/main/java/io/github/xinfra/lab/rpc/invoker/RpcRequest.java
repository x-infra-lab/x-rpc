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
package io.github.xinfra.lab.rpc.invoker;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RpcRequest implements Serializable {
  /** eg: com.github.xinfra.lab.rpc.exanple.EchoService */
  private String serviceName;

  /** eg:echo */
  private String methodName;

  /** eg: java.lang.String */
  private String[] methodArgTypes;

  private Object[] methodArgs;

  /** Extensional properties of request */
  private Map<String, Object> attachment;

  public void addAttachment(Map<String, Object> attachment) {
    if (this.attachment == null) {
      this.attachment = attachment;
    } else {
      this.attachment.putAll(attachment);
    }
  }
}

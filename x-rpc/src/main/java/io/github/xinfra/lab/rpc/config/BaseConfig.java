package io.github.xinfra.lab.rpc.config;

import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolType;
import io.github.xinfra.lab.rpc.remoting.serialization.SerializationType;
import lombok.Getter;

@Getter
public abstract class BaseConfig<T> {
    protected ProtocolType protocolType;
    protected SerializationType serializationType;
    protected Class<T> interfaceId;
    protected RegistryConfig<?> registryConfig;
}

package io.github.xinfra.lab.rpc.registry;

import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.config.ProviderConfig;

import java.util.List;

public interface Registry {

    void register(ProviderConfig providerConfig);

    void unRegister(ProviderConfig providerConfig);

    List<ProviderGroup> subscribe(ConsumerConfig<?> config);

    void unSubscribe(ConsumerConfig<?> config);

}

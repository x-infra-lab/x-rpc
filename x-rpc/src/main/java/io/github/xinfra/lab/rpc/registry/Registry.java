package io.github.xinfra.lab.rpc.registry;

import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.config.ProviderConfig;

public interface Registry {

    void register(ProviderConfig providerConfig);

    void subscribe(ConsumerConfig<?> config);
}

package io.github.xinfra.lab.rpc.registry;

import io.github.xinfra.lab.rpc.common.LifeCycle;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.config.ProviderConfig;

import java.util.List;

public interface Registry  extends LifeCycle {

    void register(ProviderConfig providerConfig);

    void unRegister(ProviderConfig providerConfig);

    List<ProviderGroup> subscribe(ConsumerConfig<?> config);

    void unSubscribe(ConsumerConfig<?> config);


    void addListener(ProviderListener listener);
}

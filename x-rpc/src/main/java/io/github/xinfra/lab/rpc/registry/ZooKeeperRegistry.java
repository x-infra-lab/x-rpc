package io.github.xinfra.lab.rpc.registry;

import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.config.ProviderConfig;

import java.util.List;

public class ZooKeeperRegistry implements Registry {
    @Override
    public void register(ProviderConfig providerConfig) {
        // TODO
    }

    @Override
    public void unRegister(ProviderConfig providerConfig) {
        // TODO
    }

    @Override
    public List<ProviderGroup> subscribe(ConsumerConfig<?> config) {
        // TODO
        return null;
    }

    @Override
    public void unSubscribe(ConsumerConfig<?> config) {
        // TODO
    }

    @Override
    public void addListener(ProviderListener listener) {
        // todo
    }

    @Override
    public void startup() {

    }

    @Override
    public void shutDown() {

    }
}

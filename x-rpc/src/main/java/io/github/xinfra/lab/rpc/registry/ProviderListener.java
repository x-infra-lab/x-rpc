package io.github.xinfra.lab.rpc.registry;


public interface ProviderListener {

    void addProvider(ProviderGroup providerGroup);

    void removeProvider(ProviderGroup providerGroup);

    void updateProvider(ProviderGroup providerGroup);
}

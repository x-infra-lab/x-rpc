package io.github.xinfra.lab.rpc.registry;

import java.util.List;

public interface ProviderInfoListener {

    void addProviders(List<ProviderInfo> providerInfoList);

    void removeProviders(List<ProviderInfo> providerInfoList);
}

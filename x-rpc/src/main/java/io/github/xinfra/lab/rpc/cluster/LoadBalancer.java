package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.registry.ProviderInfo;

import java.util.List;

public interface LoadBalancer {

    ProviderInfo select(List<ProviderInfo> providerInfoList);

}

package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;

import java.util.List;

public interface Router {
    List<ProviderInfo> route(Invocation invocation, List<ProviderInfo> providerInfoList);
}

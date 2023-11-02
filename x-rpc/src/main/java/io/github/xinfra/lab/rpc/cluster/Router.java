package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;

import java.util.List;

public interface Router {
    List<ProviderInfo> route(RpcRequest request, List<ProviderInfo> providerInfoList);
}

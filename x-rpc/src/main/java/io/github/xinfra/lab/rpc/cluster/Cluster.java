package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.RpcRequest;
import io.github.xinfra.lab.rpc.common.LifeCycle;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;
import io.github.xinfra.lab.rpc.registry.ProviderInfoListener;

public interface Cluster extends ProviderInfoListener, LifeCycle {

    ProviderInfo select(RpcRequest request);

}

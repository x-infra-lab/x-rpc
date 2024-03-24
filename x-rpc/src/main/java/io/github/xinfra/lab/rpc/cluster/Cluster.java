package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.common.LifeCycle;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.registry.ProviderListener;
import io.github.xinfra.lab.rpc.transport.TransportManager;

public interface Cluster extends ProviderListener, LifeCycle {
    ClusterInvoker invoker();

    TransportManager transportManager();

    Invoker select(Invocation request);
}

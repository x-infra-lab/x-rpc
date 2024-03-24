package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.invoker.Invoker;

public interface ClusterInvoker extends Invoker {
    Cluster cluster();
}

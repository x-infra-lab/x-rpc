package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.cluster.ClusterInvoker;
import io.github.xinfra.lab.rpc.cluster.Directory;


public class FailFastClusterInvoker implements ClusterInvoker {
    private Directory directory;

    public FailFastClusterInvoker(Directory directory) {
        this.directory = directory;
    }

    @Override
    public InvocationResult invoke(Invocation invocation) {
        // todo
        return null;
    }

    @Override
    public Directory directory() {
        return this.directory;
    }
}

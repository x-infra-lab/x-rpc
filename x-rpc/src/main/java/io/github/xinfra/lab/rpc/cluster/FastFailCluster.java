package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.invoker.FailFastClusterInvoker;


public class FastFailCluster implements Cluster {


    public FastFailCluster() {
    }

    @Override
    public ClusterInvoker filteringInvoker(Directory directory) {
        return new FailFastClusterInvoker(directory);
    }

}

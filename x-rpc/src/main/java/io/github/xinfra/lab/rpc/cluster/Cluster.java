package io.github.xinfra.lab.rpc.cluster;

public interface Cluster {

    ClusterInvoker filteringInvoker(Directory directory);

}

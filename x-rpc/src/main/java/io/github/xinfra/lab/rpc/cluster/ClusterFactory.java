package io.github.xinfra.lab.rpc.cluster;



public class ClusterFactory {

    public static Cluster create(ClusterType clusterType) {
        // todo
        return new FastFailCluster();
    }
}

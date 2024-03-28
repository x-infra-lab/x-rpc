package io.github.xinfra.lab.rpc.cluster;

import java.util.HashMap;
import java.util.Map;

public class ClusterManager {
    private static Map<ClusterType, Cluster> clusterMap = new HashMap<>();

    public synchronized static Cluster getCluster(ClusterType clusterType) {
        Cluster cluster = clusterMap.get(clusterType);
        if (cluster == null) {
            cluster = ClusterFactory.create(clusterType);
            clusterMap.put(clusterType, cluster);
        }
        return cluster;
    }
}

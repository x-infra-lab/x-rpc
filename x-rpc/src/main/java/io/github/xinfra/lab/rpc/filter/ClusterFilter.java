package io.github.xinfra.lab.rpc.filter;


import io.github.xinfra.lab.rpc.cluster.ClusterInvoker;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;

public interface ClusterFilter {

    InvocationResult filter(ClusterInvoker clusterInvoker, Invocation invocation);

    default void onResult(InvocationResult invocationResult) {
    }


    default void onError(Throwable throwable) {
    }

}

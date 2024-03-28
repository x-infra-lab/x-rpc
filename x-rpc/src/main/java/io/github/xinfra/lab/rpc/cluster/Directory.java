package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.Invoker;

import java.util.List;

public interface Directory {

    List<Invoker> list(Invocation invocation);
}

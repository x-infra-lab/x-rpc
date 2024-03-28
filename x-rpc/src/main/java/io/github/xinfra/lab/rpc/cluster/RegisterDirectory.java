package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.registry.Registry;

import java.util.List;

public class RegisterDirectory implements Directory{
    private Registry registry;
    private ReferenceConfig<?> referenceConfig;

    public RegisterDirectory(Registry registry, ReferenceConfig<?> referenceConfig) {
        this.registry = registry;
        this.referenceConfig = referenceConfig;
    }

    @Override
    public List<Invoker> list(Invocation invocation) {
        // todo
        return null;
    }
}

package io.github.xinfra.lab.rpc.filter;

import io.github.xinfra.lab.rpc.RpcRequest;
import io.github.xinfra.lab.rpc.RpcResponse;
import io.github.xinfra.lab.rpc.invoker.Invoker;

import java.util.List;

public class DefaultFilterChain implements FilterChain {
    private List<Filter> filters;

    private Invoker originalInvoker;

    private Invoker invoker;

    public DefaultFilterChain(List<Filter> filters, Invoker invoker) {
        this.filters = filters;
        this.originalInvoker = invoker;

        Invoker nextNode = null;

        for (int i = filters.size() - 1; i >= 0; i--) {
            if (nextNode == null) {
                nextNode = new FilterChainNodeInvoker(filters.get(i), originalInvoker);
                this.invoker = nextNode;
            } else {
                this.invoker = new FilterChainNodeInvoker(filters.get(i), nextNode);
            }
        }
    }

    @Override
    public RpcResponse invoke(RpcRequest request) {
        return invoker.invoke(request);
    }

    public static class FilterChainNodeInvoker implements Invoker {
        private Filter filter;
        private Invoker nextNode;

        public FilterChainNodeInvoker(Filter filter, Invoker nextNode) {
            this.filter = filter;
            this.nextNode = nextNode;
        }

        @Override
        public RpcResponse invoke(RpcRequest request) {
            return filter.filter(nextNode, request);
        }
    }
}

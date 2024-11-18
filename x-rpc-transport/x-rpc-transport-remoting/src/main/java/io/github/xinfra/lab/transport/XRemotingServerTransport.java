package io.github.xinfra.lab.transport;

import io.github.xinfra.lab.remoting.processor.UserProcessor;
import io.github.xinfra.lab.remoting.rpc.server.RpcServer;
import io.github.xinfra.lab.remoting.rpc.server.RpcServerConfig;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.transport.ServerTransport;

import java.io.IOException;

public class XRemotingServerTransport implements ServerTransport {
    private XRemotingServerConfig serverConfig;
    private RpcServer rpcServer;

    public XRemotingServerTransport(XRemotingServerConfig serverConfig) {
        this.serverConfig = serverConfig;

        RpcServerConfig rpcServerConfig = new RpcServerConfig();
        rpcServerConfig.setPort(serverConfig.serverPort());
        this.rpcServer = new RpcServer(rpcServerConfig);
        rpcServer.registerUserProcessor(new RpcProcessor());
        this.rpcServer.startup();
    }


    @Override
    public void register(ServiceConfig<?> serviceConfig, Invoker invoker) {
        // todo
    }

    @Override
    public void unRegister(ServiceConfig<?> serviceConfig, Invoker invoker) {
        // todo
    }

    @Override
    public void close() throws IOException {
        rpcServer.shutdown();
    }

    static class RpcProcessor implements UserProcessor<RpcRequest> {
        @Override
        public String interest() {
            return RpcRequest.class.getName();
        }

        @Override
        public Object handRequest(RpcRequest request) {
            // todo
            return null;
        }
    }
}

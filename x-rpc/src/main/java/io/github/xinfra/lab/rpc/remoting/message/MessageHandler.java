package io.github.xinfra.lab.rpc.remoting.message;

import java.util.concurrent.Executor;

public interface MessageHandler {

    Executor executor();
}

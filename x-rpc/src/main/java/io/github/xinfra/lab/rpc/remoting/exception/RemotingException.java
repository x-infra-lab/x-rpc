package io.github.xinfra.lab.rpc.remoting.exception;

public class RemotingException extends RuntimeException {
    public RemotingException(Throwable t) {
        super(t);
    }

    public RemotingException(String message) {
        super(message);
    }
}

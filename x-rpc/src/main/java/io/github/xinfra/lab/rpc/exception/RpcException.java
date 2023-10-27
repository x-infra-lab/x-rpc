package io.github.xinfra.lab.rpc.exception;

public class RpcException extends RuntimeException {

    private int errorCode;

    public RpcException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

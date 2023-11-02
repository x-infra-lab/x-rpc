package io.github.xinfra.lab.rpc.invoker;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class RpcResponse implements Serializable {
    private boolean isError;
    private String errorMsg;

    private Object result;
}

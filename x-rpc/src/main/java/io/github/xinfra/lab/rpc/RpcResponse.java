package io.github.xinfra.lab.rpc;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RpcResponse {
    private boolean isError;
    private String errorMsg;

    private Object result;


}

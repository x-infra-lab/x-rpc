package io.github.xinfra.lab.rpc;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RpcRequest {

    private String interfaceName;
    private String methodName;
    private String[] argSigns;
    private transient Object[] args;

}

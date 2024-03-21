package io.github.xinfra.lab.rpc.invoker;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Invocation {
    private String interfaceName;
    private String methodName;
    private String[] argSigns;
    private transient Object[] args;
}

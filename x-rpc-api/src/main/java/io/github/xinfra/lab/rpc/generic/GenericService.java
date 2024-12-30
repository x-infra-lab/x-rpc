package io.github.xinfra.lab.rpc.generic;

public interface GenericService {
    Object $invoke(String methodName, String[] argTypes, Object[] args);
}

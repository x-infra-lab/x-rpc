package io.github.xinfra.lab.rpc.config;

import lombok.Data;

@Data
public class ServiceConfig<T> {

    private Class<T> serviceClass;

    private String group;

    private String version;

    private String protocol;

}

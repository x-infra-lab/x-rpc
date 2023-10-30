package io.github.xinfra.lab.rpc.registry;

import lombok.Getter;

import java.util.List;

@Getter
public class ProviderGroup {
    public ProviderGroup() {
    }

    public ProviderGroup(String name, List<ProviderInfo> providerInfoList) {
        this.name = name;
        this.providerInfoList = providerInfoList;
    }

    private String name;
    private List<ProviderInfo> providerInfoList;
}

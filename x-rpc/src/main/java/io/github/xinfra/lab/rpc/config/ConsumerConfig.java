package io.github.xinfra.lab.rpc.config;

import io.github.xinfra.lab.rpc.filter.ClusterFilter;
import io.github.xinfra.lab.rpc.filter.Filter;
import lombok.Data;

import java.util.List;

@Data
public class ConsumerConfig extends BaseConfig {

    private List<ClusterFilter> clusterFilters;

    private List<Filter> filters;
}

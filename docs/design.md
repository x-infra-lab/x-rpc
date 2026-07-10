# x-rpc Design

## Design Goals

1. **Lightweight** - Minimal dependencies, no heavy framework coupling. Core module depends only on `x-rpc-api` and Guava.
2. **High Performance** - Netty-based async transport, zero-copy serialization, connection pooling and reuse.
3. **Extensible** - SPI-style interfaces for transport, registry, load balancer, router, filter. Adding a new implementation requires no modification to existing code.
4. **Production Ready** - Built-in service governance: fault tolerance, rate limiting, graceful shutdown, health-aware routing.
5. **Spring Boot Friendly** - Annotation-driven with `@XRpcService` / `@XRpcReference`, auto-configuration, YAML properties binding.

## Core Design Patterns

### Filter Chain

Filters intercept RPC invocations at both provider and consumer sides. They are composed into a chain via `FilterChainBuilder`, which wraps an `Invoker` in successive `FilterChainNodeInvoker` wrappers from the inside out.

```
Request  ──→ Filter[0] ──→ Filter[1] ──→ ... ──→ Filter[N] ──→ Invoker
Response ←── Filter[0] ←── Filter[1] ←── ... ←── Filter[N] ←── Invoker
```

Provider and consumer have **separate filter chains**:

| Side | Filters (in order) |
|------|-------------------|
| Provider | `GracefulShutdownFilter` → `TraceFilter` → `MetricFilter` → `ProviderGenericFilter` → `ProviderInvoker` |
| Consumer | `TraceFilter` → `MetricFilter` → `ConsumerGenericFilter` → `ConsumerInvoker` |

Cluster-level filters (`ClusterFilter`) wrap the `ClusterInvoker` and execute before load balancing and instance selection.

### Cluster Abstraction

The `Cluster` interface encapsulates the client-side view of a service provider group. It holds:
- `NameService` - manages provider instances and their health state
- `ClientTransport` - network connections to providers
- `ClusterInvoker` - the invocation strategy

Two cluster strategies:

| Strategy | Behavior |
|----------|----------|
| **FailFast** | Invoke once; propagate any failure immediately |
| **FailOver** | On `RpcClientException` (network/timeout), retry on a different instance up to N times. Business exceptions (`RpcServerException`) are NOT retried |

### NameService Health Management

`DefaultNameService` maintains two sets of instances: **healthy** and **unhealthy**. It listens for transport events to track connection state:

```
Registry notification (new instances)
    │
    ├── connect() success ──→ healthServiceInstances
    └── connect() failure ──→ unHealthServiceInstances + reconnect()

Transport DISCONNECT event ──→ move to unHealthServiceInstances + reconnect()
Transport CONNECT event    ──→ move back to healthServiceInstances
```

Only healthy instances are returned by `getInstances()`. This ensures the load balancer never selects a disconnected provider.

### SPI Extension Points

| Extension Point | Interface | Implementations |
|----------------|-----------|-----------------|
| Transport | `ClientTransport`, `ServerTransport` | `XRemotingClientTransport`, `XRemotingServerTransport` |
| Registry | `Registry` | `ZookeeperRegistry` |
| Load Balancer | `LoadBalancer` | `RandomLoadBalancer`, `RoundRobinLoadBalancer`, `WeightedLoadBalancer` |
| Router | `Router` | `ServiceGroupRouter` |
| Cluster | `Cluster` | `FastFailCluster`, `FailoverCluster` |
| Proxy | `Proxy` | `JDKProxy` |
| Filter | `Filter`, `ClusterFilter` | 7 built-in filters |

## RPC Call Flow

### Consumer Side

```
User code
  │  method call
  ▼
JDKProxy.invoke()
  │  build Invocation (service, method, args, timeout)
  ▼
ClusterFilter chain (if any)
  │
  ▼
ClusterInvoker.invoke()
  │  NameService.getInstances() → get healthy instances
  │  RouterChain.route() → filter by group/tag
  │  LoadBalancer.select() → pick one instance
  │  set Invocation.targetAddress
  ▼
Consumer Filter chain
  │  TraceFilter: attach traceId/spanId
  │  MetricFilter: count + timing
  │  ConsumerGenericFilter: rewrite generic invocation
  ▼
ConsumerInvoker.invoke()
  │  build RpcRequest
  │  ClientTransport.sendAsync() → Netty channel write
  │  CompletableFuture.get(timeout) → block for response
  ▼
Return result to user
```

### Provider Side

```
Netty server receives bytes
  │
  ▼
RpcRequestProcessor.handRequest()
  │  lookup Invoker by serviceName
  │  lookup Method by methodName + argTypes (ReflectCache)
  │  build Invocation
  ▼
Provider Filter chain
  │  GracefulShutdownFilter: reject if shutting down, track active count
  │  TraceFilter: read/generate traceId/spanId
  │  MetricFilter: count + timing
  │  ProviderGenericFilter: deserialize generic args
  ▼
ProviderInvoker.invoke()
  │  Method.invoke(serviceImpl, args)
  │  wrap result as InvocationResult
  ▼
RpcResponse → Netty channel write back
```

## Service Governance

### Load Balancing

| Strategy | Algorithm | Use Case |
|----------|-----------|----------|
| Random | `ThreadLocalRandom.nextInt(size)` | Default, good for homogeneous clusters |
| RoundRobin | `AtomicLong` counter mod size | Even distribution, uses `idx & Long.MAX_VALUE` to avoid overflow |
| Weighted | Weighted random with warmup | Heterogeneous clusters, supports gradual warmup on startup |

**Warmup**: For newly started instances, weight is linearly scaled from 1 to full weight over the configured warmup period, based on `registrationTimestamp`.

### Rate Limiting

Per-service TPS limiting using Guava `RateLimiter` (token bucket). Separate limiters for provider and consumer:

- `ProviderTpsLimitFilter` - protects provider from overload
- `ConsumerTpsLimitFilter` - prevents consumer from flooding provider

Rate limiters are lazily created per service name and cached in a `ConcurrentHashMap`.

### Graceful Shutdown

Shutdown sequence:

```
JVM shutdown hook triggered
  │
  ▼
GracefulShutdown.shuttingDown = true
  │  GracefulShutdownFilter starts rejecting new requests
  ▼
Wait for activeCount to reach 0 (max 30s, poll every 100ms)
  │
  ▼
ProviderBoostrap.close()
  │  RegistryManager.close() → unregister from ZK
  │  ServerTransportManager.close() → stop Netty server
  ▼
Shutdown complete
```

### Service Routing

`ServiceGroupRouter` filters instances by group tag in invocation attachments. Falls back to all instances if no matching group is found, with a warning log.

### Metadata & Revision

Each `ServiceInstance` carries `MetadataInfo` containing a `TreeMap<interfaceName, ServiceInfo>`. When services are added or removed, a revision hash (MD5 of the metadata string) is computed. The instance is only re-registered in ZooKeeper when the revision changes, minimizing ZK writes.

## Configuration Model

All configuration flows through typed config objects:

```
ApplicationConfig ─── appName
ProviderConfig ─────── applicationConfig + registryConfig + protocolConfig + filters
ConsumerConfig ─────── applicationConfig + registryConfig + protocolConfig + filters + clusterFilters + routerChain
ExporterConfig ──────── serviceInterfaceClass + serviceImpl + weight + warmupMills + tpsLimit
ReferenceConfig ─────── serviceInterfaceClass + appName + clusterType + loadBalanceType + retries + timeoutMills + tpsLimit
```

Spring Boot maps YAML properties via `@ConfigurationProperties`:

| Prefix | Config Class |
|--------|-------------|
| `x.rpc.application` | `ApplicationConfig` |
| `x.rpc.registry.zookeeper` | `ZookeeperConfig` |
| `x.rpc.protocol.server` | `XRemotingTransportServerConfig` |
| `x.rpc.protocol.client` | `XRemotingTransportClientConfig` |

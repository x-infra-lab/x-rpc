# x-rpc Source Code Guide

This guide walks through the key classes and call chains in the x-rpc source code. It is intended for contributors and developers who want to understand the internals.

## Consumer Side Call Chain

When a consumer calls a remote service method, the following chain executes:

### 1. JDKProxy (Entry Point)

**File**: `x-rpc-core/.../proxy/JDKProxy.java`

The JDK dynamic proxy intercepts all method calls on the service interface. It builds an `Invocation` object containing the service name, method name, argument types, arguments, and timeout, then delegates to the cluster invoker.

```
JDKProxy.JDKInvocationHandler.invoke(proxy, method, args)
  → build Invocation
  → set timeoutMills from ReferenceConfig
  → invoker.invoke(invocation)
  → invocationResult.invokeResult()  // unwrap result or throw RpcServerException
```

Object methods (`toString`, `hashCode`, `equals`) are handled locally without RPC.

### 2. ClusterInvoker (Fault Tolerance)

**File**: `x-rpc-core/.../cluster/AbstractClusterInvoker.java`

The abstract base class handles instance retrieval and routing:

```java
public InvocationResult invoke(Invocation invocation) {
    List<ServiceInstance> instances = nameService.getInstances(invocation);
    instances = routerChain.route(invocation, instances);
    return doInvoke(invocation, instances);
}
```

**FailFastClusterInvoker** (`x-rpc-core/.../cluster/FailFastClusterInvoker.java`):
- Selects one instance via LoadBalancer
- Invokes once, propagates any exception

**FailoverClusterInvoker** (`x-rpc-core/.../cluster/FailoverClusterInvoker.java`):
- Attempts `retries + 1` times
- On `RpcClientException` (network/timeout), excludes the tried instance and retries
- On business exceptions (e.g., `RpcServerException`), fails immediately without retry
- Falls back to all instances if untried instances are exhausted

### 3. FilterChainBuilder (Filter Composition)

**File**: `x-rpc-core/.../filter/FilterChainBuilder.java`

Wraps the terminal invoker with filters in reverse order, creating a chain:

```java
// For filters [A, B, C] and invoker I:
// Result: A → B → C → I
Invoker nextNode = invoker;
for (int i = filters.size() - 1; i >= 0; i--) {
    invoker = new FilterChainNodeInvoker(filters.get(i), nextNode);
    nextNode = invoker;
}
```

Each `FilterChainNodeInvoker` calls `filter.filter(nextNode, invocation)` and handles `onResult`/`onError` callbacks via `whenComplete`.

### 4. ConsumerInvoker (Network Call)

**File**: `x-rpc-core/.../invoker/ConsumerInvoker.java`

Converts `Invocation` to `RpcRequest`, sends it asynchronously, and blocks for the response:

```java
CompletableFuture<RpcResponse> future = clientTransport.sendAsync(
    invocation.getTargetAddress(), request, timeoutMills, callbackExecutor);
// ... complete InvocationResult on response/error
return result.get(timeoutMills);  // blocking wait
```

Timeout throws `RpcTimeoutException`. Network errors throw `RpcClientException`.

### 5. ClientTransport (Network I/O)

**File**: `x-rpc-transport-remoting/.../XRemotingClientTransport.java`

Delegates to `x-remoting` library's `RpcClient`:
- `connect()` - establishes a Netty connection to a provider
- `sendAsync()` - writes `RpcRequest` and registers an async callback
- `disconnect()` / `reconnect()` - connection lifecycle
- `addTransportEventListener()` - notifies `NameService` on CONNECT/DISCONNECT events

## Provider Side Call Chain

### 1. RpcRequestProcessor (Request Dispatch)

**File**: `x-rpc-transport-remoting/.../RpcRequestProcessor.java`

Netty's thread pool delivers deserialized `RpcRequest` objects here:

```java
public RpcResponse handRequest(RpcRequest request) {
    Invoker invoker = invokerMap.get(request.getServiceName());
    Method method = reflectCache.find(serviceName, methodName, argTypes);
    // build Invocation
    InvocationResult result = invoker.invoke(invocation);
    return InvokeTypes.convertRpcResponse(result);
}
```

Errors are caught and returned as `RpcResponse(success=false, errorMsg=className: message)`.

### 2. Provider Filter Chain

Same `FilterChainBuilder` mechanism as consumer, but with provider-specific filters:

1. **GracefulShutdownFilter** - If `shuttingDown`, rejects with `RpcException`. Otherwise increments active count, invokes, decrements in `finally`.
2. **TraceFilter** - Reads or generates `traceId`/`spanId` in invocation attachments.
3. **MetricFilter** - Increments total/success/fail counters, measures latency via `System.nanoTime()` in try/finally. Logs aggregated metrics every 1000 requests.
4. **ProviderGenericFilter** - For generic invocations, deserializes JSON string arguments to the method's actual parameter types.

### 3. ProviderInvoker (Business Logic)

**File**: `x-rpc-core/.../invoker/ProviderInvoker.java`

Performs reflective method invocation on the service implementation:

```java
Object result = invocation.getMethod().invoke(exporterConfig.getServiceImpl(), args);
```

`InvocationTargetException` is unwrapped; all errors are wrapped as `RpcServerException`.

## Service Registration & Discovery

### Registration Flow (Provider)

**File**: `x-rpc-core/.../bootstrap/ProviderBoostrap.java`

```
ProviderBoostrap.export(exporterConfig)
  │  create ProviderInvoker + FilterChain
  │  register invoker with ServerTransport
  │
  │  Registry.initInstance(appName, protocol, address)  // once
  │  ServiceInstance.addService(exporterConfig)          // add to metadata
  │  ServiceInstance.isRevisionChanged()                 // compute MD5
  │
  ├── first export → Registry.registerInstance()         // create ZK node
  └── subsequent  → Registry.updateInstance()            // update ZK node
```

### Discovery Flow (Consumer)

**File**: `x-rpc-core/.../bootstrap/ConsumerBootstrap.java`

```
ConsumerBootstrap.refer(referenceConfig)
  │  get/create ClientTransport (shared)
  │  create Cluster (with NameService, filters, invoker)
  │
  │  Registry.addAppServiceInstancesWatcher(appName)
  │    └── creates ServiceCache → ZK watcher
  │    └── initial query → NameService.notify(instances)
  │
  │  Registry.subscribe(appName, nameService)
  │    └── nameService receives future change notifications
  │
  │  create JDK Proxy with ClusterInvoker
  └── return proxy to user
```

### ZookeeperRegistry Internals

**File**: `x-rpc-registry-zookeeper/.../ZookeeperRegistry.java`

- Uses `CuratorFramework` with `ExponentialBackoffRetry(1000ms, 3 retries)` for ZK connection resilience
- `ServiceDiscovery<ZookeeperInstancePayload>` manages ephemeral ZK nodes under `basePath`
- `ServiceCache` provides local caching with `ZookeeperServiceDiscoveryChangeWatcher` for real-time notifications
- Instance changes trigger `AppServiceInstancesWatcher.change()` → `NameService.notify()` → connection management

## Key Bootstrap Classes

### ConsumerBootstrap

**File**: `x-rpc-core/.../bootstrap/ConsumerBootstrap.java`

- Creates a shared `ThreadPoolExecutor(10, 100, 60s, queue=1024, CallerRunsPolicy)` for async callbacks
- `refer()` creates proxy for each service reference
- `close()` shuts down executor (30s await), then closes registry and transport managers
- Supports **direct connect mode**: skips cluster/registry, connects directly to a specified address

### ProviderBoostrap

**File**: `x-rpc-core/.../bootstrap/ProviderBoostrap.java`

- `export()` is `synchronized` to prevent concurrent registration conflicts
- Detects duplicate exports by checking `exportedExporterConfigs`
- Automatically exports `MetadataService` alongside the first business service
- `register()` batch-registers all exported services (called by `XRpcApplicationListener` on Spring context refresh)
- `unExport()` removes service from metadata, updates ZK, and unregisters from transport
- Registers a JVM shutdown hook via `GracefulShutdown.INSTANCE`

### DefaultNameService

**File**: `x-rpc-core/.../cluster/naming/DefaultNameService.java`

- All methods are `synchronized` for thread safety
- `notify()` diffs old vs new instance sets: connects new, disconnects removed
- `onEvent()` handles transport CONNECT/DISCONNECT to move instances between health sets
- `getInstances()` throws `NoAvailableProviderException` if no healthy instances exist

## Spring Boot Integration

### Auto-Configuration

**File**: `x-rpc-spring-boot-starter/.../XRpcAutoConfiguration.java`

Bean creation order (managed by `@ConditionalOnBean` chains):

```
ApplicationConfig
  → ZookeeperConfig → ZookeeperRegistryConfig
  → XRemotingTransportServerConfig + XRemotingTransportClientConfig
    → XRemotingTransportConfig → ProtocolConfig
      → ProviderBoostrap (with provider filters)
      → ConsumerBootstrap (with consumer filters + routers)
        → XRpcApplicationListener
```

Provider and consumer filters are built as **separate lists** via `buildProviderFilters()` and `buildConsumerFilters()` private methods, preventing filter cross-contamination.

### @XRpcService Processing

**File**: `x-rpc-spring-boot-starter/.../bean/XRpcServiceAnnotationPostProcessor.java`

During `BeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry()`:

1. Scans configured packages for `@XRpcService` annotated classes
2. Resolves the service interface: uses `interfaceClass` if specified, otherwise finds the first non-`java.*` interface
3. Creates `ExporterConfig` and `XRpcServiceBean` bean definitions
4. `XRpcServiceBean.afterPropertiesSet()` calls `providerBoostrap.export()`

### @XRpcReference Processing

**File**: `x-rpc-spring-boot-starter/.../bean/XRpcReferenceAnnotationPostProcessor.java`

During `InstantiationAwareBeanPostProcessor.postProcessProperties()`:

1. Scans all fields for `@XRpcReference` annotation
2. Generates a unique bean name: `interfaceName@appName+XRpcReferenceFactoryBean`
3. Registers `XRpcReferenceFactoryBean` if not already registered
4. `XRpcReferenceFactoryBean.getObject()` calls `consumerBootstrap.refer()` to create the proxy
5. Injects the proxy into the field

### Deferred Registration

**File**: `x-rpc-spring-boot-starter/.../context/XRpcApplicationListener.java`

Listens for `ContextRefreshedEvent` and calls `providerBoostrap.register()` to batch-register all exported services. This ensures all services are exported before any ZK registration happens, preventing partial visibility.

# x-rpc Architecture

## Overview

x-rpc is a lightweight Java RPC framework built on a layered, modular architecture. Each layer has a clear responsibility boundary and communicates through well-defined interfaces, making the framework easy to extend and maintain.

## Layered Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Application Layer                   │
│          x-rpc-console  /  x-rpc-example             │
├─────────────────────────────────────────────────────┤
│              Spring Boot Integration Layer            │
│               x-rpc-spring-boot-starter               │
├─────────────────────────────────────────────────────┤
│                Core Implementation Layer              │
│                     x-rpc-core                        │
├──────────────────────┬──────────────────────────────┤
│   Transport Layer    │      Registry Layer           │
│ x-rpc-transport-     │  x-rpc-registry-              │
│   remoting           │    zookeeper                   │
├──────────────────────┴──────────────────────────────┤
│                   API Layer (SPI)                     │
│                     x-rpc-api                         │
└─────────────────────────────────────────────────────┘
```

## Module Structure

```
x-rpc (parent pom)
├── x-rpc-api                          # Core interfaces and models
├── x-rpc-core                         # Implementation of cluster, filter, proxy, invoker
├── x-rpc-transport/                   # Transport abstraction
│   └── x-rpc-transport-remoting       # x-remoting based transport implementation
├── x-rpc-registry/                    # Registry abstraction
│   └── x-rpc-registry-zookeeper       # ZooKeeper based registry implementation
├── x-rpc-spring-boot-starter          # Spring Boot auto-configuration
├── x-rpc-console                      # Management web console
├── x-rpc-test                         # Integration tests
└── x-rpc-example                      # Usage examples
```

## Module Responsibilities

### x-rpc-api

The SPI (Service Provider Interface) layer. Defines all core abstractions as Java interfaces and data models, with zero implementation dependency.

Key packages:

| Package | Contents |
|---------|----------|
| `cluster` | `Cluster`, `ClusterInvoker`, `LoadBalancer`, `Router`, `RouterChain` |
| `config` | `ApplicationConfig`, `ProviderConfig`, `ConsumerConfig`, `ReferenceConfig`, `ExporterConfig` |
| `filter` | `Filter`, `ClusterFilter` |
| `invoker` | `Invoker`, `Invocation`, `InvocationResult`, `RpcRequest`, `RpcResponse` |
| `registry` | `Registry`, `ServiceInstance`, `NotifyListener`, `AppServiceInstancesWatcher` |
| `transport` | `ClientTransport`, `ServerTransport` |
| `proxy` | `Proxy` |
| `metadata` | `MetadataInfo`, `ServiceInfo` |

### x-rpc-core

Implements all core logic. This is the heart of the framework.

| Package | Contents |
|---------|----------|
| `bootstrap` | `ConsumerBootstrap`, `ProviderBoostrap`, `GracefulShutdown` |
| `cluster` | `AbstractCluster`, `FastFailCluster`, `FailoverCluster`, `FailoverClusterInvoker` |
| `cluster.loadblancer` | `RandomLoadBalancer`, `RoundRobinLoadBalancer`, `WeightedLoadBalancer` |
| `cluster.naming` | `DefaultNameService` (health-aware instance management) |
| `cluster.router` | `ServiceGroupRouter` |
| `filter` | `FilterChainBuilder`, `GracefulShutdownFilter`, `MetricFilter`, `TraceFilter`, `ProviderTpsLimitFilter`, `ConsumerTpsLimitFilter`, `ProviderGenericFilter`, `ConsumerGenericFilter` |
| `invoker` | `ConsumerInvoker`, `ProviderInvoker`, `DirectConnectInvoker` |
| `proxy` | `JDKProxy`, `ProxyManager` |
| `transport` | `ClientTransportManager`, `ServerTransportManager` |
| `registry` | `RegistryManager`, `RegistryFactory` |

### x-rpc-transport-remoting

Network transport implementation based on [x-remoting](https://github.com/x-infra-lab/x-remoting) (Netty-based).

| Class | Role |
|-------|------|
| `XRemotingClientTransport` | Client-side: connection management, async RPC calls |
| `XRemotingServerTransport` | Server-side: starts Netty server, registers request processors |
| `RpcRequestProcessor` | Dispatches incoming RPC requests to the correct `Invoker` |

### x-rpc-registry-zookeeper

Service registry implementation using Apache Curator + ZooKeeper.

| Class | Role |
|-------|------|
| `ZookeeperRegistry` | Register/unregister instances, subscribe/watch for changes |
| `ZookeeperServiceDiscoveryChangeWatcher` | Handles ZK node change events |
| `InstanceConverter` | Converts between `ServiceInstance` and Curator `ServiceInstance` |

### x-rpc-spring-boot-starter

Spring Boot integration with annotation-driven programming model.

| Class | Role |
|-------|------|
| `XRpcAutoConfiguration` | Auto-configures all beans (config, transport, registry, bootstrap) |
| `XRpcServiceAnnotationPostProcessor` | Scans `@XRpcService` classes and registers them as providers |
| `XRpcReferenceAnnotationPostProcessor` | Resolves `@XRpcReference` fields and injects consumer proxies |
| `XRpcServiceBean` | Bridges Spring bean lifecycle to provider export |
| `XRpcReferenceFactoryBean` | Creates consumer proxy via `ConsumerBootstrap.refer()` |
| `XRpcApplicationListener` | Triggers batch service registration on `ContextRefreshedEvent` |

### x-rpc-console

Standalone Spring Boot web application for monitoring and managing registered services.

| Component | Role |
|-----------|------|
| `ConsoleRegistryService` | Connects to ZooKeeper, queries service data |
| `AppController` | REST API: list applications |
| `InstanceController` | REST API: list/get/enable/disable instances |
| `ServiceController` | REST API: list services and providers |
| `static/` | Single-page web UI (vanilla HTML/JS/CSS) |

## Module Dependencies

```
x-rpc-spring-boot-starter ──→ x-rpc-core
                           ──→ x-rpc-registry-zookeeper
                           ──→ x-rpc-transport-remoting

x-rpc-core ──→ x-rpc-api

x-rpc-transport-remoting ──→ x-rpc-api
                         ──→ x-remoting (external)

x-rpc-registry-zookeeper ──→ x-rpc-api
                         ──→ curator-x-discovery (external)

x-rpc-console ──→ x-rpc-registry-zookeeper
              ──→ x-rpc-api
              ──→ spring-boot-starter-web

x-rpc-test ──→ x-rpc-core
           ──→ x-rpc-registry-zookeeper
           ──→ x-rpc-transport-remoting
```

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Network | Netty 4.x (via x-remoting) |
| Serialization | Hessian 2 (via x-remoting) |
| Service Discovery | Apache ZooKeeper 3.5+ / Curator 5.7.1 |
| Rate Limiting | Guava RateLimiter |
| Spring Integration | Spring Boot 2.7.18 |
| Build | Maven 3.6+, Java 1.8+ |
| Code Style | Google Java Format (Spotless) |
| CI | GitHub Actions |

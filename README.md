# x-rpc

[![Java CI with Maven](https://github.com/x-infra-lab/x-rpc/actions/workflows/maven.yml/badge.svg)](https://github.com/x-infra-lab/x-rpc/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-1.8+-green.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)

A lightweight, high-performance Java RPC framework with built-in service governance, supporting service registration/discovery, load balancing, fault tolerance, rate limiting, and graceful shutdown.

## Architecture

```
                    ┌─────────────────────────────────────┐
                    │            x-rpc-console             │
                    │       (Management Dashboard)         │
                    └──────────────┬──────────────────────┘
                                   │ ZooKeeper
    ┌──────────────────────────────┼──────────────────────────────┐
    │                              │                              │
    │  Consumer                    │                   Provider   │
    │  ┌───────────────────┐       │       ┌───────────────────┐  │
    │  │   Proxy (JDK)     │       │       │  ProviderInvoker  │  │
    │  │        ↓          │       │       │        ↑          │  │
    │  │   Filter Chain    │       │       │   Filter Chain    │  │
    │  │   (Trace/Metric/  │       │       │   (Shutdown/Trace │  │
    │  │    TpsLimit)       │       │       │    /Metric/TPS)   │  │
    │  │        ↓          │       │       │        ↑          │  │
    │  │   ClusterInvoker  │       │       │  ServerTransport  │  │
    │  │   (FailFast/      │       │       └───────────────────┘  │
    │  │    FailOver)       │       │                              │
    │  │        ↓          │       │                              │
    │  │   RouterChain     │       │                              │
    │  │        ↓          │       │                              │
    │  │   LoadBalancer    │───────┼───── x-remoting ────────────→│
    │  └───────────────────┘       │                              │
    │                              │                              │
    └──────────────────────────────┴──────────────────────────────┘
                    ┌──────────────────────────┐
                    │   ZooKeeper Registry     │
                    │   (Service Discovery)    │
                    └──────────────────────────┘
```

## Features

- **Service Registration & Discovery** - ZooKeeper-based, with automatic instance health management
- **Multiple Load Balancing** - Random, RoundRobin, Weighted (with warmup support)
- **Fault Tolerance** - FailFast (fail immediately) and FailOver (retry on failure)
- **Service Routing** - Group-based routing with fallback
- **Rate Limiting** - Per-service TPS limiting via Guava RateLimiter (provider & consumer)
- **Graceful Shutdown** - Active request drain with configurable timeout
- **Distributed Tracing** - Automatic trace/span ID propagation
- **Metrics Collection** - Request count, success/fail ratio, latency tracking
- **Generic Invocation** - Call services without compile-time interface dependency
- **Spring Boot Integration** - `@XRpcService` / `@XRpcReference` annotation-driven
- **Management Console** - Web UI for service/instance monitoring and control

## Modules

| Module | Description |
|--------|-------------|
| `x-rpc-api` | Core interfaces and data models (Filter, Invoker, Cluster, Registry, etc.) |
| `x-rpc-core` | Implementation of cluster, load balancing, routing, filters, proxy |
| `x-rpc-transport` | Network transport layer (based on [x-remoting](https://github.com/x-infra-lab/x-remoting)) |
| `x-rpc-registry` | ZooKeeper service registry implementation (Apache Curator) |
| `x-rpc-spring-boot-starter` | Spring Boot auto-configuration with annotation support |
| `x-rpc-console` | Web-based management console for monitoring and operations |
| `x-rpc-test` | Integration tests |

## Quick Start

### Prerequisites

- JDK 1.8+
- Maven 3.6+
- ZooKeeper 3.5+

### 1. Define a Service Interface

```java
public interface EchoService {
    String hello(String name);
}
```

### 2. Provider

```java
// Implementation
public class EchoServiceImpl implements EchoService {
    @Override
    public String hello(String name) {
        return "Hello, " + name;
    }
}

// Bootstrap
ApplicationConfig appConfig = new ApplicationConfig();
appConfig.setAppName("echo-provider");

ZookeeperConfig zkConfig = new ZookeeperConfig();
zkConfig.setZkAddress("127.0.0.1:2181");
ZookeeperRegistryConfig registryConfig = new ZookeeperRegistryConfig(zkConfig);

XRemotingTransportConfig transportConfig = new XRemotingTransportConfig();
transportConfig.setTransportServerConfig(new XRemotingTransportServerConfig());

XProtocolConfig protocolConfig = new XProtocolConfig();
protocolConfig.setXRemotingTransportConfig(transportConfig);

ProviderConfig providerConfig = new ProviderConfig();
providerConfig.setApplicationConfig(appConfig);
providerConfig.setRegistryConfig(registryConfig);
providerConfig.setProtocolConfig(protocolConfig);

ProviderBoostrap provider = ProviderBoostrap.from(providerConfig);

ExporterConfig<EchoService> exporterConfig = new ExporterConfig<>(EchoService.class);
exporterConfig.setServiceImpl(new EchoServiceImpl());
provider.export(exporterConfig);
```

### 3. Consumer

```java
ApplicationConfig appConfig = new ApplicationConfig();
appConfig.setAppName("echo-consumer");

// ... same registry/transport/protocol setup ...

ConsumerConfig consumerConfig = new ConsumerConfig();
consumerConfig.setApplicationConfig(appConfig);
consumerConfig.setRegistryConfig(registryConfig);
consumerConfig.setProtocolConfig(protocolConfig);

ConsumerBootstrap consumer = ConsumerBootstrap.from(consumerConfig);

ReferenceConfig<EchoService> refConfig = new ReferenceConfig<>(EchoService.class);
refConfig.setAppName("echo-provider");

EchoService echoService = consumer.refer(refConfig);
String result = echoService.hello("world"); // "Hello, world"
```

### 4. Spring Boot Integration

```java
// Provider
@XRpcService
public class EchoServiceImpl implements EchoService {
    @Override
    public String hello(String name) {
        return "Hello, " + name;
    }
}

// Consumer
@Component
public class EchoConsumer {
    @XRpcReference(appName = "echo-provider")
    private EchoService echoService;

    public String sayHello(String name) {
        return echoService.hello(name);
    }
}
```

`application.yml`:

```yaml
x:
  rpc:
    application:
      app-name: my-app
    registry:
      zookeeper:
        zk-address: 127.0.0.1:2181
```

## Service Governance

### Load Balancing

```java
@XRpcReference(appName = "provider", loadBalanceType = "WEIGHTED")
private EchoService echoService;
```

| Strategy | Description |
|----------|-------------|
| `RANDOM` | Random selection (default) |
| `ROUND_ROBIN` | Sequential rotation |
| `WEIGHTED` | Weight-based with warmup period support |

### Fault Tolerance

```java
@XRpcReference(appName = "provider", clusterType = "FAIL_OVER", retries = 3)
private EchoService echoService;
```

| Strategy | Description |
|----------|-------------|
| `FAST_FAIL` | Fail immediately on error (default) |
| `FAIL_OVER` | Retry on different instances up to N times |

### Rate Limiting

```java
// Consumer-side TPS limit
@XRpcReference(appName = "provider", tpsLimit = 1000)
private EchoService echoService;

// Provider-side TPS limit
@XRpcService(tpsLimit = 5000)
public class EchoServiceImpl implements EchoService { ... }
```

### Service Group Routing

```java
@XRpcReference(appName = "provider", routeGroup = "canary")
private EchoService echoService;
```

## Console

The management console provides a web UI for monitoring and operating services.

```bash
cd x-rpc-console
mvn spring-boot:run
# Visit http://localhost:8888
```

Features:
- View all registered applications and their instance counts
- Inspect instance details (address, port, protocol, revision, properties)
- Enable/disable instances dynamically
- Browse services and their providers
- Auto-refresh every 10 seconds

## Documentation

- [Architecture](docs/architecture.md) - Module structure, layered design, dependency graph
- [Design](docs/design.md) - Design goals, core patterns, RPC flow, service governance
- [Source Guide](docs/source-guide.md) - Call chain walkthrough, key class analysis, Spring Boot integration internals

## Build

```bash
# Build all modules
mvn clean install

# Run tests
mvn test

# Check code style
mvn spotless:check
```

## License

[Apache License 2.0](LICENSE)

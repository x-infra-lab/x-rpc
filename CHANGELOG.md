# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- FailOver cluster with configurable retry count
- RoundRobin and Weighted load balancers (with warmup support)
- Service group routing
- TPS rate limiting for provider and consumer (Guava RateLimiter)
- Graceful shutdown with active request drain
- Distributed tracing (trace/span ID propagation)
- Metrics collection (request count, latency, success/fail ratio)
- Spring Boot starter with `@XRpcService` and `@XRpcReference` annotations
- Spring Boot configuration metadata for IDE auto-completion
- Management console (x-rpc-console) with web UI and REST API
- Console global exception handler (hides stack traces from clients)
- Example module (x-rpc-example) with provider and consumer demos
- Config validation for bootstrap classes (`ConsumerBootstrap`, `ProviderBoostrap`, `ZookeeperRegistry`)
- Maven Central publishing configuration with release profile
- CI workflow with code style check, test execution, and release publishing
- Dependabot for automated Maven and GitHub Actions dependency updates
- Unit tests for x-rpc-api, x-rpc-core, x-rpc-registry-zookeeper, x-rpc-console, x-rpc-spring-boot-starter
- Javadoc on public API interfaces and annotations
- Community files: CONTRIBUTING.md, SECURITY.md, NOTICE, issue/PR templates
- `.editorconfig` for consistent coding style
- JaCoCo code coverage reporting

### Fixed
- Provider/Consumer filter separation to prevent cross-contamination
- Bean name collision in `XRpcReferenceAnnotationPostProcessor`
- Interface selection in `XRpcServiceAnnotationPostProcessor` (filter `java.*` interfaces)
- Added missing `spring.factories` for auto-configuration
- LICENSE file now contains full Apache 2.0 text (spotless uses separate HEADER file)
- README and example YAML use correct nested property paths (`x.rpc.application.app-name`)
- ZooKeeper retry strategy changed from `RetryOneTime` to `ExponentialBackoffRetry(1000ms, 3 retries)` for production resilience
- `RpcRequestProcessor` now returns actual exception class and message instead of generic "rpc server error"
- `FailoverClusterInvoker` only retries on `RpcClientException` (network/timeout); business exceptions propagate immediately
- `MetricFilter` replaced ThreadLocal-based timing with inline try/finally to fix consumer-side metrics and ThreadLocal leak
- `ZookeeperRegistry.addAppServiceInstancesWatcher` simplified redundant synchronized + computeIfAbsent logic
- `ProviderBoostrap.unExport` fixed broken re-registration: added `MetadataInfo.removeService()`, `ServiceInstance.removeService()`, and implemented `ZookeeperRegistry.update()`
- `RoundRobinLoadBalancer` uses `idx & Long.MAX_VALUE` instead of `Math.abs(idx)` to prevent negative index on Long overflow
- `ConsumerBootstrap.refer()` in direct connect mode now calls `clientTransport.connect()` before creating the proxy

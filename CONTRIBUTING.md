# Contributing to x-rpc

Thanks for your interest in contributing! Here's how to get started.

## Development Setup

1. **Prerequisites**: JDK 1.8+, Maven 3.6+, ZooKeeper 3.5+ (for integration tests)
2. **Clone**: `git clone https://github.com/x-infra-lab/x-rpc.git`
3. **Build**: `mvn clean install`
4. **Code style**: We use Google Java Format via Spotless. Run `mvn spotless:apply` to auto-format before committing.

## How to Contribute

### Reporting Issues

- Use [GitHub Issues](https://github.com/x-infra-lab/x-rpc/issues) with the provided templates
- Include steps to reproduce, expected vs actual behavior, and your environment (JDK version, OS)

### Submitting Pull Requests

1. Fork the repository and create a branch from `main`
2. Write tests for any new functionality
3. Ensure all tests pass: `mvn test`
4. Ensure code style passes: `mvn spotless:check`
5. Keep commits focused — one logical change per PR
6. Write a clear PR description explaining what and why

### Code Guidelines

- Java 1.8 compatibility required (no `var`, no streams `.toList()`, etc.)
- Follow existing naming conventions and package structure
- Add license headers to new files (Spotless handles this automatically)
- Prefer simple, readable code over clever abstractions

## Project Structure

| Module | Purpose |
|--------|---------|
| `x-rpc-api` | Public interfaces, configs, and data models |
| `x-rpc-core` | Core implementation (cluster, filters, proxy, bootstrap) |
| `x-rpc-transport` | Network transport layer |
| `x-rpc-registry` | Service registry (ZooKeeper) |
| `x-rpc-spring-boot-starter` | Spring Boot auto-configuration |
| `x-rpc-console` | Management web console |
| `x-rpc-example` | Usage examples |
| `x-rpc-test` | Integration tests |

## License

By contributing, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE).

# Security Policy

## Supported Versions

| Version       | Supported |
|---------------|-----------|
| 0.1.x         | Yes       |

## Reporting a Vulnerability

If you discover a security vulnerability in x-rpc, please report it responsibly.

**Do not open a public GitHub issue for security vulnerabilities.**

Instead, please send an email to the project maintainers via GitHub's private vulnerability reporting feature:

1. Go to the [Security tab](https://github.com/x-infra-lab/x-rpc/security) of this repository.
2. Click **"Report a vulnerability"**.
3. Provide a description of the vulnerability, steps to reproduce, and any potential impact.

We will acknowledge receipt within 48 hours and aim to provide a fix within 7 days for critical issues.

## Security Best Practices

When deploying x-rpc in production:

- **Network isolation**: Run ZooKeeper and RPC services within a trusted network. x-rpc does not provide built-in encryption for RPC traffic.
- **Access control**: Use ZooKeeper ACLs to restrict registry access.
- **Rate limiting**: Configure TPS limits on both provider and consumer sides to prevent abuse.
- **Monitoring**: Use the management console and metrics to detect anomalous traffic patterns.

# ADR 001: Adopting Streamable HTTP for MCP Transport

## Status
Accepted

## Context
The Model Context Protocol (MCP) originally supported Server-Sent Events (SSE) and STDIO for transport. As of early 2025 (MCP Specification v2025.03.26), Standalone SSE is being deprecated in favor of **Streamable HTTP**.

## Decision
We decided to use **Streamable HTTP** as the primary transport mechanism for the Stardust AI-Native Hub (MCP).

## Rationale
1. **Cloud Native Compatibility**: Unlike STDIO, Streamable HTTP can be easily routed through standard Cloud Load Balancers (ALB) in AWS ECS/Fargate environments.
2. **Unified Architecture**: Streamable HTTP provides a unified endpoint for both GET and POST requests, simplifying connection management compared to the dual-endpoint SSE model.
3. **Resilience**: It handles intermittent connection issues better than traditional SSE, facilitating the "Active-Active" multi-region requirement.
4. **Future-Proofing**: Aligning with the latest 2025 MCP specification avoids technical debt related to deprecated SSE standards.

## Consequences
- The client-side (Nexus AI or MCP Inspector) must connect to the `/mcp` unified endpoint.
- Traditional SSE-only clients may need a shim or upgrade to interact with this server.
- Observability and Tracing are simplified as all traffic flows through a single logical channel.

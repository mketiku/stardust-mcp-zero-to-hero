# Stardust AI-Native Hub (MCP) - Zero to Hero

This repository contains a high-fidelity **Model Context Protocol (MCP)** server built with Kotlin and Spring AI. It demonstrates how to evolve a gateway from a passive payload proxy to an active operations hub for AI agents.

## Quick Start

### Prerequisites
- Java 21+
- Node.js (for MCP Inspector)

### Running the Server
Use the Gradle Wrapper to start the Spring Boot application:

```bash
./gradlew bootRun
```

The server will be available at `http://localhost:8080`.

### Verifying with MCP Inspector
Open a new terminal and run:

```bash
npx @modelcontextprotocol/inspector
```

- **Transport**: Select **Streamable HTTP**
- **URL**: `http://localhost:8080/mcp`
- **Click Connect**

## Documentation
- [Architecture Decision Records (ADRs)](file:///Users/mketiku/Projects/mcp_zero_to_hero/doc/adr/001-streamable-http-transport.md)
- [Project Mission (context.md)](file:///Users/mketiku/Projects/mcp_zero_to_hero/context.md)

## Architecture FAQ

### Should we use Hexagonal Architecture?
**Absolutely.** Hexagonal (also known as Ports and Adapters) is the ideal architectural pattern for MCP servers because:

1. **Isolation of Protocol**: The MCP Transport (Streamable HTTP) is just one "Driving Adapter." You could swap it for STDIO or WebSocket without touching your core logic.
2. **Domain-Centric Tools**: Your `@McpTool` methods represent the core domain logic (the "Inside").
3. **Mockable Downstream**: Downstream systems (DOCKING, CHRONOS, PEOPLE) are "Driven Adapters." By defining them as interfaces, you can mock "Station-like" data for development (as we did in `MockBrokers.kt`) and swap in real mTLS-secured clients for production.

---
*Created for the Stardust MCP Zero-to-Hero Workshop.*

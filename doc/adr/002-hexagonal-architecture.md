# ADR 002: Using Hexagonal Architecture for MCP Servers

## Status
Accepted

## Context
Developing Model Context Protocol (MCP) servers involves orchestrating multiple station systems (DOCKING, CHRONOS, PEOPLE) and exposing specific logic as a gateway for an AI Assistant (Nexus AI). Traditional layered architectures often blur the lines between protocol management (MCP) and domain orchestration.

## Decision
We will follow **Hexagonal Architecture** (Ports and Adapters) for the internal structure of this and future MCP projects.

## Rationale
1. **Clean Orchestration**: The core "StardustOperationsServer" should not care whether the downstream data comes from a mock REST API or a real interplanetary gRPC service.
2. **AI Protocol Agnostic**: By isolating the "Driving Port" (MCP over HTTP), we can easily support future protocols (e.g., direct STDIO for desktop apps) without refactoring the tool logic.
3. **Enhanced Testing**: Hexagonal architecture makes it easier to write unit tests for the "Parallel Orchestration" logic (`get_personnel_deployment`) by injecting mock adapters for docking bay and personnel lookups.
4. **Resilience**: We can apply circuit breakers (Resilience4j) at the Adapter level, keeping the Core Domain logic simple and focused on AI-readable responses.

## Consequences
- The project structure will maintain a clear separation between:
  - **Adapters**: `MockBrokers.kt` (Downstream), `McpApplication.kt` (Entry points).
  - **Domain Core**: `StardustOperationsServer.kt` (Tool orchestration).
- Future additions of real backend services will be done by implementing new Adapter classes rather than modifying existing Domain logic.

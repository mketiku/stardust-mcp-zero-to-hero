# ADR 003: Resource Implementation Strategy (Calendar & Static Context)

## Status
Accepted

## Context
As the Stardust Station transitions to an AI-Native operational model, we need a way to provide Large Language Models (LLMs) with "slowly changing context"—data that is useful for reasoning but doesn't necessarily require an executable "Tool" call. Examples include station holiday calendars, deck maps, and safety protocols.

## Decision
We decided to use the **MCP Resource** abstraction (`@McpResource`) to expose the **Chronos Holiday Calendar** via the URI `stardust://chronos/holidays`.

## Rationale
1. **Semantic Distinction**: Unlike Tools (which imply *action* or *state change*), Resources represent **declarative state**. This helps the AI model understand that reading this data is "safe" and doesn't trigger side effects.
2. **AI Discovery**: By exposing the calendar as a named resource with a description ("Read-only access to galactic holiday cycles"), the AI can autonomously decide to "look up" the context whenever a timing conflict occurs, without being explicitly told to run a tool.
3. **Efficiency**: Resources are often cached by AI clients, reducing the number of redundant network calls compared to tool executions.
4. **Standardization**: Using the `stardust://` URI scheme establishes a consistent namespace for all future station data, making it easier to scale the hub across different star systems (e.g., Zenith vs. Horizon).

## Consequences
- AI Agents (like Nexus AI) can now "read" the station's calendar to resolve scheduling conflicts.
- Future static data (e.g., "Deck Alpha Blueprint") should follow this pattern instead of being turned into a `getBlueprint` tool.
- Developers must ensure that Resources remain **read-only** to maintain the semantic integrity of the protocol.

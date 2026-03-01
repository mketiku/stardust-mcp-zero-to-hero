package com.stardust.mcp

/** Superclass for all stardust-station operations errors. */
sealed class StationException(
    message: String,
) : RuntimeException(message)

/**
 * Thrown when an operation conflicts with the current station state (e.g., bay already occupied).
 */
class StationConflictException(
    val bayId: String,
    val currentOccupant: String? = null,
    message: String = "CONFLICT: Bay $bayId is already occupied.",
) : StationException(message)

/** Thrown when a resource (Personnel, Bay, or Shift) is not found in galactic records. */
class StationResourceNotFoundException(
    val resourceType: String,
    val resourceId: String,
    message: String = "$resourceType $resourceId not found.",
) : StationException(message)

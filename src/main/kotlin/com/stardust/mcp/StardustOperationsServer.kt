package com.stardust.mcp

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springaicommunity.mcp.annotation.McpResource
import org.springaicommunity.mcp.annotation.McpTool
import org.springaicommunity.mcp.annotation.McpToolParam
import org.springframework.stereotype.Service

@Service
class StardustOperationsServer(
    private val mockSystem: MockSystemService,
) {
    @McpResource(
        uri = "stardust://chronos/holidays",
        name = "Station Holiday Calendar",
        description = "Read-only access to the galactic holiday cycles.",
    )
    fun getHolidayCalendar(): String = mockSystem.getHolidays()

    @McpTool(
        description = "Search for station personnel by name or PID.",
    )
    fun searchPersonnel(
        @McpToolParam(
            description = "Name or PID. Example: 'Cmdr. Orion' or 'PID-101'",
            required = true,
        )
        query: String,
    ): List<Personnel> = mockSystem.searchPersonnel(query)

    @McpTool(description = "Get status of docking bays, optionally filtered by deck.")
    fun getBayStatus(
        @McpToolParam(
            description = "Numeric deck identifier. Example: '10' or '15'",
            required = false,
        )
        deck: String? = null,
    ): List<BayInfo> = mockSystem.getBays(deck)

    @McpTool(
        description = "Assign a docking bay to a personnel (PID).",
    )
    fun assignBay(
        @McpToolParam(
            description = "The specific bay ID. Example: 'BAY-ALPHA-1'",
            required = true,
        )
        bayId: String,
        @McpToolParam(description = "Personnel ID. Example: 'PID-202'", required = true)
        pid: String,
    ): String = mockSystem.assignBay(bayId, pid)

    @McpTool(description = "Record a duty shift/rotation for personnel.")
    fun recordDutyShift(
        @McpToolParam(description = "Personnel ID. Example: 'PID-101'", required = true)
        pid: String,
        @McpToolParam(
            description = "Start cycle in SOL format. Example: 'SOL-2026.04.12'",
            required = true,
        )
        startCycle: String,
        @McpToolParam(
            description = "End cycle in SOL format. Example: 'SOL-2026.04.13'",
            required = true,
        )
        endCycle: String,
        @McpToolParam(
            description = "Shift category. Example: 'Maintenance' or 'Command'",
            required = true,
        )
        type: String,
    ): String = mockSystem.createShift(pid, startCycle, endCycle, type)

    @McpTool(
        description =
            "Orchestrated tool: Find personnel and their assigned docking bay details in parallel.",
    )
    suspend fun getPersonnelDeployment(
        @McpToolParam(description = "Personnel ID. Example: 'PID-101'", required = true)
        pid: String,
    ): String =
        coroutineScope {
            val personnelJob = async { mockSystem.searchPersonnel(pid).firstOrNull() }
            val allBaysJob = async { mockSystem.getBays() }

            val officer =
                personnelJob.await()
                    ?: return@coroutineScope "Personnel $pid not found in galactic records."
            val bay = allBaysJob.await().find { it.bayId == officer.bayLocation }

            if (bay == null) {
                "Officer ${officer.name} (${officer.pid}) is assigned to bay ${officer.bayLocation}, but bay specs are missing."
            } else {
                "Officer ${officer.name} (${officer.pid}) is deployed to ${bay.bayId} (Deck ${bay.deck}, ${bay.station}). Status: ${if (bay.isOccupied) "Occupied" else "Vacant"}."
            }
        }
}

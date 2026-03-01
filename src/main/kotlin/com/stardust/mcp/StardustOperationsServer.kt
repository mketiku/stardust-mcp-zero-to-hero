package com.stardust.mcp

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springaicommunity.mcp.annotation.McpTool
import org.springframework.stereotype.Service

@Service
class StardustOperationsServer(private val mockSystem: MockSystemService) {

    @McpTool(
            description =
                    "Search for station personnel by name or PID. Returns Rank and Bay allocation."
    )
    fun searchPersonnel(query: String): List<Personnel> {
        return mockSystem.searchPersonnel(query)
    }

    @McpTool(description = "Get status of docking bays, optionally filtered by deck.")
    fun getBayStatus(deck: String? = null): List<BayInfo> {
        return mockSystem.getBays(deck)
    }

    @McpTool(
            description =
                    "Assign a docking bay to a personnel (PID). Handles bay occupancy conflicts."
    )
    fun assignBay(bayId: String, pid: String): String {
        return mockSystem.assignBay(bayId, pid)
    }

    @McpTool(description = "Record a duty shift/rotation for personnel.")
    fun recordDutyShift(pid: String, startCycle: String, endCycle: String, type: String): String {
        return mockSystem.createShift(pid, startCycle, endCycle, type)
    }

    @McpTool(
            description =
                    "Orchestrated tool: Find personnel and their assigned docking bay details in parallel."
    )
    suspend fun getPersonnelDeployment(pid: String): String = coroutineScope {
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

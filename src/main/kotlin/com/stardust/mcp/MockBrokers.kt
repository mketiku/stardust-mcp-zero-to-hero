package com.stardust.mcp

import kotlinx.serialization.Serializable
import org.springframework.stereotype.Service
import java.util.UUID

@Serializable
data class BayInfo(
    val bayId: String,
    val deck: String,
    val station: String,
    val isOccupied: Boolean,
)

@Serializable
data class DutyShift(
    val id: String,
    val pid: String,
    val startCycle: String,
    val endCycle: String,
    val type: String,
)

@Serializable
data class Personnel(
    val pid: String,
    val name: String,
    val rank: String,
    val bayLocation: String,
)

@Service
class MockSystemService {
    private val bays =
        mutableListOf(
            BayInfo("BAY-ALPHA-1", "10", "Zenith Station", false),
            BayInfo("BAY-ALPHA-2", "10", "Zenith Station", true),
            BayInfo("BAY-BETA-2", "15", "Horizon Outpost", false),
        )

    private val shifts = mutableListOf<DutyShift>()

    private val personnelSet =
        listOf(
            Personnel("PID-101", "Cmdr. Orion", "Commander", "BAY-ALPHA-1"),
            Personnel("PID-202", "Lt. Lyra", "Tactical Officer", "BAY-BETA-2"),
        )

    private val holidays =
        listOf(
            "SOL-2026.04.12: Galactic Liberation Day",
            "SOL-2026.10.31: Void harvest Festival",
            "SOL-2026.12.25: Starlight Solstice",
        )

    fun getBays(deck: String? = null) = if (deck != null) bays.filter { it.deck == deck } else bays

    fun getHolidays() = holidays.joinToString("\n")

    fun assignBay(
        bayId: String,
        pid: String,
    ): String {
        val bay =
            bays.find { it.bayId == bayId }
                ?: throw StationResourceNotFoundException("Bay", bayId)
        if (bay.isOccupied) throw StationConflictException(bayId)

        val index = bays.indexOf(bay)
        bays[index] = bay.copy(isOccupied = true)
        return "SUCCESS: Bay $bayId assigned to $pid"
    }

    fun getShifts(pid: String) = shifts.filter { it.pid == pid }

    fun createShift(
        pid: String,
        start: String,
        end: String,
        type: String,
    ): String {
        // If the personnel doesn't exist, throw ResourceNotFound
        if (personnelSet.none { it.pid == pid }) {
            throw StationResourceNotFoundException("Personnel", pid)
        }

        val id = UUID.randomUUID().toString().substring(0, 8)
        shifts.add(DutyShift(id, pid, start, end, type))
        return "SUCCESS: Duty shift created ($id)"
    }

    fun searchPersonnel(query: String) =
        personnelSet.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.pid == query
        }
}

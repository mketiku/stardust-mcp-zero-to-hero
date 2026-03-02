package com.stardust.mcp

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StardustOperationsServerTest {
    private val mockSystem = mockk<MockSystemService>()
    private val server = StardustOperationsServer(mockSystem)

    @Test
    fun `assignBay throws StationConflictException when bay is occupied`() {
        // Given
        val bayId = "BAY-OCCUPIED"
        val pid = "PID-101"
        every { mockSystem.assignBay(bayId, pid) } throws StationConflictException(bayId)

        // When/Then
        assertFailsWith<StationConflictException> { server.assignBay(bayId, pid) }
    }

    @Test
    fun `assignBay throws StationResourceNotFoundException when bay missing`() {
        // Given
        val bayId = "BAY-MISSING"
        val pid = "PID-101"
        every { mockSystem.assignBay(bayId, pid) } throws
            StationResourceNotFoundException("Bay", bayId)

        // When/Then
        assertFailsWith<StationResourceNotFoundException> { server.assignBay(bayId, pid) }
    }

    @Test
    fun `getPersonnelDeployment returns success for found officer and bay`() =
        runTest {
            // Given
            val pid = "PID-TEST"
            val officer = Personnel(pid, "Test Officer", "Commander", "BAY-T1")
            val bay = BayInfo("BAY-T1", "1", "Testing", false)

            every { mockSystem.searchPersonnel(pid) } returns listOf(officer)
            every { mockSystem.getBays() } returns listOf(bay)

            // When
            val result = server.getPersonnelDeployment(pid)

            // Then
            assertTrue(result.contains("Officer Test Officer"))
            assertTrue(result.contains("BAY-T1"))
            assertTrue(result.contains("Vacant"))
        }

    @Test
    fun `getPersonnelDeployment returns message for missing officer`() =
        runTest {
            // Given
            val pid = "PID-MISSING"
            every { mockSystem.searchPersonnel(pid) } returns emptyList()
            every { mockSystem.getBays() } returns emptyList() // parallel call

            // When
            val result = server.getPersonnelDeployment(pid)

            // Then
            assertEquals("Personnel PID-MISSING not found in galactic records.", result)
        }

    @Test
    fun `getPersonnelDeployment returns message for missing bay specs`() =
        runTest {
            // Given
            val pid = "PID-TEST"
            val officer = Personnel(pid, "Test Officer", "Ensign", "BAY-LOST")

            every { mockSystem.searchPersonnel(pid) } returns listOf(officer)
            every { mockSystem.getBays() } returns emptyList()

            // When
            val result = server.getPersonnelDeployment(pid)

            // Then
            assertTrue(
                result.contains(
                    "Officer Test Officer (PID-TEST) is assigned to bay BAY-LOST, but bay specs are missing.",
                ),
            )
        }
}

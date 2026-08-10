package com.enriqueajin.pomidorki.designsystem.components.timerprogressindicator

import org.junit.Assert.assertEquals
import org.junit.Test

class TimerProgressIndicatorProgressTest {
    @Test
    fun `GIVEN half remaining WHEN progressRatio THEN returns half progress`() {
        val totalMillis = 2 * 60_000L
        val remainingMillis = totalMillis / 2

        assertEquals(0.5f, progressRatio(remainingMillis, totalMillis), FLOAT_DELTA)
    }

    @Test
    fun `GIVEN full remaining WHEN progressRatio THEN returns full progress`() {
        val totalMillis = 2 * 60_000L
        val remainingMillis = totalMillis

        assertEquals(1f, progressRatio(remainingMillis, totalMillis), FLOAT_DELTA)
    }

    @Test
    fun `GIVEN zero remaining WHEN progressRatio THEN returns empty progress`() {
        val totalMillis = 2 * 60_000L
        val remainingMillis = 0L

        assertEquals(0f, progressRatio(remainingMillis, totalMillis), FLOAT_DELTA)
    }

    @Test
    fun `GIVEN zero total WHEN progressRatio THEN returns empty progress`() {
        val totalMillis = 0L
        val remainingMillis = 60_000L

        assertEquals(0f, progressRatio(remainingMillis, totalMillis), FLOAT_DELTA)
    }

    @Test
    fun `GIVEN negative total WHEN progressRatio THEN returns empty progress`() {
        val totalMillis = -100L
        val remainingMillis = 60_000L

        assertEquals(0f, progressRatio(remainingMillis, totalMillis), FLOAT_DELTA)
    }

    @Test
    fun `GIVEN remaining above total WHEN progressRatio THEN clamps to full progress`() {
        val totalMillis = 2 * 60_000L
        val remainingMillis = totalMillis + 60_000L

        assertEquals(1f, progressRatio(remainingMillis, totalMillis), FLOAT_DELTA)
    }

    @Test
    fun `GIVEN negative remaining WHEN progressRatio THEN clamps to empty progress`() {
        val totalMillis = 2 * 60_000L
        val remainingMillis = -10_000L

        assertEquals(0f, progressRatio(remainingMillis, totalMillis), FLOAT_DELTA)
    }

    private companion object {
        const val FLOAT_DELTA = 0.0001f
    }
}

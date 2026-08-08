package com.enriqueajin.pomidorki.utils

import com.enriqueajin.pomidorki.utils.TimeFormatter.formatCountdownTime
import com.enriqueajin.pomidorki.utils.TimeFormatter.formatTime
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatterTest {
    @Test
    fun `GIVEN zero millis WHEN formatTime THEN returns 00_00`() {
        assertEquals("00:00", 0L.formatTime())
    }

    @Test
    fun `GIVEN zero millis WHEN formatCountdownTime THEN returns 00_00`() {
        assertEquals("00:00", 0L.formatCountdownTime())
    }

    @Test
    fun `GIVEN 59999 millis WHEN formatCountdownTime THEN returns 01_00`() {
        assertEquals("01:00", 59_999L.formatCountdownTime())
    }

    @Test
    fun `GIVEN 59999 millis WHEN formatTime THEN returns 00_59`() {
        assertEquals("00:59", 59_999L.formatTime())
    }

    @Test
    fun `GIVEN 60000 millis WHEN formatTime THEN returns 01_00`() {
        assertEquals("01:00", 60_000L.formatTime())
    }

    @Test
    fun `GIVEN 60000 millis WHEN formatCountdownTime THEN returns 01_00`() {
        assertEquals("01:00", 60_000L.formatCountdownTime())
    }

    @Test
    fun `GIVEN 90000 millis WHEN formatTime THEN returns 01_30`() {
        assertEquals("01:30", 90_000L.formatTime())
    }
}

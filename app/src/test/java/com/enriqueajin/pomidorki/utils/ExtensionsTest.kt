package com.enriqueajin.pomidorki.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsTest {
    @Test
    fun `GIVEN zero minutes WHEN toMillis THEN returns zero millis`() {
        assertEquals(0L, 0.toMillis())
    }

    @Test
    fun `GIVEN one minute WHEN toMillis THEN returns 60000 millis`() {
        assertEquals(60_000L, 1.toMillis())
    }

    @Test
    fun `GIVEN twenty five minutes WHEN toMillis THEN returns 1500000 millis`() {
        assertEquals(1_500_000L, 25.toMillis())
    }

    @Test
    fun `GIVEN zero millis WHEN toMinutes THEN returns zero minutes`() {
        assertEquals(0L, 0L.toMinutes())
    }

    @Test
    fun `GIVEN 59999 millis WHEN toMinutes THEN returns zero minutes`() {
        assertEquals(0L, 59_999L.toMinutes())
    }

    @Test
    fun `GIVEN 60000 millis WHEN toMinutes THEN returns one minute`() {
        assertEquals(1L, 60_000L.toMinutes())
    }

    @Test
    fun `GIVEN 1500000 millis WHEN toMinutes THEN returns twenty five minutes`() {
        assertEquals(25L, 1_500_000L.toMinutes())
    }
}

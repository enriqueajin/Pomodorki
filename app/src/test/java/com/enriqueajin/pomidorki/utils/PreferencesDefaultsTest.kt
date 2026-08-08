package com.enriqueajin.pomidorki.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class PreferencesDefaultsTest {
    @Test
    fun `GIVEN pomodoro duration key WHEN getDefaultPreference THEN returns 25`() {
        assertEquals("25", getDefaultPreference(PreferencesKeys.POMODORO_DURATION))
    }

    @Test
    fun `GIVEN short break duration key WHEN getDefaultPreference THEN returns 5`() {
        assertEquals("5", getDefaultPreference(PreferencesKeys.SHORT_BREAK_DURATION))
    }

    @Test
    fun `GIVEN long break duration key WHEN getDefaultPreference THEN returns 15`() {
        assertEquals("15", getDefaultPreference(PreferencesKeys.LONG_BREAK_DURATION))
    }

    @Test
    fun `GIVEN unknown key WHEN getDefaultPreference THEN returns pomodoro default`() {
        assertEquals("25", getDefaultPreference("unknown_key"))
    }
}

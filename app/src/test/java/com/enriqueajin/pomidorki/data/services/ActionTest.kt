package com.enriqueajin.pomidorki.data.services

import org.junit.Assert.assertEquals
import org.junit.Test

class ActionTest {
    @Test
    fun `GIVEN START WHEN toAction THEN returns START`() {
        assertEquals(Action.START, "START".toAction())
    }

    @Test
    fun `GIVEN STOP WHEN toAction THEN returns STOP`() {
        assertEquals(Action.STOP, "STOP".toAction())
    }

    @Test
    fun `GIVEN PAUSE WHEN toAction THEN returns PAUSE`() {
        assertEquals(Action.PAUSE, "PAUSE".toAction())
    }

    @Test
    fun `GIVEN CANCEL WHEN toAction THEN returns CANCEL`() {
        assertEquals(Action.CANCEL, "CANCEL".toAction())
    }

    @Test
    fun `GIVEN NONE WHEN toAction THEN returns NONE`() {
        assertEquals(Action.NONE, "NONE".toAction())
    }

    @Test
    fun `GIVEN unknown string WHEN toAction THEN returns NONE`() {
        assertEquals(Action.NONE, "bogus".toAction())
    }
}

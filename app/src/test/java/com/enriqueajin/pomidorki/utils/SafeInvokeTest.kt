package com.enriqueajin.pomidorki.utils

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SafeInvokeTest {
    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `GIVEN successful lambda WHEN safeInvoke THEN returns value`() {
        assertEquals("ok", safeInvoke { "ok" })
    }

    @Test
    fun `GIVEN IOException WHEN safeInvoke THEN returns null`() {
        assertNull(
            safeInvoke<String> {
                throw IOException("disk full")
            },
        )
    }
}

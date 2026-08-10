package com.enriqueajin.pomidorki.data.countdown

import android.content.Context
import com.enriqueajin.pomidorki.domain.model.TimerSessionSnapshot
import com.enriqueajin.pomidorki.fake.FakeUserSettingsRepository
import com.enriqueajin.pomidorki.utils.PreferencesKeys.LONG_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.POMODORO_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SHORT_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SessionState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class CountDownPomodoroTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeUserSettingsRepository
    private var clockNow = 0L
    private lateinit var countdown: CountDownPomodoro

    @Before
    fun setUp() {
        mockkObject(ServiceHelper)
        every { ServiceHelper.triggerForegroundService(any(), any()) } returns Unit
        every { ServiceHelper.triggerForegroundService(any(), any(), any()) } returns Unit

        repository = FakeUserSettingsRepository.withDefaults()
        clockNow = 0L
        val clock = ElapsedRealtimeClock { clockNow }
        val context = mockk<Context>(relaxed = true)
        countdown =
            CountDownPomodoro(
                context = context,
                userSettingsRepository = repository,
                elapsedRealtimeClock = clock,
                dispatcher = testDispatcher,
            )
    }

    @After
    fun tearDown() {
        unmockkObject(ServiceHelper)
    }

    @Test
    fun `GIVEN idle session WHEN restoreSession THEN Idle`() =
        runTest(testDispatcher) {
            advanceUntilIdle()

            val result = countdown.restoreSession()

            assertEquals(SessionRestoreResult.Idle, result)
        }

    @Test
    fun `GIVEN paused session WHEN restoreSession THEN Paused with saved timer state`() =
        runTest(testDispatcher) {
            advanceUntilIdle()
            repository.saveTimerSession(
                TimerSessionSnapshot(
                    state = SessionState.PAUSED,
                    deadlineElapsed = 0L,
                    timeLeft = minutes(2),
                    initialMillis = minutes(25),
                    selectedTimer = 1,
                ),
            )

            val result = countdown.restoreSession()

            assertEquals(SessionRestoreResult.Paused, result)
            assertEquals(1, countdown.selectedTimer.value)
            assertEquals(minutes(25), countdown.initialMillis.value)
            assertEquals(minutes(2), countdown.timeLeft.value)
            assertEquals(0L, countdown.deadlineElapsed.value)
        }

    @Test
    fun `GIVEN started session with time remaining WHEN restoreSession THEN Started and ticking`() =
        runTest(testDispatcher) {
            advanceUntilIdle()
            clockNow = 1_000L
            repository.saveTimerSession(
                TimerSessionSnapshot(
                    state = SessionState.STARTED,
                    deadlineElapsed = 61_000L,
                    timeLeft = 0L,
                    initialMillis = minutes(25),
                    selectedTimer = 0,
                ),
            )

            val result = countdown.restoreSession()
            runCurrent()

            assertEquals(SessionRestoreResult.Started, result)
            assertEquals(minutes(1), countdown.timeLeft.value)
            assertEquals(61_000L, countdown.deadlineElapsed.value)
            val session = repository.getTimerSession()
            assertEquals(SessionState.STARTED, session.state)
            assertEquals(61_000L, session.deadlineElapsed)
        }

    @Test
    fun `GIVEN running timer completed while away WHEN restoreSession THEN CompletedWhileAway and session cleared`() =
        runTest(testDispatcher) {
            advanceUntilIdle()
            clockNow = 100_000L
            repository.saveTimerSession(
                TimerSessionSnapshot(
                    state = SessionState.STARTED,
                    deadlineElapsed = 50_000L,
                    timeLeft = 0L,
                    initialMillis = minutes(15),
                    selectedTimer = 2,
                ),
            )

            val result = countdown.restoreSession()
            advanceUntilIdle()

            assertEquals(SessionRestoreResult.CompletedWhileAway, result)
            assertEquals(2, countdown.selectedTimer.value)
            assertEquals(minutes(15), countdown.timeLeft.value)
            val session = repository.getTimerSession()
            assertEquals(SessionState.IDLE, session.state)
        }

    @Test
    fun `GIVEN custom duration prefs WHEN tabs selected THEN pomodoro short and long durations applied`() =
        runTest(testDispatcher) {
            repository.putString(POMODORO_DURATION, "30")
            repository.putString(SHORT_BREAK_DURATION, "10")
            repository.putString(LONG_BREAK_DURATION, "20")
            advanceUntilIdle()

            assertEquals(minutes(30), countdown.timeLeft.value)

            countdown.updateSelectedTimer(1)
            advanceUntilIdle()
            assertEquals(minutes(10), countdown.timeLeft.value)

            countdown.updateSelectedTimer(2)
            advanceUntilIdle()
            assertEquals(minutes(20), countdown.timeLeft.value)
        }

    @Test
    fun `GIVEN running timer WHEN pause THEN timeLeft persisted as paused session`() =
        runTest(testDispatcher) {
            advanceUntilIdle()
            countdown.start()
            runCurrent()
            clockNow = minutes(5)

            countdown.pause()
            runCurrent()

            assertEquals(minutes(20), countdown.timeLeft.value)
            assertEquals(0L, countdown.deadlineElapsed.value)
            val session = repository.getTimerSession()
            assertEquals(SessionState.PAUSED, session.state)
            assertEquals(minutes(20), session.timeLeft)
        }

    @Test
    fun `GIVEN active session WHEN reset THEN session cleared and full duration restored`() =
        runTest(testDispatcher) {
            advanceUntilIdle()
            countdown.start()
            runCurrent()

            countdown.reset()
            advanceUntilIdle()

            assertEquals(minutes(25), countdown.timeLeft.value)
            assertEquals(0L, countdown.deadlineElapsed.value)
            val session = repository.getTimerSession()
            assertEquals(SessionState.IDLE, session.state)
        }

    @Test
    fun `GIVEN idle timer WHEN updateSelectedTimer THEN duration switches and session cleared`() =
        runTest(testDispatcher) {
            advanceUntilIdle()
            countdown.start()
            runCurrent()

            countdown.updateSelectedTimer(1)
            advanceUntilIdle()

            assertEquals(1, countdown.selectedTimer.value)
            assertEquals(minutes(5), countdown.timeLeft.value)
            val session = repository.getTimerSession()
            assertEquals(SessionState.IDLE, session.state)
        }

    private fun minutes(value: Long): Long = TimeUnit.MINUTES.toMillis(value)
}

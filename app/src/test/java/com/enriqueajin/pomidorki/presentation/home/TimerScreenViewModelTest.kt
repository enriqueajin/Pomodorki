package com.enriqueajin.pomidorki.presentation.home

import androidx.datastore.preferences.core.intPreferencesKey
import app.cash.turbine.test
import com.enriqueajin.pomidorki.data.model.PomodoroServiceData
import com.enriqueajin.pomidorki.data.services.CountdownService
import com.enriqueajin.pomidorki.data.services.CountdownState
import com.enriqueajin.pomidorki.fake.FakeUserSettingsRepository
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.Effect
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.Event
import com.enriqueajin.pomidorki.testutil.MainDispatcherRule
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_CLOSE
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_RESET
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SELECTED_TIMER
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerScreenViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeUserSettingsRepository
    private lateinit var viewModel: TimerScreenViewModel
    private lateinit var service: CountdownService
    private lateinit var serviceDataFlow: MutableStateFlow<PomodoroServiceData>

    @Before
    fun setUp() {
        repository = FakeUserSettingsRepository()
        viewModel = TimerScreenViewModel(repository, TimerUiMapper())
        serviceDataFlow = MutableStateFlow(PomodoroServiceData())
        service =
            mockk {
                every { serviceData } returns serviceDataFlow
            }
        viewModel.onServiceConnected(service)
    }

    @Test
    fun `GIVEN idle timer WHEN OnTabClicked new index THEN selectedTimer updated and TriggerIntent emitted`() =
        runTest {
            viewModel.uiEffects.test {
                viewModel.onEvent(Event.OnTabClicked(1))
                advanceUntilIdle()

                assertEquals(1, viewModel.uiState.value.selectedTimer)
                assertEquals(Effect.TriggerIntent(1), awaitItem())
                assertEquals(
                    1,
                    repository.userSettingsFlow.first()[intPreferencesKey(SELECTED_TIMER)],
                )
            }
        }

    @Test
    fun `GIVEN running timer WHEN OnTabClicked new index THEN dialog opens and TimerToBeConfirmed emitted`() =
        runTest {
            serviceDataFlow.value =
                PomodoroServiceData(
                    currentState = CountdownState.Started,
                    selectedTimer = 0,
                )
            advanceUntilIdle()

            viewModel.uiEffects.test {
                viewModel.onEvent(Event.OnTabClicked(2))
                advanceUntilIdle()

                assertTrue(viewModel.uiState.value.isDialogOpen)
                assertEquals(0, viewModel.uiState.value.selectedTimer)
                assertEquals(Effect.TimerToBeConfirmed(2), awaitItem())
            }
        }

    @Test
    fun `GIVEN running timer and dialog WHEN OnAlertConfirmClick THEN state updated and TriggerForegroundService emitted`() =
        runTest {
            serviceDataFlow.value =
                PomodoroServiceData(
                    currentState = CountdownState.Started,
                    selectedTimer = 0,
                )
            advanceUntilIdle()
            viewModel.onEvent(Event.OnTabClicked(2))
            advanceUntilIdle()

            viewModel.uiEffects.test {
                // Drain TimerToBeConfirmed from opening the dialog
                awaitItem()

                viewModel.onEvent(Event.OnAlertConfirmClick(2))
                advanceUntilIdle()

                assertEquals(2, viewModel.uiState.value.selectedTimer)
                assertFalse(viewModel.uiState.value.isDialogOpen)
                assertEquals(
                    Effect.TriggerForegroundService(
                        action = ACTION_SERVICE_CLOSE,
                        timerType = 2,
                    ),
                    awaitItem(),
                )
                assertEquals(
                    2,
                    repository.userSettingsFlow.first()[intPreferencesKey(SELECTED_TIMER)],
                )
            }
        }

    @Test
    fun `GIVEN service connected WHEN PomodoroServiceData emitted THEN timerText formatted`() =
        runTest {
            advanceUntilIdle()

            viewModel.uiState.test {
                skipItems(1) // initial / first service emission

                serviceDataFlow.value =
                    PomodoroServiceData(
                        currentState = CountdownState.Started,
                        timeLeft = 90_000L,
                        initialMillis = 90_000L,
                        selectedTimer = 0,
                    )
                advanceUntilIdle()

                val state = awaitItem()
                assertEquals("01:30", state.timerText)
                assertEquals(90_000L, state.timeLeft)
                assertEquals(CountdownState.Started, state.currentState)
            }
        }

    @Test
    fun `GIVEN dialog open WHEN OnAlertCancelClick THEN dialog closes and no effect emitted`() =
        runTest {
            serviceDataFlow.value =
                PomodoroServiceData(
                    currentState = CountdownState.Started,
                    selectedTimer = 0,
                )
            advanceUntilIdle()
            viewModel.onEvent(Event.OnTabClicked(2))
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.isDialogOpen)

            viewModel.uiEffects.test {
                // Drain TimerToBeConfirmed from opening the dialog
                awaitItem()

                viewModel.onEvent(Event.OnAlertCancelClick)
                advanceUntilIdle()

                assertFalse(viewModel.uiState.value.isDialogOpen)
                expectNoEvents()
            }
        }

    @Test
    fun `WHEN OnRestartIconClick THEN dialog opens and TimerToBeConfirmed emitted`() =
        runTest {
            viewModel.uiEffects.test {
                viewModel.onEvent(Event.OnRestartIconClick)
                advanceUntilIdle()

                assertTrue(viewModel.uiState.value.isDialogOpen)
                assertEquals(Effect.TimerToBeConfirmed(0), awaitItem())
            }
        }

    @Test
    fun `GIVEN idle timer and dialog WHEN OnAlertConfirmClick THEN TriggerForegroundService with RESET`() =
        runTest {
            viewModel.uiEffects.test {
                viewModel.onEvent(Event.OnRestartIconClick)
                awaitItem() // TimerToBeConfirmed

                viewModel.onEvent(Event.OnAlertConfirmClick(1))
                advanceUntilIdle()

                assertEquals(1, viewModel.uiState.value.selectedTimer)
                assertFalse(viewModel.uiState.value.isDialogOpen)
                assertEquals(
                    Effect.TriggerForegroundService(
                        action = ACTION_SERVICE_RESET,
                        timerType = 1,
                    ),
                    awaitItem(),
                )
                assertEquals(
                    1,
                    repository.userSettingsFlow.first()[intPreferencesKey(SELECTED_TIMER)],
                )
            }
        }

    @Test
    fun `GIVEN selected tab WHEN OnTabClicked same index THEN no state change and no effect`() =
        runTest {
            viewModel.uiEffects.test {
                viewModel.onEvent(Event.OnTabClicked(0))
                advanceUntilIdle()

                assertEquals(0, viewModel.uiState.value.selectedTimer)
                expectNoEvents()
                assertNull(repository.userSettingsFlow.first()[intPreferencesKey(SELECTED_TIMER)])
            }
        }

    @Test
    fun `GIVEN service disconnected WHEN PomodoroServiceData emitted THEN uiState unchanged`() =
        runTest {
            advanceUntilIdle()
            val before = viewModel.uiState.value

            viewModel.onServiceDisconnected()
            serviceDataFlow.value =
                PomodoroServiceData(
                    currentState = CountdownState.Started,
                    timeLeft = 90_000L,
                    initialMillis = 90_000L,
                    selectedTimer = 1,
                )
            advanceUntilIdle()

            assertEquals(before, viewModel.uiState.value)
        }
}

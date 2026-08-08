package com.enriqueajin.pomidorki.presentation

import app.cash.turbine.test
import com.enriqueajin.pomidorki.fake.FakeUserSettingsRepository
import com.enriqueajin.pomidorki.testutil.MainDispatcherRule
import com.enriqueajin.pomidorki.utils.PreferencesKeys.LONG_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.POMODORO_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SHORT_BREAK_DURATION
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserSettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `GIVEN empty preferences THEN state with default values`() =
        runTest {
            val fake = FakeUserSettingsRepository()
            val viewModel = UserSettingsViewModel(fake)

            viewModel.userSettingsState.test {
                assertNull(awaitItem())
                advanceUntilIdle()
                assertEquals(UserSettingsState(25, 5, 15), awaitItem())
            }
        }

    @Test
    fun `GIVEN preference durations THEN state contains them`() =
        runTest {
            val fake =
                FakeUserSettingsRepository().apply {
                    putString(POMODORO_DURATION, "30")
                    putString(SHORT_BREAK_DURATION, "10")
                    putString(LONG_BREAK_DURATION, "20")
                }
            val viewModel = UserSettingsViewModel(fake)

            viewModel.userSettingsState.test {
                assertNull(awaitItem())
                advanceUntilIdle()
                assertEquals(UserSettingsState(30, 10, 20), awaitItem())
            }
        }

    @Test
    fun `WHEN UpdatePomodoroDuration THEN state contains new duration`() =
        runTest {
            val fake = FakeUserSettingsRepository()
            val viewModel = UserSettingsViewModel(fake)

            viewModel.userSettingsState.test {
                assertNull(awaitItem())
                advanceUntilIdle()
                assertEquals(UserSettingsState(25, 5, 15), awaitItem())

                viewModel.onUserSettingsEvent(UserSettingsEvent.UpdatePomodoroDuration(40))
                advanceUntilIdle()

                assertEquals(UserSettingsState(40, 5, 15), awaitItem())
                assertEquals("40", fake.getSetting(POMODORO_DURATION))
            }
        }
}

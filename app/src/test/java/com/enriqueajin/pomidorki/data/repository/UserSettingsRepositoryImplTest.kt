package com.enriqueajin.pomidorki.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.enriqueajin.pomidorki.domain.model.TimerSessionSnapshot
import com.enriqueajin.pomidorki.utils.PreferencesKeys.POMODORO_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SELECTED_TIMER
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SessionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File
import java.util.UUID

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class UserSettingsRepositoryImplTest {
    private lateinit var dataStoreScope: CoroutineScope
    private lateinit var dataStoreFile: File
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: UserSettingsRepositoryImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dataStoreFile = File(context.filesDir, "test_user_settings_${UUID.randomUUID()}.preferences_pb")
        dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        dataStore =
            PreferenceDataStoreFactory.create(
                scope = dataStoreScope,
                produceFile = { dataStoreFile },
            )
        repository = UserSettingsRepositoryImpl(dataStore)
    }

    @After
    fun tearDown() {
        dataStoreScope.cancel()
        dataStoreFile.delete()
    }

    @Test
    fun `WHEN saveString THEN getSetting returns saved value`() =
        runTest {
            repository.saveString(POMODORO_DURATION, "30")

            assertEquals("30", repository.getSetting(POMODORO_DURATION))
        }

    @Test
    fun `GIVEN missing key WHEN getSetting THEN default preference`() =
        runTest {
            assertEquals("25", repository.getSetting(POMODORO_DURATION))
        }

    @Test
    fun `WHEN saveInt for selected timer THEN getTimerSession reflects it`() =
        runTest {
            repository.saveInt(SELECTED_TIMER, 2)

            assertEquals(2, repository.getTimerSession().selectedTimer)
        }

    @Test
    fun `WHEN saveLong THEN value readable from preferences snapshot`() =
        runTest {
            val key = "test_long_key"
            val value = 12_345L

            repository.saveLong(key, value)

            assertEquals(value, repository.userSettingsFlow.first()[longPreferencesKey(key)])
        }

    @Test
    fun `WHEN saveString THEN userSettingsFlow emits updated preference`() =
        runTest {
            repository.userSettingsFlow.test {
                awaitItem()

                repository.saveString(POMODORO_DURATION, "40")

                assertEquals("40", awaitItem()[stringPreferencesKey(POMODORO_DURATION)])
            }
        }

    @Test
    fun `GIVEN empty Preferences WHEN extension getSetting THEN default`() =
        runTest {
            val value =
                with(repository) {
                    emptyPreferences().getSetting(POMODORO_DURATION)
                }

            assertEquals("25", value)
        }

    @Test
    fun `GIVEN saved string WHEN extension getSetting on snapshot THEN returns value`() =
        runTest {
            repository.saveString(POMODORO_DURATION, "30")
            val preferences = repository.userSettingsFlow.first()

            val value =
                with(repository) {
                    preferences.getSetting(POMODORO_DURATION)
                }

            assertEquals("30", value)
        }

    @Test
    fun `WHEN saveTimerSession THEN getTimerSession returns saved snapshot`() =
        runTest {
            val session =
                TimerSessionSnapshot(
                    state = SessionState.PAUSED,
                    deadlineElapsed = 1_000L,
                    timeLeft = 2_000L,
                    initialMillis = 3_000L,
                    selectedTimer = 1,
                )

            repository.saveTimerSession(session)

            assertEquals(session, repository.getTimerSession())
        }

    @Test
    fun `GIVEN saved session WHEN clearTimerSession THEN session fields reset to idle`() =
        runTest {
            repository.saveTimerSession(
                TimerSessionSnapshot(
                    state = SessionState.STARTED,
                    deadlineElapsed = 1_000L,
                    timeLeft = 2_000L,
                    initialMillis = 3_000L,
                    selectedTimer = 2,
                ),
            )

            repository.clearTimerSession()
            val session = repository.getTimerSession()

            assertEquals(SessionState.IDLE, session.state)
            assertEquals(0L, session.deadlineElapsed)
            assertEquals(0L, session.timeLeft)
            assertEquals(0L, session.initialMillis)
            assertEquals(2, session.selectedTimer)
        }
}

package com.enriqueajin.pomidorki.presentation.base

import app.cash.turbine.test
import com.enriqueajin.pomidorki.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `WHEN ui event THEN state updated and effect emitted`() =
        runTest {
            val viewModel = TestBaseViewModel()

            viewModel.uiEffects.test {
                viewModel.onEvent(TestUiEvent.Increment)
                advanceUntilIdle()

                assertEquals(1, viewModel.uiState.value.count)
                assertEquals(TestEffect.CountChanged(1), awaitItem())
            }
        }

    @Test
    fun `WHEN internal event dispatched THEN state updated`() =
        runTest {
            val viewModel = TestBaseViewModel()

            viewModel.dispatchPublic(TestInternalEvent.SetCount(5))
            advanceUntilIdle()

            assertEquals(5, viewModel.uiState.value.count)
        }

    @Test
    fun `WHEN no-op ui event THEN state unchanged and no effect emitted`() =
        runTest {
            val viewModel = TestBaseViewModel()

            viewModel.uiEffects.test {
                viewModel.onEvent(TestUiEvent.NoOp)
                advanceUntilIdle()

                assertEquals(0, viewModel.uiState.value.count)
                expectNoEvents()
            }
        }
}

private data class TestState(
    val count: Int = 0,
)

private sealed class TestUiEvent {
    data object Increment : TestUiEvent()

    data object NoOp : TestUiEvent()
}

private sealed class TestInternalEvent {
    data class SetCount(
        val count: Int,
    ) : TestInternalEvent()
}

private sealed class TestEffect {
    data class CountChanged(
        val count: Int,
    ) : TestEffect()
}

private class TestBaseViewModel :
    BaseViewModel<TestState, TestUiEvent, TestInternalEvent, TestEffect>(
        TestState(),
    ) {
    fun dispatchPublic(event: TestInternalEvent) = dispatch(event)

    override fun handleUiEvent(event: TestUiEvent) {
        when (event) {
            TestUiEvent.Increment -> {
                setState { copy(count = count + 1) }
                emitEffect(TestEffect.CountChanged(uiState.value.count))
            }
            TestUiEvent.NoOp -> Unit
        }
    }

    override fun handleInternalEvent(event: TestInternalEvent) {
        when (event) {
            is TestInternalEvent.SetCount -> setState { copy(count = event.count) }
        }
    }
}

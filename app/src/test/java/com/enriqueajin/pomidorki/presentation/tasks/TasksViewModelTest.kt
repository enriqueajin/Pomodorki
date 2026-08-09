package com.enriqueajin.pomidorki.presentation.tasks

import com.enriqueajin.pomidorki.domain.model.Status
import com.enriqueajin.pomidorki.domain.model.Task
import com.enriqueajin.pomidorki.presentation.tasks.TasksScreenContract.Event
import com.enriqueajin.pomidorki.presentation.tasks.TasksScreenContract.Grouping
import com.enriqueajin.pomidorki.presentation.tasks.TasksScreenContract.Sorting
import com.enriqueajin.pomidorki.presentation.tasks.TasksScreenContract.State
import com.enriqueajin.pomidorki.testutil.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TasksViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TasksViewModel

    @Before
    fun setUp() {
        viewModel = TasksViewModel()
    }

    @Test
    fun `WHEN init ViewModel THEN state with selected status To-do, dropdown collapsed, no grouping, and not loading`() {
        val state = viewModel.uiState.value

        assertEquals("To-do", state.selectedStatus)
        assertFalse(state.isDropdownExpanded)
        assertNull(state.groupedTasks)
        assertFalse(state.loading)
        assertNull(state.error)
    }

    @Test
    fun `WHEN init ViewModel THEN state shows only tasks with the selected status`() {
        val state = viewModel.uiState.value

        assertTrue(allTasksMatchSelectedStatus(state))
    }

    @Test
    fun `WHEN init ViewModel THEN state contains tasks sorted by priority`() {
        val state = viewModel.uiState.value

        assertTrue(isSortedByPriority(state.tasks.orEmpty()))
    }

    @Test
    fun `WHEN UpdateSelectedStatus to In progress THEN state updates selected status and shows only In progress tasks`() {
        viewModel.onEvent(Event.UpdateSelectedStatus(Status.IN_PROGRESS.label))

        val state = viewModel.uiState.value

        assertEquals(Status.IN_PROGRESS.label, state.selectedStatus)
        assertTrue(allTasksMatchSelectedStatus(state))
    }

    @Test
    fun `WHEN UpdateSelectedStatus to Done THEN state updates selected status and shows only Done tasks`() {
        viewModel.onEvent(Event.UpdateSelectedStatus(Status.DONE.label))

        val state = viewModel.uiState.value

        assertEquals(Status.DONE.label, state.selectedStatus)
        assertTrue(allTasksMatchSelectedStatus(state))
    }

    @Test
    fun `WHEN UpdateSelectedStatus to a status with no matching tasks THEN state contains empty task list`() {
        viewModel.onEvent(Event.UpdateSelectedStatus("No matching status"))

        val state = viewModel.uiState.value

        assertTrue(state.tasks.orEmpty().isEmpty())
    }

    @Test
    fun `WHEN SetDropdownExpanded to expanded THEN state shows dropdown expanded and task list unchanged`() {
        val tasksBefore = viewModel.uiState.value.tasks

        viewModel.onEvent(Event.SetDropdownExpanded(expanded = true))

        val state = viewModel.uiState.value

        assertTrue(state.isDropdownExpanded)
        assertEquals(tasksBefore, state.tasks)
    }

    @Test
    fun `WHEN SetDropdownExpanded to collapsed THEN state shows dropdown collapsed`() {
        viewModel.onEvent(Event.SetDropdownExpanded(expanded = true))
        viewModel.onEvent(Event.SetDropdownExpanded(expanded = false))

        assertFalse(viewModel.uiState.value.isDropdownExpanded)
    }

    @Test
    fun `WHEN UpdateCurrentSorting to title THEN state contains tasks sorted alphabetically by title`() {
        viewModel.onEvent(Event.UpdateCurrentSorting(Sorting.Title))

        assertTrue(
            isSortedByTitle(
                viewModel.uiState.value.tasks
                    .orEmpty(),
            ),
        )
    }

    @Test
    fun `WHEN UpdateCurrentSorting to category THEN state contains tasks sorted by category`() {
        viewModel.onEvent(Event.UpdateCurrentSorting(Sorting.Category))

        assertTrue(
            isSortedByCategory(
                viewModel.uiState.value.tasks
                    .orEmpty(),
            ),
        )
    }

    @Test
    fun `WHEN UpdateCurrentSorting to priority THEN state contains tasks sorted by priority`() {
        viewModel.onEvent(Event.UpdateCurrentSorting(Sorting.Title))
        viewModel.onEvent(Event.UpdateCurrentSorting(Sorting.Priority))

        assertTrue(
            isSortedByPriority(
                viewModel.uiState.value.tasks
                    .orEmpty(),
            ),
        )
    }

    @Test
    fun `GIVEN status In progress WHEN UpdateCurrentSorting to title THEN In progress tasks sorted by title`() {
        viewModel.onEvent(Event.UpdateSelectedStatus(Status.IN_PROGRESS.label))
        viewModel.onEvent(Event.UpdateCurrentSorting(Sorting.Title))

        val state = viewModel.uiState.value

        assertEquals(Status.IN_PROGRESS.label, state.selectedStatus)
        assertTrue(allTasksMatchSelectedStatus(state))
        assertTrue(isSortedByTitle(state.tasks.orEmpty()))
    }

    @Test
    fun `WHEN UpdateCurrentGrouping to category THEN state contains tasks grouped by category`() {
        viewModel.onEvent(Event.UpdateCurrentGrouping(Grouping.Category))

        val state = viewModel.uiState.value

        assertTrue(isGroupedByCategory(state))
    }

    @Test
    fun `WHEN UpdateCurrentGrouping to priority THEN state contains tasks grouped by priority`() {
        viewModel.onEvent(Event.UpdateCurrentGrouping(Grouping.Priority))

        val state = viewModel.uiState.value

        assertTrue(isGroupedByPriority(state))
    }

    @Test
    fun `WHEN UpdateCurrentGrouping cleared THEN state contains flat task list without groups`() {
        viewModel.onEvent(Event.UpdateCurrentGrouping(Grouping.Category))
        viewModel.onEvent(Event.UpdateCurrentGrouping(null))

        val state = viewModel.uiState.value

        assertNull(state.groupedTasks)
        assertFalse(state.tasks.isNullOrEmpty())
    }

    @Test
    fun `GIVEN status In progress WHEN UpdateCurrentGrouping to category THEN groups only In progress tasks`() {
        viewModel.onEvent(Event.UpdateSelectedStatus(Status.IN_PROGRESS.label))
        viewModel.onEvent(Event.UpdateCurrentGrouping(Grouping.Category))

        val state = viewModel.uiState.value

        assertTrue(allTasksMatchSelectedStatus(state))
        assertTrue(isGroupedByCategory(state))
    }

    @Test
    fun `GIVEN status In progress WHEN UpdateCurrentSorting to title and UpdateCurrentGrouping to category THEN sorted group order`() {
        viewModel.onEvent(Event.UpdateSelectedStatus(Status.IN_PROGRESS.label))
        viewModel.onEvent(Event.UpdateCurrentSorting(Sorting.Title))
        viewModel.onEvent(Event.UpdateCurrentGrouping(Grouping.Category))

        val state = viewModel.uiState.value

        assertTrue(isSortedByTitle(state.tasks.orEmpty()))
        assertTrue(groupsPreserveFlatListOrder(state))
    }

    private fun allTasksMatchSelectedStatus(state: State): Boolean =
        state.tasks.orEmpty().all { task -> task.status.label == state.selectedStatus }

    private fun isSortedByTitle(tasks: List<Task>): Boolean = tasks.zipWithNext().all { (first, second) -> first.title <= second.title }

    private fun isSortedByCategory(tasks: List<Task>): Boolean =
        tasks.zipWithNext().all { (first, second) -> first.category.name <= second.category.name }

    private fun isSortedByPriority(tasks: List<Task>): Boolean =
        tasks.zipWithNext().all { (first, second) ->
            first.priority.priorityValue <= second.priority.priorityValue
        }

    private fun isGroupedByCategory(state: State): Boolean {
        val groupedTasks = state.groupedTasks ?: return false

        return groupedTasks.all { (category, tasks) ->
            tasks.isNotEmpty() && tasks.all { task -> task.category.name == category }
        }
    }

    private fun isGroupedByPriority(state: State): Boolean {
        val groupedTasks = state.groupedTasks ?: return false

        return groupedTasks.all { (priority, tasks) ->
            tasks.isNotEmpty() && tasks.all { task -> task.priority.name == priority }
        }
    }

    private fun groupsPreserveFlatListOrder(state: State): Boolean {
        val groupedTasks = state.groupedTasks ?: return false
        val flat = state.tasks.orEmpty()
        val flattenedGroups = groupedTasks.values.flatten()

        if (flattenedGroups.size != flat.size) return false
        if (flattenedGroups.toSet() != flat.toSet()) return false

        return groupedTasks.values.all { group ->
            val indices = group.map { task -> flat.indexOf(task) }
            indices == indices.sorted()
        }
    }
}

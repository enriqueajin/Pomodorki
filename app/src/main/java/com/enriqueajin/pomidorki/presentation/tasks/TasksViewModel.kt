package com.enriqueajin.pomidorki.presentation.tasks

import androidx.lifecycle.ViewModel
import com.enriqueajin.pomidorki.domain.model.Task
import com.enriqueajin.pomidorki.presentation.tasks.TasksScreenContract.Event
import com.enriqueajin.pomidorki.presentation.tasks.TasksScreenContract.Grouping
import com.enriqueajin.pomidorki.presentation.tasks.TasksScreenContract.Sorting
import com.enriqueajin.pomidorki.presentation.tasks.TasksScreenContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TasksViewModel
    @Inject
    constructor() : ViewModel() {
        private val allTasks = getTasks()

        private val _uiState = MutableStateFlow(updateState(State()))
        val uiState: StateFlow<State> = _uiState.asStateFlow()

        fun onEvent(event: Event) {
            when (event) {
                is Event.UpdateSelectedStatus -> updateSelectedStatus(event.status)
                is Event.SetDropdownExpanded -> setDropdownExpanded(event.expanded)
                is Event.UpdateCurrentGrouping -> updateCurrentGrouping(event.grouping)
                is Event.UpdateCurrentSorting -> updateCurrentSorting(event.sorting)
            }
        }

        private fun updateSelectedStatus(status: String) {
            _uiState.update { current ->
                updateState(current.copy(selectedStatus = status))
            }
        }

        private fun setDropdownExpanded(expanded: Boolean) {
            _uiState.update { current ->
                updateState(current.copy(isDropdownExpanded = expanded))
            }
        }

        private fun updateCurrentSorting(sorting: Sorting) {
            _uiState.update { current ->
                updateState(current.copy(currentSorting = sorting))
            }
        }

        private fun updateCurrentGrouping(grouping: Grouping?) {
            _uiState.update { current ->
                updateState(current.copy(currentGrouping = grouping))
            }
        }

        private fun updateState(state: State): State {
            val filteredTasks = applyFilters(allTasks, state)
            val groupedTasks =
                state.currentGrouping?.let { grouping ->
                    when (grouping) {
                        Grouping.Category -> filteredTasks.groupBy { it.category.name }
                        Grouping.Priority -> filteredTasks.groupBy { it.priority.name }
                    }
                }

            return state.copy(
                tasks = filteredTasks,
                groupedTasks = groupedTasks,
                loading = false,
            )
        }

        private fun applyFilters(
            tasks: List<Task>,
            state: State,
        ): List<Task> =
            tasks
                .filter { task -> task.status.label == state.selectedStatus }
                .let { filtered ->
                    when (state.currentSorting) {
                        Sorting.Category -> filtered.sortedBy { it.category.name }
                        Sorting.Priority -> filtered.sortedBy { it.priority.priorityValue }
                        Sorting.Title -> filtered.sortedBy { it.title }
                    }
                }
    }

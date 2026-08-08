package com.enriqueajin.pomidorki.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TasksViewModel
    @Inject
    constructor() : ViewModel() {
        private val initialTasks = getTasks()
        private val tasksFlow = flowOf(initialTasks)

        private val filters = MutableStateFlow(TasksFilters())

        val uiState: StateFlow<TasksScreenState> =
            combine(
                tasksFlow.distinctUntilChanged(),
                filters,
            ) { tasks, myFilters ->
                createUiState(tasks, myFilters)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = createUiState(initialTasks, filters.value),
            )

        private fun createUiState(
            tasks: List<Task>,
            filters: TasksFilters,
        ): TasksScreenState {
            val filteredTasks = applyFilters(tasks, filters)
            val groupedTasks =
                filters.currentGrouping?.let { grouping ->
                    when (grouping) {
                        Grouping.Category -> filteredTasks.groupBy { it.category.name }
                        Grouping.Priority -> filteredTasks.groupBy { it.priority.name }
                    }
                }

            return TasksScreenState(
                isDropdownExpanded = filters.isDropdownExpanded,
                selectedStatus = filters.selectedStatus,
                tasks = filteredTasks,
                groupedTasks = groupedTasks,
                loading = false,
            )
        }

        fun onEvent(event: TasksScreenEvent) {
            when (event) {
                is TasksScreenEvent.UpdateSelectedStatus -> updateSelectedStatus(event.status)
                is TasksScreenEvent.SetDropdownExpanded -> setDropdownExpanded(event.expanded)
                is TasksScreenEvent.UpdateCurrentGrouping -> updateCurrentGrouping(event.grouping)
                is TasksScreenEvent.UpdateCurrentSorting -> updateCurrentSorting(event.sorting)
            }
        }

        private fun applyFilters(
            tasks: List<Task>,
            filters: TasksFilters,
        ): List<Task> =
            tasks
                .filter { task -> task.status.label == filters.selectedStatus }
                .let { filtered ->
                    when (filters.currentSorting) {
                        Sorting.Category -> filtered.sortedBy { it.category.name }
                        Sorting.Priority -> filtered.sortedBy { it.priority.priorityValue }
                        Sorting.Title -> filtered.sortedBy { it.title }
                    }
                }

        private fun updateSelectedStatus(status: String) {
            filters.value =
                filters.value.copy(
                    selectedStatus = status,
                )
        }

        private fun setDropdownExpanded(expanded: Boolean) {
            filters.value =
                filters.value.copy(
                    isDropdownExpanded = expanded,
                )
        }

        private fun updateCurrentSorting(sorting: Sorting) {
            filters.value =
                filters.value.copy(
                    currentSorting = sorting,
                )
        }

        private fun updateCurrentGrouping(grouping: Grouping?) {
            filters.value =
                filters.value.copy(
                    currentGrouping = grouping,
                )
        }
    }

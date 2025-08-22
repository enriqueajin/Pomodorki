package com.enriqueajin.pomidorki.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor() : ViewModel() {

    private val tasksFlow = flowOf(getTasks())

    private val _filters = MutableStateFlow(TasksFilters())

    val uiState: StateFlow<TasksScreenState> = combine(
        tasksFlow.distinctUntilChanged(),
        _filters
    ) { tasks, myFilters ->

        val filteredTasks = applyFilters(tasks, myFilters)

        val groupedTasks = myFilters.currentGrouping?.let { grouping ->
            when (grouping) {
                Grouping.Category -> filteredTasks.groupBy { it.category.name }
                Grouping.Priority -> filteredTasks.groupBy { it.priority.name }
            }
        }

        TasksScreenState(
            isDropdownExpanded = myFilters.isDropdownExpanded,
            selectedStatus = myFilters.selectedStatus,
            tasks = filteredTasks,
            groupedTasks = groupedTasks,
            loading = false
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TasksScreenState(loading = true)
    )

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
        filters: TasksFilters
    ): List<Task> {
        return tasks
            .filter { task -> task.status.label == filters.selectedStatus }
            .let { filtered ->
                when (filters.currentSorting) {
                    Sorting.Category -> filtered.sortedBy { it.category.name }
                    Sorting.Priority -> filtered.sortedBy { it.priority.priorityValue }
                    Sorting.Title -> filtered.sortedBy { it.title }
                }
            }
    }

    private fun updateSelectedStatus(status: String) {
        _filters.value = _filters.value.copy(
            selectedStatus = status
        )
    }

    private fun setDropdownExpanded(expanded: Boolean) {
        _filters.value = _filters.value.copy(
            isDropdownExpanded = expanded
        )
    }

    private fun updateCurrentSorting(sorting: Sorting) {
        _filters.value = _filters.value.copy(
            currentSorting = sorting
        )
    }

    private fun updateCurrentGrouping(grouping: Grouping?) {
        _filters.value = _filters.value.copy(
            currentGrouping = grouping
        )
    }
}
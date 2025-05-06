package com.enriqueajin.pomidorki.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(): ViewModel() {

    private val tasksFlow = flowOf(getTasks())

    private val _filters = MutableStateFlow(TasksFilters())

    private fun applyFilters(
        tasks: List<Task>,
        filters: TasksFilters
    ): List<Task> {
        return tasks
            .filter { task ->
                filters.selectedStatus.let { task.status.label == it }
            }
            .sortedWith(
                when(filters.currentSorting) {
                    Sorting.Category -> compareBy { it.category.name }
                    Sorting.Priority -> compareBy { it.priority.priorityValue }
                    Sorting.Title -> compareBy { it.title }
                }
            )
    }

    val uiState: StateFlow<TasksScreenState> = combine(
        tasksFlow,
        _filters
    ) { tasks, myFilters ->
        var state = TasksScreenState(
            isDropdownExpanded = myFilters.isDropdownExpanded,
            selectedStatus = myFilters.selectedStatus
        )

        if(myFilters.currentGrouping == null) {
            val filteredTasks = applyFilters(tasks, myFilters)
            state = state.copy(tasks = filteredTasks)
        } else {
            val groupedTasks = myFilters.currentGrouping.let { currentGrouping ->
                when(currentGrouping) {
                    Grouping.Category -> tasks.groupBy { it.category.name }
                    Grouping.Priority -> tasks.groupBy { it.priority.name }
                }
            }
            val filteredGroup = groupedTasks?.let { taskMap ->
                taskMap.mapValues { (key, value) ->

                    applyFilters(value, myFilters)
                }
            }
            state = state.copy(groupedTasks = filteredGroup)
        }
        state

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TasksScreenState()
    )

    fun onEvent(event: TasksScreenEvent) {
        when(event) {
            is TasksScreenEvent.UpdateSelectedStatus -> updateSelectedStatus(event.status)
            is TasksScreenEvent.SetDropdownExpanded -> setDropdownExpanded(event.expanded)
            is TasksScreenEvent.UpdateCurrentGrouping -> updateCurrentGrouping(event.grouping)
            is TasksScreenEvent.UpdateCurrentSorting -> updateCurrentSorting(event.sorting)
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
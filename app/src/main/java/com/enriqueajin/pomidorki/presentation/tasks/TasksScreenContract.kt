package com.enriqueajin.pomidorki.presentation.tasks

import com.enriqueajin.pomidorki.domain.model.Task

sealed interface TasksScreenContract {
    data class State(
        val tasks: List<Task>? = null,
        val groupedTasks: Map<String, List<Task>>? = null,
        val selectedStatus: String = "To-do",
        val isDropdownExpanded: Boolean = false,
        val currentSorting: Sorting = Sorting.Priority,
        val currentGrouping: Grouping? = null,
        val loading: Boolean = false,
        val error: String? = null,
    )

    sealed class Event {
        data class UpdateSelectedStatus(
            val status: String,
        ) : Event()

        data class SetDropdownExpanded(
            val expanded: Boolean,
        ) : Event()

        data class UpdateCurrentSorting(
            val sorting: Sorting,
        ) : Event()

        data class UpdateCurrentGrouping(
            val grouping: Grouping?,
        ) : Event()
    }

    sealed class Effect

    sealed class Sorting {
        data object Title : Sorting()

        data object Category : Sorting()

        data object Priority : Sorting()
    }

    sealed class Grouping {
        data object Category : Grouping()

        data object Priority : Grouping()
    }
}

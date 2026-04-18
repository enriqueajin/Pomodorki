package com.enriqueajin.pomidorki.presentation.tasks

data class TasksFilters(
    val selectedStatus: String = "To-do",
//    val groupingByCategoryEnabled: Boolean = false,
    val isDropdownExpanded: Boolean = false,
    val currentSorting: Sorting = Sorting.Priority,
    val currentGrouping: Grouping? = null,
)

sealed class Sorting {
    data object Title : Sorting()

    data object Category : Sorting()

    data object Priority : Sorting()
}

sealed class Grouping {
    data object Category : Grouping()

    data object Priority : Grouping()
}

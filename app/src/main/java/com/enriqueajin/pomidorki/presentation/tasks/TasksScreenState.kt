package com.enriqueajin.pomidorki.presentation.tasks

data class TasksScreenState(
    val tasks: List<Task>? = null,
    val groupedTasks: Map<String, List<Task>>? = null,
    val selectedStatus: String? = null,
    val isDropdownExpanded: Boolean = false,
    val loading: Boolean? = true,
    val error: String? = null
)

package com.enriqueajin.pomidorki.presentation.tasks

sealed interface TasksScreenEvent {

    data class UpdateSelectedStatus(val status: String): TasksScreenEvent
    data class SetDropdownExpanded(val expanded: Boolean): TasksScreenEvent
    data class UpdateCurrentSorting(val sorting: Sorting): TasksScreenEvent
    data class UpdateCurrentGrouping(val grouping: Grouping?): TasksScreenEvent
}
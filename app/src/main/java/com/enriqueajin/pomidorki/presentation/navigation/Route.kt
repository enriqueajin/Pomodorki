package com.enriqueajin.pomidorki.presentation.navigation

import com.enriqueajin.pomidorki.presentation.tasks.Task
import kotlinx.serialization.Serializable

@Serializable
sealed class Route(val route: String) {

    @Serializable
    data object Timer: Route("Timer")

    @Serializable
    data object Tasks: Route("Tasks")

    @Serializable
    data object Stats: Route("Stats")

    @Serializable
    data object Settings: Route("Settings")

    @Serializable
    data class TaskDetail(val task: Task): Route("TaskDetail")

}
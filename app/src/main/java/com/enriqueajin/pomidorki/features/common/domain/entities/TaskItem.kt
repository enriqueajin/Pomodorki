package com.enriqueajin.pomidorki.features.common.domain.entities


sealed interface TaskItem {

    val id: Int
    data class Task (
        override val id: Int,
        val title: String,
        val description: String,
        val tag: String,
        val completed:  Boolean
    ) : TaskItem

    data class Pomodoro(
        override val id: Int,
        val pomodoroId: Int = 0,
        val fullPeriod: Long,
        val completed: Boolean,
        val taskId: Int
    ) : TaskItem

    data class TimePeriod(
        override val id: Int,
        val periodId: Int,
        val startTime: Long,
        val endTime: Long,
        val pomodoroId: Int
    ) : TaskItem
}

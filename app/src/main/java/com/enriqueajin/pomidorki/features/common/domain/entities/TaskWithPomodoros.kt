package com.enriqueajin.pomidorki.features.common.domain.entities

import com.enriqueajin.pomidorki.features.common.domain.entities.TaskItem.Task

data class TaskWithPomodoros(
    val task: Task,
    val pomodoros: List<PomodoroWithPeriods>
)

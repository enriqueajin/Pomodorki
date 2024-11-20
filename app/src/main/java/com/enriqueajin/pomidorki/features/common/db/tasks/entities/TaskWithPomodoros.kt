package com.enriqueajin.pomidorki.features.common.db.tasks.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithPomodoros (
    @Embedded
    val task: TaskDbEntity,
    @Relation(
        parentColumn = "task_id",
        entityColumn = "pomodoro_id"
    )
    val pomodoros: List<PomodoroDbEntity>
)
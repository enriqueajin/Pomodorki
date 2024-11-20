package com.enriqueajin.pomidorki.features.common.db.tasks.entities

import androidx.room.Embedded
import androidx.room.Relation

data class FullTaskInfo(
    @Embedded
    val task: TaskDbEntity,
    @Relation(
        parentColumn = "task_id",
        entityColumn = "pomodoro_id",
        entity = PomodoroDbEntity::class
    )
    val pomodorosWithPeriods: List<PomodoroWithPeriods>
)

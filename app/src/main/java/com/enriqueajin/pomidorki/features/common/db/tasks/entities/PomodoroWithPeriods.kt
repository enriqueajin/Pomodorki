package com.enriqueajin.pomidorki.features.common.db.tasks.entities

import androidx.room.Embedded
import androidx.room.Relation

data class PomodoroWithPeriods(
    @Embedded
    val pomodoro: PomodoroDbEntity,
    @Relation(
        parentColumn = "pomodoro_id",
        entityColumn = "period_id"
    )
    val periodDbEntity: List<TimePeriodDbEntity>
)

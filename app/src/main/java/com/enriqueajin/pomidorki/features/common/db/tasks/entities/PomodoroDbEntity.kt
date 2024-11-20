package com.enriqueajin.pomidorki.features.common.db.tasks.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro")
data class PomodoroDbEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pomodoro_id")
    val pomodoroId: Int = 0,
    val fullPeriod: Long,
    val completed: Boolean,
    val taskId: Int
)

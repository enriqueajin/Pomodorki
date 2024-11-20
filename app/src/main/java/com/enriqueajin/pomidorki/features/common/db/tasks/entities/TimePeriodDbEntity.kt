package com.enriqueajin.pomidorki.features.common.db.tasks.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_period")
data class TimePeriodDbEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "period_id")
    val periodId: Int,
    val startTime: Long,
    val endTime: Long,
    val pomodoroId: Int
)

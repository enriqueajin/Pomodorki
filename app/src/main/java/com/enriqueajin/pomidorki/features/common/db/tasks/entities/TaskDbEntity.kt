package com.enriqueajin.pomidorki.features.common.db.tasks.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskDbEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    val taskId: Int = 0,
    val title: String,
    val description: String,
    val tag: String,
    val completed: Boolean,
)

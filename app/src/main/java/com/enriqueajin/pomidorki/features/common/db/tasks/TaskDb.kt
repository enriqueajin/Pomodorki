package com.enriqueajin.pomidorki.features.common.db.tasks

import androidx.room.Database
import com.enriqueajin.pomidorki.features.common.db.tasks.entities.PomodoroDbEntity
import com.enriqueajin.pomidorki.features.common.db.tasks.entities.TaskDbEntity
import com.enriqueajin.pomidorki.features.common.db.tasks.entities.TimePeriodDbEntity

@Database(entities = [TaskDbEntity::class, PomodoroDbEntity::class, TimePeriodDbEntity::class], version = 1)
abstract class TaskDb {
    abstract fun taskDao(): TaskDao
}
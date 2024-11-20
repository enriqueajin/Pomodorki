package com.enriqueajin.pomidorki.features.common.db.tasks

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.enriqueajin.pomidorki.features.common.db.tasks.entities.PomodoroDbEntity
import com.enriqueajin.pomidorki.features.common.db.tasks.entities.TaskDbEntity
import com.enriqueajin.pomidorki.features.common.db.tasks.entities.TimePeriodDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Upsert
    suspend fun insertOrUpdateTask(task: TaskDb): Int
    @Query("SELECT * FROM task WHERE task_id = :id")
    fun getTask(id: Int): Flow<TaskDbEntity>
    @Query("DELETE  FROM task WHERE task_id = :id")
    suspend fun deleteTask(id: Int): Unit
    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<TaskDbEntity>>

    @Upsert
    suspend fun insertOrUpdatePomodoro(pomodoro: PomodoroDbEntity): Int
    @Query("SELECT * FROM pomodoro WHERE pomodoro_id = :id")
    fun getPomodoro(id: Int): Flow<PomodoroDbEntity>
    @Query("DELETE  FROM pomodoro WHERE pomodoro_id = :id")
    suspend fun deletePomodoro(id: Int): Unit
    @Query("SELECT * FROM task")
    fun getAllPomodoros(): Flow<List<PomodoroDbEntity>>

    @Upsert
    suspend fun insertOrUpdatePeriod(period: TimePeriodDbEntity): Int
    @Query("SELECT * FROM time_period WHERE period_id = :id")
    fun getPeriodId(id: Int): Flow<TimePeriodDbEntity>
    @Query("DELETE  FROM time_period WHERE period_id = :id")
    suspend fun deletePeriod(id: Int): Unit
    @Query("SELECT * FROM task")
    fun getAllTimePeriods(): Flow<List<TimePeriodDbEntity>>

}
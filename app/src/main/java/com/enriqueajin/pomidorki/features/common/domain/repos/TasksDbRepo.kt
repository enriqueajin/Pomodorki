package com.enriqueajin.pomidorki.features.common.domain.repos

import com.enriqueajin.pomidorki.features.common.domain.entities.PomodoroWithPeriods
import com.enriqueajin.pomidorki.features.common.domain.entities.TaskItem
import com.enriqueajin.pomidorki.features.common.domain.entities.TaskWithPomodoros
import kotlinx.coroutines.flow.Flow
import com.enriqueajin.pomidorki.features.common.utl.Result

//  Todo: Complete the repo
interface TasksDbRepo {
    fun <T: TaskItem> getItem(id: Int): Flow<Result<T>>
    fun <T: TaskItem> insert(task: T): Flow<Result<Unit>>
    fun <T: TaskItem> delete(id: Int): Flow<Result<Unit>>
    fun <T: TaskItem> getAll(): Flow<Result<List<T>>>
    fun getPomodoroWithPeriods(pomodoroId: Int): Flow<Result<PomodoroWithPeriods>>
    fun getTaskWithPomodoros(taskId: Int): Flow<Result<TaskWithPomodoros>>
}
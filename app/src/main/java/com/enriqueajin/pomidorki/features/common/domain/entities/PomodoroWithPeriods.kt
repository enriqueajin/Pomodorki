package com.enriqueajin.pomidorki.features.common.domain.entities

import com.enriqueajin.pomidorki.features.common.domain.entities.TaskItem.Pomodoro
import com.enriqueajin.pomidorki.features.common.domain.entities.TaskItem.TimePeriod

data class PomodoroWithPeriods(
    val pomodoro: Pomodoro,
    val periods: List<TimePeriod>
)

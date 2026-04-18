package com.enriqueajin.pomidorki.presentation.home

import com.enriqueajin.pomidorki.utils.TimeFormatter.formatTime
import javax.inject.Inject

class TimerUiMapper
    @Inject
    constructor() {
        fun formatProgress(
            timeLeftMillis: Long,
            initialMillis: Long,
        ): Float {
            if (initialMillis <= 0L) return 0f
            return (timeLeftMillis.toFloat() / initialMillis.toFloat()).coerceIn(0f, 1f)
        }

        fun formatTimerText(timeLeftMillis: Long): String = timeLeftMillis.formatTime()
    }

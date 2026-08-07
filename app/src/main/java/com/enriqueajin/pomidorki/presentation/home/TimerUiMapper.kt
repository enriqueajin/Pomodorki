package com.enriqueajin.pomidorki.presentation.home

import com.enriqueajin.pomidorki.utils.TimeFormatter.formatTime
import javax.inject.Inject

class TimerUiMapper
    @Inject
    constructor() {
        fun formatTimerText(timeLeftMillis: Long): String = timeLeftMillis.formatTime()
    }

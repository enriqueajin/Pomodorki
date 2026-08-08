package com.enriqueajin.pomidorki.presentation.home

import com.enriqueajin.pomidorki.utils.TimeFormatter.formatCountdownTime
import javax.inject.Inject

class TimerUiMapper
    @Inject
    constructor() {
        fun formatTimerText(timeLeftMillis: Long): String = timeLeftMillis.formatCountdownTime()
    }

package com.enriqueajin.pomidorki.utils

import java.util.concurrent.TimeUnit

object TimeFormatter {
    private const val FORMAT = "%02d:%02d"

    fun Long.formatTime(): String = String.format(
        FORMAT,
        TimeUnit.MILLISECONDS.toMinutes(this),
        TimeUnit.MILLISECONDS.toSeconds(this) % 60
    )
}
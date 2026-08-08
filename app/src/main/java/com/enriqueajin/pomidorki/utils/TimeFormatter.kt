package com.enriqueajin.pomidorki.utils

import java.util.concurrent.TimeUnit

object TimeFormatter {
    private const val FORMAT = "%02d:%02d"

    fun Long.formatTime(): String =
        String.format(
            FORMAT,
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) % 60,
        )

    fun Long.formatCountdownTime(): String =
        String.format(
            FORMAT,
            TimeUnit.MILLISECONDS.toMinutes(this + 999L),
            TimeUnit.MILLISECONDS.toSeconds(this + 999L) % 60,
        )
}

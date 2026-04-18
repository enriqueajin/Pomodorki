package com.enriqueajin.pomidorki.utils

fun Int.toMillis(): Long = this.toLong() * 60_000

fun Long.toMinutes(): Long = this / 60_000

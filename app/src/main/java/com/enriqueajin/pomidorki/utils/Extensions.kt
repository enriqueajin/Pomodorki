package com.enriqueajin.pomidorki.utils

fun Int.toMillis(): Long {
    return this.toLong() * 60_000
}

fun Long.toMinutes(): Long {
    return this / 60_000
}
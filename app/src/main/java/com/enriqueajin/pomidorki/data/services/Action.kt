package com.enriqueajin.pomidorki.data.services

enum class Action {
    START, STOP, CANCEL, PAUSE, NONE, RESUME
}

fun String.toAction(): Action {
    return when(this) {
        "START" -> Action.START
        "STOP" -> Action.STOP
        "CANCEL" -> Action.CANCEL
        "PAUSE" -> Action.PAUSE
        "NONE" -> Action.NONE
        else -> Action.NONE
    }
}
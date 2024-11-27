package com.enriqueajin.pomidorki.data.countdown

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.concurrent.TimeUnit

class CountDownPomodoro(private val totalMinutes: Long) {

    private var countDownTimer: CountDownTimer? = null

    private val initialMillis = TimeUnit.MINUTES.toMillis(totalMinutes)
    var timeLeft by mutableLongStateOf(initialMillis)
    val countDownInterval = 1_000L

    private var isActive by mutableStateOf(false)

    fun start() {
        isActive = true
        countDownTimer = object : CountDownTimer(timeLeft, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                println("The time left is $timeLeft")
            }

            override fun onFinish() {
                timeLeft = 0
                isActive = false
            }
        }.start()
    }

    fun pause() {
        isActive = false
        countDownTimer?.cancel()
    }

    fun reset() {
        isActive = false
        countDownTimer?.cancel()
        timeLeft = initialMillis
    }
}
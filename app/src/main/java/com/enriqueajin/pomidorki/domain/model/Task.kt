package com.enriqueajin.pomidorki.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val title: String,
    val description: String,
    val targetPomodoros: Int,
    val status: Status,
    val priority: Priority,
    val category: Category,
    val dueDate: String,
)

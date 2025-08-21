package com.enriqueajin.pomidorki.domain.model

enum class Priority(val hexColor: Long, val priorityValue: Int) {
    VERY_HIGH(0xFFE74C3C, 1),
    HIGH(0xFFF5B041, 2),
    MEDIUM(0xFFF7DC6F, 3),
    LOW(0xFFA8D5BA, 4),
}
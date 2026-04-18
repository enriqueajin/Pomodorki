package com.enriqueajin.pomidorki.domain.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val name: String,
    @Contextual val color: Color,
)

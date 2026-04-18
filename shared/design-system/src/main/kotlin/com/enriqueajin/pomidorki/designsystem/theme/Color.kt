package com.enriqueajin.pomidorki.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val RedPrimary = Color(0xFFB3261E)
private val RedPrimaryContainer = Color(0xFFF9DEDC)
private val RedOnPrimary = Color(0xFFFFFFFF)
private val RedOnPrimaryContainer = Color(0xFF410E0B)

private val NeutralBackground = Color(0xFFFFFBFF)
private val NeutralOnBackground = Color(0xFF1C1B1F)

internal val LightColorScheme =
    lightColorScheme(
        primary = RedPrimary,
        onPrimary = RedOnPrimary,
        primaryContainer = RedPrimaryContainer,
        onPrimaryContainer = RedOnPrimaryContainer,
        background = NeutralBackground,
        onBackground = NeutralOnBackground,
    )

internal val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFFFFB4AB),
        onPrimary = Color(0xFF690005),
        primaryContainer = Color(0xFF93000A),
        onPrimaryContainer = Color(0xFFFFDAD6),
        background = Color(0xFF1C1B1F),
        onBackground = Color(0xFFE6E1E5),
    )

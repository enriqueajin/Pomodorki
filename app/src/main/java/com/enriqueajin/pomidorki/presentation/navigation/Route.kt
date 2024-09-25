package com.enriqueajin.pomidorki.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Timer: Route // Home

    @Serializable
    data object Tasks: Route

    @Serializable
    data object Stats: Route
}
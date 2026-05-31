package com.amrdeveloper.turtle.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable data class Home(val sourceCode: String = "") : AppRoute
    @Serializable data object Files : AppRoute
    @Serializable data object Settings : AppRoute
}

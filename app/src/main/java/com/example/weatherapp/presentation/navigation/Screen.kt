package com.example.weatherapp.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Weather : Screen

    @Serializable
    data object Favorites : Screen

    @Serializable
    data object Activity : Screen
}
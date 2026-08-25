package com.example.fluidvectorar

import kotlinx.serialization.Serializable

sealed interface AppRoute {

    @Serializable
    data object Auth : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data class EditorStudio(
        val projectId: String? = null // Arguments pass karne ke liye clean way
    ) : AppRoute

    @Serializable
    data object ARTracing : AppRoute
}
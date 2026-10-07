package dev.cirimo.trosko.feature.home

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** The place on the back stack that shows [HomeRoute]. It is where the app starts. */
@Serializable
data object HomeKey : NavKey

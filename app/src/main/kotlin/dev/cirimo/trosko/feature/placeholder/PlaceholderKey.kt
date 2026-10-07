package dev.cirimo.trosko.feature.placeholder

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** The place on the back stack that shows [PlaceholderRoute]. */
@Serializable
data object PlaceholderKey : NavKey

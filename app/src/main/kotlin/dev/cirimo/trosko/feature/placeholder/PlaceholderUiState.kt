package dev.cirimo.trosko.feature.placeholder

sealed interface PlaceholderUiState {
    /** Storage has not answered yet. */
    data object Loading : PlaceholderUiState

    /** The database was opened and queried successfully. */
    data object Ready : PlaceholderUiState
}

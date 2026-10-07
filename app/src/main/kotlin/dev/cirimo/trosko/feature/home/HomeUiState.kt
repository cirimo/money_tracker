package dev.cirimo.trosko.feature.home

import dev.cirimo.trosko.format.RecordLine

sealed interface HomeUiState {
    /** Storage has not answered yet. The page stays blank instead of claiming to be empty. */
    data object Loading : HomeUiState

    /** @property latest the records written most recently, newest first; empty on a new install. */
    data class Loaded(
        val latest: List<RecordLine>,
    ) : HomeUiState
}

package dev.cirimo.trosko.feature.home

import dev.cirimo.trosko.domain.model.RecordId
import dev.cirimo.trosko.format.RecordLine
import dev.cirimo.trosko.format.RecordNotice

sealed interface HomeUiState {
    /** Storage has not answered yet. The page stays blank instead of claiming to be empty. */
    data object Loading : HomeUiState

    /**
     * @property latest the records written most recently, newest first; empty on a new install.
     * @property notice what was just done to one of them on the correction screen, if anything.
     * @property landedId the record that was just corrected; it sits tilted on the page.
     * @property isLandingPending the landing of [landedId] has not been played yet. The screen
     * acknowledges it once the animation starts; it is state, not an event, so it survives a
     * rotation.
     */
    data class Loaded(
        val latest: List<RecordLine>,
        val notice: RecordNotice? = null,
        val landedId: RecordId? = null,
        val isLandingPending: Boolean = false,
    ) : HomeUiState
}

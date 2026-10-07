package dev.cirimo.trosko.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cirimo.trosko.domain.repository.RecordRepository
import dev.cirimo.trosko.format.RecordChange
import dev.cirimo.trosko.format.RecordLine
import dev.cirimo.trosko.format.RecordNotice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Clock
import java.time.LocalDate

// Keeps the upstream query alive across a rotation, and lets it go when the screen is gone.
private const val STOP_TIMEOUT_MILLIS = 5_000L

// Home shows the latest entries, not the history; this is as far back as it reaches.
private const val LATEST_COUNT = 50

class HomeViewModel(
    records: RecordRepository,
    private val clock: Clock,
) : ViewModel() {
    private val today = MutableStateFlow(LocalDate.now(clock))
    private val lastChange = MutableStateFlow(LastChange())

    val uiState: StateFlow<HomeUiState> =
        combine(records.observeLatest(LATEST_COUNT), today, lastChange) { latest, today, lastChange ->
            val change = lastChange.change
            HomeUiState.Loaded(
                latest = latest.map { RecordLine.of(it, today) },
                notice =
                    when (change) {
                        is RecordChange.Corrected -> RecordNotice.Corrected
                        is RecordChange.Deleted -> RecordNotice.Deleted
                        null -> null
                    },
                landedId = (change as? RecordChange.Corrected)?.id,
                isLandingPending = lastChange.isLandingPending,
            ) as HomeUiState
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = HomeUiState.Loading,
        )

    /** The screen is in front again; the day may have changed while it was away. */
    fun onResumed() {
        today.value = LocalDate.now(clock)
    }

    /** A record opened from this page was corrected or deleted. */
    fun onRecordChanged(change: RecordChange) {
        lastChange.value = LastChange(change, isLandingPending = change is RecordChange.Corrected)
    }

    /** The screen has started the landing animation of the corrected record. */
    fun onLandingShown() = lastChange.update { it.copy(isLandingPending = false) }

    /** The user is turning to another page; what was said about the last change has been read. */
    fun onLeaving() {
        lastChange.value = LastChange()
    }
}

private data class LastChange(
    val change: RecordChange? = null,
    val isLandingPending: Boolean = false,
)

package dev.cirimo.trosko.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cirimo.trosko.domain.repository.RecordRepository
import dev.cirimo.trosko.format.RecordLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    val uiState: StateFlow<HomeUiState> =
        combine(records.observeLatest(LATEST_COUNT), today) { latest, today ->
            HomeUiState.Loaded(latest.map { RecordLine.of(it, today) }) as HomeUiState
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = HomeUiState.Loading,
        )

    /** The screen is in front again; the day may have changed while it was away. */
    fun onResumed() {
        today.value = LocalDate.now(clock)
    }
}

package dev.cirimo.trosko.feature.placeholder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cirimo.trosko.domain.model.RecordKind
import dev.cirimo.trosko.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Keeps the upstream query alive across a rotation, and lets it go when the screen is really gone.
private const val STOP_TIMEOUT_MILLIS = 5_000L

class PlaceholderViewModel(
    categories: CategoryRepository,
) : ViewModel() {
    val uiState: StateFlow<PlaceholderUiState> =
        categories
            .observeActive(RecordKind.EXPENSE)
            .map { PlaceholderUiState.Ready as PlaceholderUiState }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = PlaceholderUiState.Loading,
            )
}

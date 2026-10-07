package dev.cirimo.trosko.feature.placeholder

import dev.cirimo.trosko.domain.model.Category
import dev.cirimo.trosko.domain.model.RecordKind
import dev.cirimo.trosko.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// Replacing the main dispatcher is how a view model's scope is driven from a test.
@OptIn(ExperimentalCoroutinesApi::class)
class PlaceholderViewModelTest {
    private val categories = FakeCategoryRepository()

    @Before
    fun replaceMainDispatcher() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun restoreMainDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state is loading until storage answers`() =
        runTest {
            val viewModel = PlaceholderViewModel(categories)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

            assertEquals(PlaceholderUiState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `state is ready once storage has answered`() =
        runTest {
            val viewModel = PlaceholderViewModel(categories)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

            categories.emit(emptyList())

            assertEquals(PlaceholderUiState.Ready, viewModel.uiState.value)
        }
}

private class FakeCategoryRepository : CategoryRepository {
    private val active = MutableSharedFlow<List<Category>>()

    override fun observeActive(kind: RecordKind): Flow<List<Category>> = active

    suspend fun emit(categories: List<Category>) = active.emit(categories)
}

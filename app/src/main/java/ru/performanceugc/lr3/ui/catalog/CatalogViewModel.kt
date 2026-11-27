package ru.performanceugc.lr3.ui.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ru.performanceugc.lr3.data.BriefRepository
import ru.performanceugc.lr3.domain.filterCreators
import ru.performanceugc.lr3.domain.model.Category
import ru.performanceugc.lr3.navigation.BRIEF_ID

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repository: BriefRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private val briefId = savedState.get<String>(BRIEF_ID).orEmpty()
    private val query = savedState.getStateFlow("query", "")
    private val category = savedState.getStateFlow("category", "")
    private val budgetOnly = savedState.getStateFlow("budgetOnly", true)

    private fun snapshot(query: String, categoryName: String, budgetOnly: Boolean): CatalogUiState {
        val brief = repository.brief(briefId)
        val category = Category.entries.find { it.name == categoryName }
        val creators = filterCreators(repository.creators, brief, category, query, budgetOnly)
        return CatalogUiState(brief, creators, query, category, budgetOnly)
    }

    val state = combine(query, category, budgetOnly, repository.latestBrief) { search, filter, affordable, _ ->
        snapshot(search, filter, affordable)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), snapshot(query.value, category.value, budgetOnly.value))

    fun updateQuery(value: String) { savedState["query"] = value }

    fun selectCategory(category: Category?) { savedState["category"] = category?.name.orEmpty() }

    fun toggleBudget() { savedState["budgetOnly"] = !budgetOnly.value }

    fun resetFilters() {
        updateQuery("")
        selectCategory(null)
        savedState["budgetOnly"] = false
    }
}

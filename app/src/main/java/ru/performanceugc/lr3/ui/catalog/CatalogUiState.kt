package ru.performanceugc.lr3.ui.catalog

import ru.performanceugc.lr3.domain.model.Brief
import ru.performanceugc.lr3.domain.model.Category
import ru.performanceugc.lr3.domain.model.Creator

data class CatalogUiState(
    val brief: Brief? = null,
    val creators: List<Creator> = emptyList(),
    val query: String = "",
    val category: Category? = null,
    val budgetOnly: Boolean = true,
)

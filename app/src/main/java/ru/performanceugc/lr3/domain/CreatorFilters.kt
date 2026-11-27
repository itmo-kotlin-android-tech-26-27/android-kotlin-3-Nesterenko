package ru.performanceugc.lr3.domain

import ru.performanceugc.lr3.domain.model.Brief
import ru.performanceugc.lr3.domain.model.Category
import ru.performanceugc.lr3.domain.model.Creator

fun filterCreators(
    creators: List<Creator>,
    brief: Brief?,
    category: Category?,
    query: String,
    budgetOnly: Boolean,
): List<Creator> {
    val search = query.trim()
    return creators.filter { creator ->
        (category == null || creator.category == category) &&
            (brief == null || !budgetOnly || BriefRules.fitsBudget(brief, creator)) &&
            (search.isEmpty() || listOf(creator.name, creator.category.label, creator.platform)
                .any { it.contains(search, ignoreCase = true) })
    }
}

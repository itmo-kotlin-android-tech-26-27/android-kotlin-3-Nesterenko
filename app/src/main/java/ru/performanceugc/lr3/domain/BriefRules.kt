package ru.performanceugc.lr3.domain

import ru.performanceugc.lr3.domain.model.Brief
import ru.performanceugc.lr3.domain.model.BriefField
import ru.performanceugc.lr3.domain.model.BriefInput
import ru.performanceugc.lr3.domain.model.Creator
import ru.performanceugc.lr3.domain.model.Rubles

object BriefRules {
    fun parseBudget(value: String): Rubles? =
        value.trim().toIntOrNull()?.takeIf { it > 0 }?.let(::Rubles)

    fun invalidFields(input: BriefInput): Set<BriefField> = buildSet {
        if (input.title.isBlank()) add(BriefField.TITLE)
        if (input.product.isBlank()) add(BriefField.PRODUCT)
        if (input.goal.isBlank()) add(BriefField.GOAL)
        if (parseBudget(input.budget) == null) add(BriefField.BUDGET)
    }

    fun fitsBudget(brief: Brief, creator: Creator): Boolean = creator.price <= brief.budget
}

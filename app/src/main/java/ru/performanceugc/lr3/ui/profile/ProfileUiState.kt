package ru.performanceugc.lr3.ui.profile

import ru.performanceugc.lr3.domain.model.Brief
import ru.performanceugc.lr3.domain.model.Creator

data class ProfileUiState(
    val brief: Brief?,
    val creator: Creator?,
    val fitsBudget: Boolean,
)

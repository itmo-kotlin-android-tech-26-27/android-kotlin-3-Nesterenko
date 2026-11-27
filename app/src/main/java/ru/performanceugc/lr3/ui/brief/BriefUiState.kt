package ru.performanceugc.lr3.ui.brief

import ru.performanceugc.lr3.domain.model.BriefField
import ru.performanceugc.lr3.domain.model.BriefInput

data class BriefUiState(
    val input: BriefInput = BriefInput(),
    val invalidFields: Set<BriefField> = emptySet(),
)

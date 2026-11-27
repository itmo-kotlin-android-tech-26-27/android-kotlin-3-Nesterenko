package ru.performanceugc.lr3.ui.brief

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.performanceugc.lr3.data.BriefRepository
import ru.performanceugc.lr3.domain.BriefRules
import ru.performanceugc.lr3.domain.model.Brief
import ru.performanceugc.lr3.domain.model.BriefField
import ru.performanceugc.lr3.domain.model.BriefInput
import ru.performanceugc.lr3.navigation.BRIEF_ID

@HiltViewModel
class BriefViewModel @Inject constructor(
    private val repository: BriefRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private fun readInput() = BriefInput(
        title = savedState[BriefField.TITLE.name] ?: "",
        product = savedState[BriefField.PRODUCT.name] ?: "",
        goal = savedState[BriefField.GOAL.name] ?: "",
        budget = savedState[BriefField.BUDGET.name] ?: "",
    )

    private val mutableState = MutableStateFlow(BriefUiState(readInput()))
    val state: StateFlow<BriefUiState> = mutableState

    fun update(field: BriefField, value: String) {
        savedState[field.name] = value
        mutableState.value = BriefUiState(readInput(), state.value.invalidFields - field)
    }

    fun fillExample() {
        update(BriefField.TITLE, "Новый ритуал ухода")
        update(BriefField.PRODUCT, "Увлажняющая сыворотка")
        update(BriefField.GOAL, "Показать текстуру, нанесение и результат в коротком обзоре.")
        update(BriefField.BUDGET, "12000")
    }

    fun submit(): String? {
        val input = state.value.input
        val invalid = BriefRules.invalidFields(input)
        mutableState.value = BriefUiState(input, invalid)
        if (invalid.isNotEmpty()) return null

        val budget = checkNotNull(BriefRules.parseBudget(input.budget))
        val id = savedState.get<String>(BRIEF_ID) ?: UUID.randomUUID().toString()
        val existing = repository.brief(id)
        val candidate = Brief(id, input.title.trim(), input.product.trim(), input.goal.trim(), budget)
        val selected = repository.creators.find { it.id == existing?.selectedCreatorId }
        val selectedId = selected?.takeIf { BriefRules.fitsBudget(candidate, it) }?.id
        repository.save(candidate.copy(selectedCreatorId = selectedId))
        savedState[BRIEF_ID] = id
        return id
    }
}

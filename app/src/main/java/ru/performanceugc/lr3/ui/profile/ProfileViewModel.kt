package ru.performanceugc.lr3.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.performanceugc.lr3.data.BriefRepository
import ru.performanceugc.lr3.domain.BriefRules
import ru.performanceugc.lr3.navigation.BRIEF_ID
import ru.performanceugc.lr3.navigation.CREATOR_ID

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: BriefRepository,
    savedState: SavedStateHandle,
) : ViewModel() {
    private val creator = repository.creators.find { it.id == savedState.get<String>(CREATOR_ID) }
    private val briefId = savedState.get<String>(BRIEF_ID).orEmpty()
    val project = creator?.projects?.find { it.id == savedState.get<String>("projectId") }
    private val mutableState = MutableStateFlow(snapshot())
    val state: StateFlow<ProfileUiState> = mutableState

    private fun snapshot(): ProfileUiState {
        val brief = repository.brief(briefId)
        return ProfileUiState(brief, creator, brief != null && creator != null && BriefRules.fitsBudget(brief, creator))
    }

    fun confirm() {
        val creator = state.value.creator ?: return
        val brief = repository.brief(briefId) ?: return
        if (!BriefRules.fitsBudget(brief, creator) || brief.selectedCreatorId == creator.id) return
        repository.save(brief.copy(selectedCreatorId = creator.id))
        mutableState.value = snapshot()
    }
}

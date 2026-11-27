package ru.performanceugc.lr3.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ru.performanceugc.lr3.data.BriefRepository

@HiltViewModel
class HomeViewModel @Inject constructor(repository: BriefRepository) : ViewModel() {
    val latestBrief = repository.latestBrief
}

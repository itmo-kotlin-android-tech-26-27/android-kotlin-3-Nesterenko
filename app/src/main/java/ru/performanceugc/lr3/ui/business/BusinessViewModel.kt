package ru.performanceugc.lr3.ui.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.performanceugc.lr3.data.BriefRepository
import ru.performanceugc.lr3.domain.model.BusinessProfile
import ru.performanceugc.lr3.domain.model.OWN_BUSINESS

@HiltViewModel
class BusinessViewModel @Inject constructor(
    private val repository: BriefRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    val businesses = repository.businesses
    private val existing = businesses.value.find { it.id == OWN_BUSINESS }
    private val editable = MutableStateFlow(
        BusinessProfile(
            id = OWN_BUSINESS,
            brand = savedState["brand"] ?: existing?.brand.orEmpty(),
            description = savedState["description"] ?: existing?.description.orEmpty(),
            contact = savedState["contact"] ?: existing?.contact.orEmpty(),
        ),
    )
    val draft: StateFlow<BusinessProfile> = editable

    fun update(profile: BusinessProfile) {
        savedState["brand"] = profile.brand
        savedState["description"] = profile.description
        savedState["contact"] = profile.contact
        editable.value = profile
    }

    fun save() {
        val profile = editable.value
        if (profile.brand.isBlank()) return
        repository.saveBusiness(
            profile.copy(
                brand = profile.brand.trim(),
                description = profile.description.trim(),
                contact = profile.contact.trim(),
            ),
        )
    }
}

package ru.performanceugc.lr3.data

import kotlinx.coroutines.flow.StateFlow
import ru.performanceugc.lr3.domain.model.Brief
import ru.performanceugc.lr3.domain.model.BusinessProfile
import ru.performanceugc.lr3.domain.model.Creator

interface BriefRepository {
    val creators: List<Creator>
    val latestBrief: StateFlow<Brief?>
    fun brief(id: String): Brief?
    fun save(brief: Brief)
    val businesses: StateFlow<List<BusinessProfile>>
    fun saveBusiness(profile: BusinessProfile)
}

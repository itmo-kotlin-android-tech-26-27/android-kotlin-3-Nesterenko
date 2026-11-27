package ru.performanceugc.lr3.ui.business

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.performanceugc.lr3.domain.model.BusinessProfile
import ru.performanceugc.lr3.domain.model.OWN_BUSINESS
import ru.performanceugc.lr3.ui.common.Ink
import ru.performanceugc.lr3.ui.common.SectionCard
import ru.performanceugc.lr3.ui.common.StudioButton
import ru.performanceugc.lr3.ui.common.StudioHeader

@Composable
fun BusinessDetails(profile: BusinessProfile?, onBack: () -> Unit, onEdit: () -> Unit) {
    Column {
        StudioHeader("Профиль бизнеса", null, onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
            if (profile == null) {
                Text("Профиль недоступен")
                return@Column
            }
            Text(profile.brand, style = MaterialTheme.typography.headlineLarge, color = Ink)
            SectionCard("О бренде", "", profile.description.ifBlank { "Описание пока не добавлено" })
            SectionCard("Для сотрудничества", "", profile.contact.ifBlank { "Контакт пока не указан" })
            if (profile.id == OWN_BUSINESS) StudioButton("Редактировать", onEdit)
        }
    }
}

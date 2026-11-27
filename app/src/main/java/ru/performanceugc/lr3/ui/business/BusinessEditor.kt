package ru.performanceugc.lr3.ui.business

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.performanceugc.lr3.domain.model.BusinessProfile
import ru.performanceugc.lr3.ui.common.ActionBar
import ru.performanceugc.lr3.ui.common.Glyph
import ru.performanceugc.lr3.ui.common.Ink
import ru.performanceugc.lr3.ui.common.Muted
import ru.performanceugc.lr3.ui.common.ScreenPadding
import ru.performanceugc.lr3.ui.common.StudioButton
import ru.performanceugc.lr3.ui.common.StudioHeader
import ru.performanceugc.lr3.ui.common.cardShape
import ru.performanceugc.lr3.ui.common.studioCard

@Composable
fun BusinessEditor(
    profile: BusinessProfile,
    onBack: () -> Unit,
    update: (BusinessProfile) -> Unit,
    onSave: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        StudioHeader("Мой бизнес", null, onBack)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Профиль бренда", style = MaterialTheme.typography.headlineLarge, color = Ink)
            Text(
                "Описание и контакт будут видны в каталоге бизнесов.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
            )
            Column(
                Modifier.fillMaxWidth().studioCard(24).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                OutlinedTextField(
                    value = profile.brand,
                    onValueChange = { update(profile.copy(brand = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Название бренда") },
                    singleLine = true,
                    shape = cardShape,
                )
                OutlinedTextField(
                    value = profile.description,
                    onValueChange = { update(profile.copy(description = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("О бренде") },
                    minLines = 3,
                    shape = cardShape,
                )
                OutlinedTextField(
                    value = profile.contact,
                    onValueChange = { update(profile.copy(contact = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Контакт для сотрудничества") },
                    singleLine = true,
                    shape = cardShape,
                )
            }
        }
        ActionBar {
            StudioButton(
                "Сохранить профиль",
                onSave,
                enabled = profile.brand.isNotBlank(),
                glyph = Glyph.CHECK,
            )
        }
    }
}

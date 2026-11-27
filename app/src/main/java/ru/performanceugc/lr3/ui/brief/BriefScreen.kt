package ru.performanceugc.lr3.ui.brief

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.performanceugc.lr3.domain.model.BriefField
import ru.performanceugc.lr3.ui.common.ActionBar
import ru.performanceugc.lr3.ui.common.Ink
import ru.performanceugc.lr3.ui.common.Muted
import ru.performanceugc.lr3.ui.common.ScreenPadding
import ru.performanceugc.lr3.ui.common.StudioButton
import ru.performanceugc.lr3.ui.common.StudioHeader
import ru.performanceugc.lr3.ui.common.cardShape
import ru.performanceugc.lr3.ui.common.studioCard

@Composable
fun BriefScreen(
    state: BriefUiState,
    onBack: () -> Unit,
    onUpdate: (BriefField, String) -> Unit,
    onExample: () -> Unit,
    onContinue: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        StudioHeader("Бриф", "Шаг 1 из 3", onBack)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Новый бриф", style = MaterialTheme.typography.headlineLarge, color = Ink)
            Text("Расскажи о продукте и задай бюджет для одного видео.", color = Muted, style = MaterialTheme.typography.bodyMedium)
            Column(Modifier.fillMaxWidth().studioCard(24).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BriefTextField("Название проекта", "Например, запуск новой линейки", state.input.title, BriefField.TITLE, state, onUpdate)
                BriefTextField("Продукт", "Что будем продвигать?", state.input.product, BriefField.PRODUCT, state, onUpdate)
                BriefTextField("Цель ролика", "Что важно показать в кадре?", state.input.goal, BriefField.GOAL, state, onUpdate)
                BriefTextField("Бюджет за ролик", "12000", state.input.budget, BriefField.BUDGET, state, onUpdate)
            }
            TextButton(onClick = onExample, contentPadding = PaddingValues(horizontal = 5.dp)) {
                Text("Заполнить пример", style = MaterialTheme.typography.labelLarge)
            }
        }
        ActionBar { StudioButton("Подобрать креатора", onContinue) }
    }
}

@Composable
private fun BriefTextField(
    label: String,
    placeholder: String,
    value: String,
    field: BriefField,
    state: BriefUiState,
    onUpdate: (BriefField, String) -> Unit,
) {
    val isBudget = field == BriefField.BUDGET
    val error = field in state.invalidFields
    OutlinedTextField(
        value = value,
        onValueChange = { onUpdate(field, it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        suffix = if (isBudget) ({ Text("₽") }) else null,
        isError = error,
        supportingText = if (error) ({ Text(if (isBudget) "Введи положительную сумму в рублях" else "Заполни это поле") }) else null,
        singleLine = field != BriefField.GOAL,
        minLines = if (field == BriefField.GOAL) 2 else 1,
        maxLines = if (field == BriefField.GOAL) 4 else 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isBudget) KeyboardType.Number else KeyboardType.Text,
            imeAction = if (isBudget) ImeAction.Done else if (field == BriefField.GOAL) ImeAction.Default else ImeAction.Next,
        ),
        shape = cardShape,
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFFCFBFE)),
    )
}

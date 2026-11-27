package ru.performanceugc.lr3.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.performanceugc.lr3.domain.model.Category
import ru.performanceugc.lr3.domain.model.Creator
import ru.performanceugc.lr3.ui.common.Avatar
import ru.performanceugc.lr3.ui.common.Badge
import ru.performanceugc.lr3.ui.common.EmptyCard
import ru.performanceugc.lr3.ui.common.Glyph
import ru.performanceugc.lr3.ui.common.Ink
import ru.performanceugc.lr3.ui.common.Muted
import ru.performanceugc.lr3.ui.common.Night
import ru.performanceugc.lr3.ui.common.Pale
import ru.performanceugc.lr3.ui.common.Pine
import ru.performanceugc.lr3.ui.common.ScreenPadding
import ru.performanceugc.lr3.ui.common.StudioGlyph
import ru.performanceugc.lr3.ui.common.StudioHeader
import ru.performanceugc.lr3.ui.common.Violet
import ru.performanceugc.lr3.ui.common.White
import ru.performanceugc.lr3.ui.common.cardShape
import ru.performanceugc.lr3.ui.common.rubles
import ru.performanceugc.lr3.ui.common.studioCard

@Composable
fun CatalogScreen(
    state: CatalogUiState,
    browsing: Boolean,
    onBack: () -> Unit,
    onQuery: (String) -> Unit,
    onCategory: (Category?) -> Unit,
    onBudget: () -> Unit,
    onReset: () -> Unit,
    onCreator: (String?, String) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        StudioHeader(if (browsing) "Каталог" else "Подбор", if (browsing) null else "Шаг 2 из 3", onBack)
        val brief = state.brief
        if (!browsing && brief == null) {
            Column(Modifier.padding(ScreenPadding)) { EmptyCard("Бриф недоступен", "Вернись, чтобы открыть или создать бриф.", "Назад", onBack) }
            return@Column
        }
        LazyColumn(
            contentPadding = PaddingValues(start = ScreenPadding, end = ScreenPadding, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text("Креаторы", style = MaterialTheme.typography.headlineLarge, color = Ink)
                    if (brief != null) Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Night).padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Бюджет брифа", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD9CDF5))
                            Text(brief.title, style = MaterialTheme.typography.titleMedium, color = White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Badge(rubles(brief.budget), Color(0xFFC9B5FF), Night)
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = state.query, onValueChange = onQuery, singleLine = true,
                    placeholder = { Text("Имя, категория или площадка") },
                    leadingIcon = { StudioGlyph(Glyph.SEARCH, Muted) },
                    trailingIcon = if (state.query.isNotEmpty()) ({
                        IconButton(onClick = { onQuery("") }, modifier = Modifier.semantics { contentDescription = "Очистить поиск" }) {
                            StudioGlyph(Glyph.CLOSE, Muted)
                        }
                    }) else null,
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Поиск креаторов" },
                    shape = cardShape,
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = White),
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { ChoiceChip("Все", state.category == null) { onCategory(null) } }
                    items(Category.entries) { category ->
                        ChoiceChip(category.label, category == state.category) { onCategory(category) }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Найдено · ${state.creators.size}", style = MaterialTheme.typography.titleMedium, color = Ink)
                    if (!browsing) ChoiceChip("В бюджете", state.budgetOnly, onBudget)
                }
            }
            if (state.creators.isEmpty()) {
                item { EmptyCard("Креаторы не найдены", "Измени запрос или сбрось фильтры.", "Сбросить фильтры", onReset) }
            }
            items(state.creators, key = { it.id }) { creator ->
                CreatorRow(creator, brief?.selectedCreatorId == creator.id) { onCreator(brief?.id, creator.id) }
            }
        }
    }
}

@Composable
private fun ChoiceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected, onClick = onClick, label = { Text(label) },
        shape = CircleShape,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = White, labelColor = Muted,
            selectedContainerColor = Pale, selectedLabelColor = Violet,
        ),
    )
}

@Composable
private fun CreatorRow(creator: Creator, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().studioCard().clickable(role = Role.Button, onClick = onClick).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(creator)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(creator.name, style = MaterialTheme.typography.titleMedium, color = Ink)
            Text("${creator.category.label} · ${creator.platform}", style = MaterialTheme.typography.bodySmall, color = Muted)
            if (selected) Text("Выбран", style = MaterialTheme.typography.labelMedium, color = Pine)
        }
        Text(rubles(creator.price), style = MaterialTheme.typography.labelMedium, color = Violet)
        StudioGlyph(Glyph.FORWARD, Muted, Modifier.size(18.dp))
    }
}

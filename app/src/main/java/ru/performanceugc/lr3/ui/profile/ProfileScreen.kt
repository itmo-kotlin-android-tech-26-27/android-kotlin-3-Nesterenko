package ru.performanceugc.lr3.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.Locale
import ru.performanceugc.lr3.domain.model.Brief
import ru.performanceugc.lr3.domain.model.Creator
import ru.performanceugc.lr3.ui.common.ActionBar
import ru.performanceugc.lr3.ui.common.Avatar
import ru.performanceugc.lr3.ui.common.EmptyCard
import ru.performanceugc.lr3.ui.common.Glyph
import ru.performanceugc.lr3.ui.common.Ink
import ru.performanceugc.lr3.ui.common.Mint
import ru.performanceugc.lr3.ui.common.Muted
import ru.performanceugc.lr3.ui.common.Night
import ru.performanceugc.lr3.ui.common.Pale
import ru.performanceugc.lr3.ui.common.Pine
import ru.performanceugc.lr3.ui.common.ScreenPadding
import ru.performanceugc.lr3.ui.common.SectionCard
import ru.performanceugc.lr3.ui.common.StudioButton
import ru.performanceugc.lr3.ui.common.StudioGlyph
import ru.performanceugc.lr3.ui.common.StudioHeader
import ru.performanceugc.lr3.ui.common.Violet
import ru.performanceugc.lr3.ui.common.White
import ru.performanceugc.lr3.ui.common.rubles
import ru.performanceugc.lr3.ui.common.studioCard

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    browsing: Boolean,
    onBack: () -> Unit,
    onSelect: () -> Unit,
    onHome: () -> Unit,
    onProject: (String) -> Unit,
) {
    val brief = state.brief
    val creator = state.creator
    Column(Modifier.fillMaxSize()) {
        StudioHeader("Профиль креатора", if (browsing) null else "Шаг 3 из 3", onBack)
        if ((!browsing && brief == null) || creator == null) {
            Column(Modifier.padding(ScreenPadding)) { EmptyCard("Профиль недоступен", "Вернись к каталогу и выбери креатора.", "К каталогу", onBack) }
            return@Column
        }
        val selected = brief?.selectedCreatorId == creator.id
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            if (selected) Confirmation(brief, creator)
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(25.dp))
                    .background(Brush.linearGradient(listOf(Night, Color(0xFF50378E)))).padding(17.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Avatar(creator, 68)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(creator.name, style = MaterialTheme.typography.headlineMedium, color = White)
                        Text(creator.location, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE1D7F6))
                        Text("${creator.category.label} · ${creator.platform}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE1D7F6))
                    }
                }
                HorizontalDivider(color = Color(0xFF756497))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Metric("За ролик", rubles(creator.price), Modifier.weight(1f))
                    Metric("Подписчики", creator.audience, Modifier.weight(1f))
                    Metric("ER по просмотрам", creator.engagement, Modifier.weight(1f))
                }
            }
            Column(Modifier.fillMaxWidth().studioCard().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("О креаторе", color = Ink, style = MaterialTheme.typography.titleMedium)
                Text(creator.about, style = MaterialTheme.typography.bodyMedium, color = Muted)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Примеры работ", style = MaterialTheme.typography.titleLarge, color = Ink)
                creator.projects.forEach { project ->
                    Row(
                        Modifier.fillMaxWidth().studioCard(16).clickable(role = Role.Button) { onProject(project.id) }.padding(11.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.size(33.dp).clip(RoundedCornerShape(9.dp)).background(Pale), contentAlignment = Alignment.Center) {
                            StudioGlyph(Glyph.BRIEF, Violet, Modifier.size(18.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Text(project.title, style = MaterialTheme.typography.titleMedium, color = Ink)
                            Text(project.client, style = MaterialTheme.typography.bodySmall, color = Muted)
                        }
                        StudioGlyph(Glyph.FORWARD, Violet)
                    }
                }
            }
            Text("Метрики портфолио", style = MaterialTheme.typography.titleLarge, color = Ink)
            SectionCard(
                "Работы и оценка",
                "По кейсам в портфолио",
                "${creator.projects.size} работы · ${creator.averageRating?.let { String.format(Locale.forLanguageTag("ru-RU"), "%.1f", it) } ?: "—"}/5 по отзывам",
            )
            SectionCard("Просмотры и сохранения", "Все кейсы портфолио · первые 30 дней",
                "Просмотры: ${creator.totalViews}   ·   Сохранения: ${creator.totalSaves}")
            SectionCard(
                "Как читать ER",
                "Среднее по 10 последним роликам аккаунта",
                "Лайки, комментарии и сохранения делятся на просмотры и умножаются на 100%. Все показатели профиля демонстрационные.",
            )
            Text("Отзывы заказчиков", style = MaterialTheme.typography.titleLarge, color = Ink)
            Text("Демонстрационные отзывы по работам из портфолио", style = MaterialTheme.typography.bodySmall, color = Muted)
            creator.projects.forEach { work ->
                SectionCard("${work.client}   ★ ${work.rating}/5", work.title, work.review)
            }
        }
        ActionBar {
            when {
                brief == null -> StudioButton("К каталогу", onBack, glyph = Glyph.BACK)
                selected -> StudioButton("На главную", onHome)
                state.fitsBudget -> StudioButton("Выбрать креатора", onSelect, glyph = Glyph.CHECK)
                else -> {
                    Text(
                        "Цена выше бюджета ${rubles(brief.budget)}. Выбери другого креатора или измени бриф.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    StudioButton("К каталогу", onBack, glyph = Glyph.BACK)
                }
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(value, color = White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, color = Color(0xFFE1D7F6), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun Confirmation(brief: Brief, creator: Creator) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(21.dp)).background(Mint).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(Pine), contentAlignment = Alignment.Center) {
            StudioGlyph(Glyph.CHECK, White)
        }
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("Креатор выбран", style = MaterialTheme.typography.titleMedium, color = Pine)
            Text(
                "${creator.name} · ${rubles(creator.price)}",
                style = MaterialTheme.typography.bodyMedium, color = Pine,
            )
            Text("Бриф «${brief.title}»", style = MaterialTheme.typography.bodySmall, color = Pine, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

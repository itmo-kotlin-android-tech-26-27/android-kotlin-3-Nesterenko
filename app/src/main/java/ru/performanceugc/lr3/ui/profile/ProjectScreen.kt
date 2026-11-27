package ru.performanceugc.lr3.ui.profile

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
import ru.performanceugc.lr3.domain.model.CreatorProject
import ru.performanceugc.lr3.ui.common.Badge
import ru.performanceugc.lr3.ui.common.Ink
import ru.performanceugc.lr3.ui.common.Muted
import ru.performanceugc.lr3.ui.common.SectionCard
import ru.performanceugc.lr3.ui.common.StudioHeader

@Composable
fun ProjectScreen(project: CreatorProject?, onBack: () -> Unit) {
    Column {
        StudioHeader("Кейс", null, onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            if (project == null) {
                Text("Проект недоступен")
                return@Column
            }
            Text(project.title, style = MaterialTheme.typography.headlineLarge, color = Ink)
            Badge(project.format)
            Text("Демонстрационный кейс", style = MaterialTheme.typography.bodySmall, color = Muted)
            SectionCard("Задача", project.client, project.goal)
            SectionCard("Как снимали", "", project.production)
            SectionCard("Результат", "Первые 30 дней", "${project.views} просмотров · ${project.saves} сохранений")
            SectionCard("Отзыв заказчика", "${project.client} · ★ ${project.rating}/5", project.review)
        }
    }
}

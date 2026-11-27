package ru.performanceugc.lr3.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.performanceugc.lr3.R
import ru.performanceugc.lr3.domain.model.Brief
import ru.performanceugc.lr3.ui.common.Glyph
import ru.performanceugc.lr3.ui.common.HomeLink
import ru.performanceugc.lr3.ui.common.Ink
import ru.performanceugc.lr3.ui.common.Muted
import ru.performanceugc.lr3.ui.common.Night
import ru.performanceugc.lr3.ui.common.Pale
import ru.performanceugc.lr3.ui.common.ScreenPadding
import ru.performanceugc.lr3.ui.common.StudioButton
import ru.performanceugc.lr3.ui.common.StudioGlyph
import ru.performanceugc.lr3.ui.common.Violet
import ru.performanceugc.lr3.ui.common.White
import ru.performanceugc.lr3.ui.common.rubles
import ru.performanceugc.lr3.ui.common.studioCard

@Composable
fun HomeScreen(
    brief: Brief?,
    onBrowse: () -> Unit,
    onBusinesses: () -> Unit,
    onAccount: () -> Unit,
    onNew: () -> Unit,
    onOpen: (String) -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(21.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(Violet), contentAlignment = Alignment.Center) {
                StudioGlyph(Glyph.MARK, White, Modifier.size(20.dp))
            }
            Text("Performance UGC", style = MaterialTheme.typography.titleLarge, color = Ink)
        }
        Box(
            Modifier.fillMaxWidth().heightIn(min = 285.dp)
                .clip(RoundedCornerShape(27.dp)).background(Night),
        ) {
            Image(
                painter = painterResource(R.drawable.hero_creator),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
            Box(
                Modifier.matchParentSize().background(
                    Brush.horizontalGradient(listOf(Color(0xFF1A122B), Color(0xED1A122B), Color.Transparent)),
                ),
            )
            Column(
                Modifier.align(Alignment.BottomStart).fillMaxWidth(0.67f).padding(19.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Найди своего креатора", style = MaterialTheme.typography.displaySmall, color = White)
                Text("Изучи работы авторов или подбери креатора по брифу.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE5DAFF))
            }
        }
        StudioButton("Все креаторы", onBrowse)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HomeLink("Каталог бизнесов", onBusinesses, Modifier.weight(1f).heightIn(min = 76.dp))
            HomeLink("Мой бизнес", onAccount, Modifier.weight(1f).heightIn(min = 76.dp))
        }
        HomeLink("Создать бриф", onNew, Modifier.fillMaxWidth())
        Text("Последний бриф", style = MaterialTheme.typography.titleLarge, color = Ink)
        if (brief == null) {
            Row(
                Modifier.fillMaxWidth().studioCard().padding(17.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BriefTile()
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Брифов пока нет", style = MaterialTheme.typography.titleMedium, color = Ink)
                    Text("Здесь появится последний сохранённый бриф.", style = MaterialTheme.typography.bodyMedium, color = Muted)
                }
            }
        } else {
            Row(
                Modifier.fillMaxWidth().studioCard().clickable(role = Role.Button) { onOpen(brief.id) }.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BriefTile()
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(brief.title, style = MaterialTheme.typography.titleMedium, color = Ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    val status = if (brief.selectedCreatorId == null) "Продолжить подбор" else "Креатор выбран"
                    Text("${rubles(brief.budget)} · $status", style = MaterialTheme.typography.bodySmall, color = Muted)
                }
                StudioGlyph(Glyph.FORWARD, Violet)
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun BriefTile() {
    Box(Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(Pale), contentAlignment = Alignment.Center) {
        StudioGlyph(Glyph.BRIEF, Violet)
    }
}

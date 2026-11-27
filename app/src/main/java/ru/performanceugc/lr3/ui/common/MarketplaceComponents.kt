package ru.performanceugc.lr3.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun HomeLink(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.studioCard(16).clickable(role = Role.Button, onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = Ink)
        StudioGlyph(Glyph.FORWARD, Violet, Modifier.size(16.dp))
    }
}

@Composable
fun SectionCard(title: String, subtitle: String, body: String) {
    Column(Modifier.fillMaxWidth().studioCard().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = Ink)
        if (subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Violet)
        if (body.isNotBlank()) Text(body, style = MaterialTheme.typography.bodyMedium, color = Muted)
    }
}

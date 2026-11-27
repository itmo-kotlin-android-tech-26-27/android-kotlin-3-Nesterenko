package ru.performanceugc.lr3.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal val ScreenPadding = 20.dp

internal val cardShape = RoundedCornerShape(17.dp)

@Composable
internal fun EmptyCard(title: String, description: String, action: String, onAction: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().studioCard().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = Ink)
        Text(description, style = MaterialTheme.typography.bodyMedium, color = Muted)
        TextButton(onClick = onAction, contentPadding = PaddingValues(horizontal = 4.dp)) { Text(action) }
    }
}

@Composable
internal fun ActionBar(content: @Composable () -> Unit) {
    HorizontalDivider(color = Line)
    Column(
        Modifier.fillMaxWidth().background(Paper).padding(horizontal = ScreenPadding, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) { content() }
}

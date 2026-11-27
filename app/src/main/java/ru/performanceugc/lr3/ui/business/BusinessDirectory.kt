package ru.performanceugc.lr3.ui.business

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import ru.performanceugc.lr3.domain.model.BusinessProfile
import ru.performanceugc.lr3.ui.common.Ink
import ru.performanceugc.lr3.ui.common.Muted
import ru.performanceugc.lr3.ui.common.StudioHeader
import ru.performanceugc.lr3.ui.common.studioCard

@Composable
fun BusinessDirectory(businesses: List<BusinessProfile>, onBack: () -> Unit, onOpen: (String) -> Unit) {
    Column {
        StudioHeader("Каталог бизнесов", null, onBack)
        LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Бренды", style = MaterialTheme.typography.headlineLarge, color = Ink) }
            items(businesses, key = { it.id }) { brand ->
                Column(Modifier.fillMaxWidth().studioCard().clickable(role = Role.Button) { onOpen(brand.id) }.padding(16.dp)) {
                    Text(brand.brand, style = MaterialTheme.typography.titleLarge, color = Ink)
                    Text(brand.description, style = MaterialTheme.typography.bodyMedium, color = Muted)
                }
            }
        }
    }
}

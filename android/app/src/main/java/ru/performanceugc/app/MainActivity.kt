package ru.performanceugc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Color(0xFF235B48),
                surface = Color(0xFFF8FAF6),
                secondaryContainer = Color(0xFFD8EDE1),
            )) { UgcApp() }
        }
    }
}

@Composable
private fun UgcApp() {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var creatorId by rememberSaveable { mutableStateOf<Int?>(null) }
    var search by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var platform by rememberSaveable { mutableStateOf("") }
    var maxPrice by rememberSaveable { mutableStateOf("") }
    val screens = rememberSaveableStateHolder()
    val openCreator = creatorId
    BackHandler(enabled = openCreator != null) { creatorId = null }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0; creatorId = null },
                    icon = { Text("▤") },
                    label = { Text("Брифы") },
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1; creatorId = null },
                    icon = { Text("◉") },
                    label = { Text("Креаторы") },
                )
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                openCreator != null -> ProfileScreen(openCreator) { creatorId = null }
                tab == 0 -> screens.SaveableStateProvider("briefs") { BriefScreen() }
                else -> screens.SaveableStateProvider("catalog") {
                    CatalogScreen(
                        search, { search = it }, category, { category = it }, platform, { platform = it },
                        maxPrice, { maxPrice = it }, { creatorId = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun BriefScreen() {
    var title by rememberSaveable { mutableStateOf("") }
    var product by rememberSaveable { mutableStateOf("") }
    var goal by rememberSaveable { mutableStateOf("") }
    var budget by rememberSaveable { mutableStateOf("") }
    var fetching by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var reload by remember { mutableIntStateOf(0) }
    var briefs by remember { mutableStateOf<List<Brief>>(emptyList()) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(reload) {
        fetching = true
        loadError = null
        request({ loadError = it }) { briefs = Api.briefs() }
        fetching = false
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Новый бриф", style = MaterialTheme.typography.headlineLarge)
        Field("Название проекта", title) { title = it }
        Field("Продукт или бренд", product) { product = it }
        OutlinedTextField(
            value = goal, onValueChange = { goal = it }, label = { Text("Цель и задача для креатора") },
            modifier = Modifier.fillMaxWidth(), minLines = 3,
        )
        Field("Бюджет, ₽", budget, numeric = true) { budget = it }
        Button(
            enabled = !saving,
            onClick = {
                val amount = budget.trim().toIntOrNull()
                if (title.trim().length !in 1..120 || product.trim().length !in 1..200 ||
                    goal.trim().length !in 1..2000 || amount == null || amount <= 0) {
                    notice = "Заполните поля и укажите положительный бюджет. Лимиты текста: 120, 200 и 2000 символов."
                } else {
                    saving = true
                    notice = null
                    scope.launch {
                        request({ notice = it }) {
                            val saved = Api.createBrief(title, product, goal, amount)
                            title = ""
                            product = ""
                            goal = ""
                            budget = ""
                            notice = "Бриф №${saved.id} сохранён"
                            reload++
                        }
                        saving = false
                    }
                }
            },
        ) { Text(if (saving) "Сохраняем…" else "Сохранить бриф") }
        notice?.let { Text(it) }
        HorizontalDivider()
        Text("Сохранённые брифы", style = MaterialTheme.typography.titleLarge)
        when {
            fetching -> LinearProgressIndicator(Modifier.fillMaxWidth())
            loadError != null -> Retry(loadError.orEmpty()) { reload++ }
            briefs.isEmpty() -> Text("Список пока пуст")
        }
        briefs.forEach { brief ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(brief.title, style = MaterialTheme.typography.titleMedium)
                    Text(brief.product)
                    Text(brief.goal)
                    Text(rubles(brief.budget), color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

private data class Filters(val search: String = "", val category: String = "", val platform: String = "", val maxPrice: String = "")

@Composable
private fun CatalogScreen(
    search: String, onSearch: (String) -> Unit,
    category: String, onCategory: (String) -> Unit,
    platform: String, onPlatform: (String) -> Unit,
    maxPrice: String, onMaxPrice: (String) -> Unit,
    onCreator: (Int) -> Unit,
) {
    var appliedSearch by rememberSaveable { mutableStateOf("") }
    var appliedCategory by rememberSaveable { mutableStateOf("") }
    var appliedPlatform by rememberSaveable { mutableStateOf("") }
    var appliedPrice by rememberSaveable { mutableStateOf("") }
    val filters = Filters(appliedSearch, appliedCategory, appliedPlatform, appliedPrice)
    var reload by remember { mutableIntStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var creators by remember { mutableStateOf<List<Creator>>(emptyList()) }
    LaunchedEffect(filters, reload) {
        loading = true
        error = null
        request({ error = it }) {
            creators = Api.creators(filters.search, filters.category, filters.platform, filters.maxPrice)
        }
        loading = false
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Каталог креаторов", style = MaterialTheme.typography.headlineLarge)
                Field("Поиск по имени или специализации", search, onChange = onSearch)
                Options("Категория", category, listOf("Красота", "Технологии", "Лайфстайл", "Еда", "Спорт"), onCategory)
                Options("Платформа", platform, listOf("TikTok", "Instagram", "YouTube"), onPlatform)
                Field("Стоимость до, ₽", maxPrice, numeric = true, onChange = onMaxPrice)
                Button(onClick = {
                    appliedSearch = search
                    appliedCategory = category
                    appliedPlatform = platform
                    appliedPrice = maxPrice
                    reload++
                }) { Text("Применить фильтры") }
            }
        }
        when {
            loading -> item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            error != null -> item { Retry(error.orEmpty()) { reload++ } }
            creators.isEmpty() -> item { Text("По этим фильтрам ничего не найдено") }
            else -> items(creators, key = { it.id }) { creator ->
                ElevatedCard(onClick = { onCreator(creator.id) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(creator.name, style = MaterialTheme.typography.titleLarge)
                        Text("${creator.category} · ${creator.platform}")
                        Text(rubles(creator.price), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(id: Int, back: () -> Unit) {
    var creator by remember(id) { mutableStateOf<Creator?>(null) }
    var error by remember(id) { mutableStateOf<String?>(null) }
    var reload by remember(id) { mutableIntStateOf(0) }
    LaunchedEffect(id, reload) {
        error = null
        request({ error = it }) { creator = Api.creator(id) }
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = back) { Text("← К каталогу") }
        val profile = creator
        when {
            error != null -> Retry(error.orEmpty()) { reload++ }
            profile == null -> LinearProgressIndicator(Modifier.fillMaxWidth())
            else -> {
                Text(profile.name, style = MaterialTheme.typography.headlineLarge)
                Text("${profile.category} · ${profile.platform}")
                Text(profile.bio)
                HorizontalDivider()
                Text("Стоимость за ролик · ${rubles(profile.price)}")
                Text("Подписчики · ${number(profile.followers)}")
                Text("Вовлечённость · ${NumberFormat.getNumberInstance(russian).format(profile.engagement)}%")
                HorizontalDivider()
                Text("Портфолио", style = MaterialTheme.typography.titleLarge)
                profile.portfolio.forEach { example -> Text(example) }
            }
        }
    }
}

@Composable
private fun Field(label: String, value: String, numeric: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value, onChange, Modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Number else KeyboardType.Text),
    )
}

@Composable
private fun Options(label: String, value: String, choices: List<String>, onChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) { Text("$label · ${value.ifEmpty { "Любая" }}") }
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            (listOf("") + choices).forEach { choice ->
                DropdownMenuItem(text = { Text(choice.ifEmpty { "Любая" }) }, onClick = {
                    onChange(choice)
                    expanded = false
                })
            }
        }
    }
}

@Composable
private fun Retry(message: String, action: () -> Unit) {
    Text(message, color = MaterialTheme.colorScheme.error)
    OutlinedButton(onClick = action) { Text("Повторить") }
}

private suspend fun request(onError: (String) -> Unit, block: suspend () -> Unit) {
    try {
        block()
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: java.io.IOException) {
        onError("Не удалось подключиться. Проверьте, что сервер запущен.")
    } catch (exception: IllegalStateException) {
        onError(exception.message ?: "Ошибка сервера")
    } catch (_: org.json.JSONException) {
        onError("Сервер вернул данные в неожиданном формате")
    }
}

private val russian = Locale.forLanguageTag("ru-RU")
private fun number(value: Int): String = NumberFormat.getIntegerInstance(russian).format(value)
private fun rubles(value: Int): String = "${number(value)} ₽"

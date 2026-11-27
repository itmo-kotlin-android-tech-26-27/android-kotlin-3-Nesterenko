package ru.performanceugc.lr3.data

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import ru.performanceugc.lr3.domain.model.Brief
import ru.performanceugc.lr3.domain.model.BusinessProfile
import ru.performanceugc.lr3.domain.model.Category
import ru.performanceugc.lr3.domain.model.Creator
import ru.performanceugc.lr3.domain.model.CreatorProject
import ru.performanceugc.lr3.domain.model.OWN_BUSINESS
import ru.performanceugc.lr3.domain.model.Rubles

internal class LocalBriefRepository(
    private val preferences: SharedPreferences,
) : BriefRepository {
    override val creators = listOf(
        Creator(
            "sonya", "Соня Ким", "Санкт-Петербург", Category.BEAUTY, "VK Клипы",
            Rubles(8_500), "24,8 тыс.", "6,4%",
            "Снимаю спокойные обзоры ухода и честные распаковки. В кадре показываю текстуры, нанесение и результат.",
            listOf(
                CreatorProject(
                    "ritual", "Уход в три шага", "Лист", "Обзор · 35 секунд",
                    "Показать порядок нанесения трёх средств.",
                    "Соня сняла утреннюю рутину и добавила макропланы текстур.",
                    32400, 610, 5, "Покупатели стали реже спрашивать, в каком порядке наносить средства.",
                ),
                CreatorProject(
                    "serum", "Сыворотка с витамином C", "Лист", "Демонстрация · 28 секунд",
                    "Показать расход сыворотки на одно применение.",
                    "Два макроплана при дневном свете передали плотность продукта.",
                    21800, 390, 4, "Текстура хорошо видна. В финале попросили дольше показать флакон.",
                ),
            ),
        ),
        Creator(
            "mark", "Марк Левин", "Москва", Category.TECH, "YouTube Shorts",
            Rubles(14_000), "31,2 тыс.", "5,8%",
            "Объясняю технику через повседневные задачи. Делаю обзоры с крупными планами и наглядными сравнениями.",
            listOf(
                CreatorProject(
                    "mic", "Микрофон для домашней студии", "Тон", "Сравнение · 45 секунд",
                    "Сравнить встроенный и внешний микрофоны.",
                    "Марк записал фразу на оба микрофона с одинакового расстояния.",
                    46700, 840, 5, "Разница в звуке понятна даже новичку.",
                ),
                CreatorProject(
                    "camera", "Один день с компактной камерой", "Тон", "Видеодневник · 55 секунд",
                    "Проверить стабилизацию и съёмку в сумерках.",
                    "Автор снял вечерний маршрут и включил исходные фрагменты в обзор.",
                    35200, 420, 5, "Камера показана в тех условиях, о которых спрашивают покупатели.",
                ),
            ),
        ),
        Creator(
            "alina", "Алина Юдина", "Казань", Category.FOOD, "VK Клипы",
            Rubles(10_500), "18,3 тыс.", "7,1%",
            "Готовлю и снимаю вертикальные рецепты на своей кухне. Люблю естественный свет и живой звук.",
            listOf(
                CreatorProject(
                    "breakfast", "Завтрак из четырёх ингредиентов", "Доброе зерно", "Рецепт · 30 секунд",
                    "Предложить быстрый завтрак с хлопьями.",
                    "Алина показала четыре ингредиента и вынесла пропорции в титры.",
                    28700, 1200, 5, "Зрители сохраняли рецепт и присылали свои варианты.",
                ),
                CreatorProject(
                    "coffee", "Домашняя дегустация кофе", "Доброе зерно", "Дегустация · 40 секунд",
                    "Объяснить разницу между двумя обжарками.",
                    "Автор приготовила кофе двумя порциями и сравнила вкус.",
                    19200, 310, 4, "Описание вкуса удалось. В следующем ролике хотим подробнее показать помол.",
                ),
            ),
        ),
        Creator(
            "lena", "Лена Орлова", "Екатеринбург", Category.LIFESTYLE, "Telegram",
            Rubles(12_000), "16,7 тыс.", "8,2%",
            "Снимаю короткие истории о городских привычках, вещах для дома и повседневных сервисах.",
            listOf(
                CreatorProject(
                    "storage", "Вещи для небольшого пространства", "Полка", "До и после · 32 секунды",
                    "Показать хранение в маленькой прихожей.",
                    "Лена сняла полку с одного ракурса до и после организации.",
                    25400, 690, 5, "Хорошо видно, сколько вещей помещается на полке.",
                ),
                CreatorProject(
                    "morning", "Утро без спешки", "Полка", "Видеодневник · 38 секунд",
                    "Показать набор для завтрака в обычной квартире.",
                    "Лена сервировала стол и показала, как посуда складывается после завтрака.",
                    22100, 280, 5, "Живая подача, в кадре хорошо читается размер посуды.",
                ),
            ),
        ),
    ).sortedBy { it.price }

    private val demoBusinesses = listOf(
        BusinessProfile("list", "Лист", "Бренд средств для повседневного ухода.", "hello@list.example"),
        BusinessProfile("ton", "Тон", "Студийное аудио и компактные камеры.", "studio@ton.example"),
        BusinessProfile("zerno", "Доброе зерно", "Хлопья и кофе для домашних завтраков.", "team@zerno.example"),
        BusinessProfile("polka", "Полка", "Предметы для дома и компактного хранения.", "ugc@polka.example"),
    )
    private fun businessList(): List<BusinessProfile> {
        val brand = preferences.getString("business.brand", null) ?: return demoBusinesses
        return demoBusinesses + BusinessProfile(
            OWN_BUSINESS, brand, preferences.getString("business.description", "").orEmpty(),
            preferences.getString("business.contact", "").orEmpty(),
        )
    }

    private val directory = MutableStateFlow(businessList())
    override val businesses: StateFlow<List<BusinessProfile>> = directory

    override fun saveBusiness(profile: BusinessProfile) {
        require(profile.id == OWN_BUSINESS)
        preferences.edit().putString("business.brand", profile.brand)
            .putString("business.description", profile.description)
            .putString("business.contact", profile.contact).apply()
        directory.value = businessList()
    }

    private val currentBrief = MutableStateFlow(preferences.getString(LATEST_BRIEF, null)?.let(::brief))
    override val latestBrief: StateFlow<Brief?> = currentBrief

    override fun brief(id: String): Brief? {
        val value = preferences.getString("brief:$id", null) ?: return null
        val json = JSONObject(value)
        return Brief(
            id = id,
            title = json.getString("title"),
            product = json.getString("product"),
            goal = json.getString("goal"),
            budget = Rubles(json.getInt("budget")),
            selectedCreatorId = json.optString("creator").takeIf(String::isNotEmpty),
        )
    }

    override fun save(brief: Brief) {
        val json = JSONObject()
            .put("title", brief.title)
            .put("product", brief.product)
            .put("goal", brief.goal)
            .put("budget", brief.budget.amount)
            .put("creator", brief.selectedCreatorId)
        preferences.edit()
            .putString("brief:${brief.id}", json.toString())
            .putString(LATEST_BRIEF, brief.id)
            .apply()
        currentBrief.value = brief
    }


}

private const val LATEST_BRIEF = "latestBrief"

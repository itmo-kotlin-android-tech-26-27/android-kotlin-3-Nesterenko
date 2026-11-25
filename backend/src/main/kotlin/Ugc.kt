package com.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.DriverManager

@Serializable
data class BriefDraft(val title: String, val product: String, val goal: String, val budget: Int)

@Serializable
data class Brief(val id: Long, val title: String, val product: String, val goal: String, val budget: Int)

@Serializable
data class Creator(
    val id: Int,
    val name: String,
    val category: String,
    val platform: String,
    val price: Int,
    val followers: Int,
    val engagement: Double,
    val bio: String,
    val portfolio: List<String>,
)

@Serializable
data class ApiError(val message: String)

class BriefRepository(private val url: String) {
    private suspend fun <T> query(block: (java.sql.Connection) -> T): T = withContext(Dispatchers.IO) {
        DriverManager.getConnection(url, "sa", "").use(block)
    }

    suspend fun initialize() = query { connection ->
        connection.createStatement().use {
            it.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS briefs (
                    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    title VARCHAR(120) NOT NULL,
                    product VARCHAR(200) NOT NULL,
                    goal VARCHAR(2000) NOT NULL,
                    budget INT NOT NULL CHECK (budget > 0)
                )
                """.trimIndent()
            )
        }
    }

    suspend fun create(draft: BriefDraft): Brief = query { connection ->
        connection.prepareStatement(
            "INSERT INTO briefs (title, product, goal, budget) VALUES (?, ?, ?, ?)",
            java.sql.Statement.RETURN_GENERATED_KEYS,
        ).use { statement ->
            statement.setString(1, draft.title)
            statement.setString(2, draft.product)
            statement.setString(3, draft.goal)
            statement.setInt(4, draft.budget)
            statement.executeUpdate()
            statement.generatedKeys.use { keys ->
                check(keys.next())
                Brief(keys.getLong(1), draft.title, draft.product, draft.goal, draft.budget)
            }
        }
    }

    suspend fun read(id: Long? = null): List<Brief> = query { connection ->
        val sql = if (id == null) "SELECT * FROM briefs ORDER BY id DESC" else "SELECT * FROM briefs WHERE id = ?"
        connection.prepareStatement(sql).use { statement ->
            if (id != null) statement.setLong(1, id)
            statement.executeQuery().use { rows ->
                buildList {
                    while (rows.next()) {
                        add(Brief(rows.getLong("id"), rows.getString("title"), rows.getString("product"),
                            rows.getString("goal"), rows.getInt("budget")))
                    }
                }
            }
        }
    }
}

private data class CreatorSample(val name: String, val bio: String, val examples: List<String>)

private val categories = listOf("Красота", "Технологии", "Лайфстайл", "Еда", "Спорт")
private val platforms = listOf("TikTok", "Instagram", "YouTube")

val creators = listOf(
    CreatorSample("Алина Морозова", "Снимаю уходовые рутины и тесты косметики крупным планом.", listOf("Утренняя рутина", "Тест тональной основы")),
    CreatorSample("Илья Волков", "Объясняю функции гаджетов через бытовые сценарии.", listOf("Неделя со смарт-часами", "Сравнение наушников")),
    CreatorSample("София Ким", "Показываю бренды через истории о доме и повседневных привычках.", listOf("Рабочий уголок", "Уютный вечер дома")),
    CreatorSample("Денис Орлов", "Готовлю быстрые блюда и снимаю процессы сверху.", listOf("Ужин за 15 минут", "Один продукт, три блюда")),
    CreatorSample("Мария Соколова", "Снимаю домашние тренировки и обзоры экипировки.", listOf("Тренировка с резинкой", "Тест беговых кроссовок")),
    CreatorSample("Дарья Ли", "Делаю разговорные обзоры макияжа при естественном свете.", listOf("Макияж за пять минут", "Тест стойкости помады")),
    CreatorSample("Артём Сафонов", "Снимаю распаковки и короткие инструкции для приложений.", listOf("Распаковка микрофона", "Монтаж на телефоне")),
    CreatorSample("Кристина Белова", "Снимаю прогулки и контент о жизни в городе.", listOf("Маршрут на выходные", "Что беру с собой")),
    CreatorSample("Никита Фёдоров", "Снимаю дегустации и рецепты для продуктовых брендов.", listOf("Слепая дегустация", "Обед с собой")),
    CreatorSample("Полина Андреева", "Снимаю обзоры спортивной одежды и тренировки на улице.", listOf("Разминка перед бегом", "Тест ветровки")),
    CreatorSample("Вера Новикова", "Снимаю обзоры ухода за волосами и укладки.", listOf("Укладка без фена", "Тест маски для волос")),
    CreatorSample("Максим Пак", "Сравниваю аксессуары для съёмки на конкретных примерах.", listOf("Три схемы света", "Мобильный штатив в поездке")),
    CreatorSample("Ольга Миронова", "Снимаю вертикальные видео об организации пространства.", listOf("Разбор шкафа", "Хранение в маленькой кухне")),
    CreatorSample("Евгения Смирнова", "Снимаю выпечку и пошаговые рецепты в домашней кухне.", listOf("Домашняя гранола", "Тесто для пиццы")),
    CreatorSample("Роман Васильев", "Делаю обзоры снаряжения для велопоездок и походов.", listOf("Что в велосумке", "Проверка дождевика")),
).mapIndexed { index, sample ->
    Creator(index + 1, sample.name, categories[index % categories.size], platforms[index % platforms.size],
        4500 + index * 850, 6000 + index * 2900, (25 + index * 3) / 10.0, sample.bio, sample.examples)
}

package ru.performanceugc.lr3.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.performanceugc.lr3.domain.model.Brief
import ru.performanceugc.lr3.domain.model.BriefField
import ru.performanceugc.lr3.domain.model.BriefInput
import ru.performanceugc.lr3.domain.model.Category
import ru.performanceugc.lr3.domain.model.Creator
import ru.performanceugc.lr3.domain.model.Rubles

class BriefRulesTest {
    private val valid = BriefInput("Новый ритуал ухода", "Сыворотка", "Показать текстуру", "12000")
    private val brief = Brief("brief", valid.title, valid.product, valid.goal, Rubles(12_000))
    private val creator = Creator(
        "creator", "Соня Ким", "Санкт-Петербург", Category.BEAUTY, "VK Клипы",
        Rubles(12_000), "24,8 тыс.", "6,4%", "Снимаю обзоры ухода.", emptyList(),
    )

    @Test
    fun rejectsBlankFieldsAndInvalidBudget() {
        assertEquals(BriefField.entries.toSet(), BriefRules.invalidFields(BriefInput()))
        for (budget in listOf("0", "-10", "12.5", "текст", "2147483648")) {
            assertEquals(setOf(BriefField.BUDGET), BriefRules.invalidFields(valid.copy(budget = budget)))
        }
        assertTrue(BriefRules.invalidFields(valid.copy(budget = " 12000 ")).isEmpty())
    }

    @Test
    fun exactBudgetQualifiesAndHigherPriceDoesNot() {
        assertTrue(BriefRules.fitsBudget(brief, creator))
        assertFalse(BriefRules.fitsBudget(brief, creator.copy(price = Rubles(12_001))))
    }

    @Test
    fun filtersCombineAndCanBeCleared() {
        val expensive = creator.copy(id = "expensive", price = Rubles(13_500))
        val different = creator.copy(id = "different", category = Category.FOOD)
        val creators = listOf(creator, expensive, different)

        assertEquals(listOf(creator), filterCreators(creators, brief, Category.BEAUTY, "  клИПЫ ", true))
        assertEquals(emptyList<Creator>(), filterCreators(creators, brief, Category.BEAUTY, "нет такой строки", true))
        assertEquals(creators, filterCreators(creators, brief, null, "", false))
        assertEquals(creators, filterCreators(creators, null, null, "", true))
    }
}

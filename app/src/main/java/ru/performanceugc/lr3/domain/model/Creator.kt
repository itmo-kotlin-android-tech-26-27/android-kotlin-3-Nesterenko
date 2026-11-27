package ru.performanceugc.lr3.domain.model

data class Creator(
    val id: String,
    val name: String,
    val location: String,
    val category: Category,
    val platform: String,
    val price: Rubles,
    val audience: String,
    val engagement: String,
    val about: String,
    val projects: List<CreatorProject>,
) {
    val totalViews: Int
        get() = projects.sumOf { it.views }

    val totalSaves: Int
        get() = projects.sumOf { it.saves }

    val averageRating: Double?
        get() = projects.takeIf { it.isNotEmpty() }?.map { it.rating }?.average()
}

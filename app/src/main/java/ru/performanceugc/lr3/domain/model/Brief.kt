package ru.performanceugc.lr3.domain.model

data class Brief(
    val id: String,
    val title: String,
    val product: String,
    val goal: String,
    val budget: Rubles,
    val selectedCreatorId: String? = null,
)

package ru.performanceugc.lr3.domain.model

data class CreatorProject(
    val id: String,
    val title: String,
    val client: String,
    val format: String,
    val goal: String,
    val production: String,
    val views: Int,
    val saves: Int,
    val rating: Int,
    val review: String,
)

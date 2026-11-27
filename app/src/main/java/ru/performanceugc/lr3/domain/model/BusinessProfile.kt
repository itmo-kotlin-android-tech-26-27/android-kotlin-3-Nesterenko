package ru.performanceugc.lr3.domain.model

internal const val OWN_BUSINESS = "local"

data class BusinessProfile(
    val id: String,
    val brand: String,
    val description: String,
    val contact: String,
)

package ru.ifmo.models

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val id: String,
    val title: String,
    val total: Int,
    val statistic: Map<String, Int>,
)

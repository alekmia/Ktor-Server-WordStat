package ru.ifmo.models

import kotlinx.serialization.Serializable

@Serializable
data class FindResult(val statistics: List<Result>)

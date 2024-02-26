package ru.ifmo.models

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val status: State,
) {
    enum class State { COMPLETED, PROCESSING }

    companion object {
        val COMPLETED = Status(State.COMPLETED)
        val PROCESSING = Status(State.PROCESSING)
    }
}

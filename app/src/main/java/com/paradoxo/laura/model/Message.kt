package com.paradoxo.laura.model

data class Message(
    val text: String = "",
    val status: Status = Status.AI,
)
enum class Status {
    LOAD, USER, AI
}
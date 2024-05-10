package com.paradoxo.laura.model

import android.graphics.Bitmap

data class Message(
    val text: String = "",
    val status: Status = Status.AI,
    val imagesList: List<Bitmap> = emptyList()
)
enum class Status {
    LOAD, USER, AI
}
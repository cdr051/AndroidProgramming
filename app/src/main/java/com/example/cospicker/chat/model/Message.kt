package com.example.cospicker.chat.model

data class Message(
    val senderId: String = "",
    val message: String = "",
    val time: Long = 0L
)

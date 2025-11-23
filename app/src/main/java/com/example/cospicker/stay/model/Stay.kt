package com.example.cospicker.stay.model

import java.io.Serializable

data class Stay(
    val name: String = "",
    val address: String = "",
    val price: Int = 0,
    val imageUrl: String = ""
) : Serializable

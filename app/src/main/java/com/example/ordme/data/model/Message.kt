package com.example.ordme.data.model

import java.sql.Timestamp

data class Message(
    val title: String? = null,
    val info: String? = null,
    val message: String? = null,
    val read: Boolean? = null,
    val uid: String? = null,
    val time: Timestamp? = null
)
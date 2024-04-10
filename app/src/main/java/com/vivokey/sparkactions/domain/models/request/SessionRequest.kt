package com.vivokey.sparkactions.domain.models.request

data class SessionRequest(
    val uid: String,
    val response: String,
    val token: String,
    val cld: String? = null
)

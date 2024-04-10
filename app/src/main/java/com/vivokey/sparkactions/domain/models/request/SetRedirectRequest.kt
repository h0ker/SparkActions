package com.vivokey.sparkactions.domain.models.request

data class SetRedirectRequest(
    val jwt: String,
    val title: String,
    val url: String,
    val target: String,
    val delay: Int,
    val aj: Boolean
)
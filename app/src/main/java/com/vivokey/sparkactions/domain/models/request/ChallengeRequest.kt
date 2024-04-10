package com.vivokey.sparkactions.domain.models.request

data class ChallengeRequest(
    val scheme: Int,
    val message: String? = null,
    val uid: String? = null
)

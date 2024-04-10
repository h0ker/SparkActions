package com.vivokey.sparkactions.domain.models

import com.vivokey.sparkactions.domain.models.request.ChallengeRequest
import com.vivokey.sparkactions.domain.models.request.SessionRequest
import com.vivokey.sparkactions.domain.models.response.ChallengeResponse
import com.vivokey.sparkactions.domain.models.response.SessionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("challenge")
    suspend fun postChallenge(@Body challengeRequest: ChallengeRequest): Response<ChallengeResponse>

    @POST("session")
    suspend fun postSession(@Body sessionRequest: SessionRequest): Response<SessionResponse>
}
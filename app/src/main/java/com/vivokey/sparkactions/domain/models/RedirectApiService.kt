package com.vivokey.sparkactions.domain.models

import com.vivokey.sparkactions.domain.models.request.GetRedirectRequest
import com.vivokey.sparkactions.domain.models.request.SetRedirectRequest
import com.vivokey.sparkactions.domain.models.response.GetRedirectResponse
import com.vivokey.sparkactions.domain.models.response.SetRedirectResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RedirectApiService {

    @POST("set")
    suspend fun setRedirect(@Body setRedirectRequest: SetRedirectRequest): Response<SetRedirectResponse>

    @POST("get")
    suspend fun getRedirect(@Body getRedirectRequest: GetRedirectRequest): Response<GetRedirectResponse>
}
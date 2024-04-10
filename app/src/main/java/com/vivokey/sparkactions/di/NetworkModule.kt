package com.vivokey.sparkactions.di

import com.vivokey.sparkactions.domain.models.AuthApiService
import com.vivokey.sparkactions.domain.models.RedirectApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val AUTH_BASE_URL = "https://auth.vivokey.com/"
    private const val REDIRECT_BASE_URL = "https://redirect.vivokey.co/"
    private const val API_HEADER = "X-API-VIVOKEY"
    private const val API_KEY = "9e084e64-eb74-41b8-a87d-4c0bdcd1be64"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .header(API_HEADER, API_KEY)
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    @Provides
    @Singleton
    @Named("AuthRetrofit")
    fun provideAuthRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@Named("AuthRetrofit") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("RedirectRetrofit")
    fun provideRedirectRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(REDIRECT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRedirectApiService(@Named("RedirectRetrofit") retrofit: Retrofit): RedirectApiService {
        return retrofit.create(RedirectApiService::class.java)
    }
}
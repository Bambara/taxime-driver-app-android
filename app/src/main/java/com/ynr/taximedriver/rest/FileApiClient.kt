package com.ynr.taximedriver.rest

import com.ynr.taximedriver.config.KeyString
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object FileApiClient {
    private const val BASE_URL = KeyString.BASE_URL + ":" + KeyString.BASE_SOCKET
    private lateinit var retrofit: Retrofit

    @JvmStatic
    fun getApiClient(): Retrofit {
        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(5, TimeUnit.MINUTES)
        httpClient.writeTimeout(5, TimeUnit.MINUTES)
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Content-Type", "multipart/form-data")
                .build()
            chain.proceed(request)
        }
        val client = httpClient.build()
        retrofit =
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
        return retrofit
    }
}
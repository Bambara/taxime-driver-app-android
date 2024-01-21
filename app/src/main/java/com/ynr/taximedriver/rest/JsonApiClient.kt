package com.ynr.taximedriver.rest

import android.util.Log
import com.ynr.taximedriver.config.KeyString
import okhttp3.*
import okhttp3.Interceptor.Chain
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object JsonApiClient {
    private const val BASE_URL = KeyString.BASE_URL + ":" + KeyString.BASE_SOCKET

    //    private const val BASE_PAYMENT_URL = KeyString.BASE_PAYMENT_URL
    var retrofit: Retrofit? = null
    var retrofitPayment: Retrofit? = null


    class LogJsonInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Chain): Response {
            val request: Request = chain.request()
            val response: Response = chain.proceed(request)
            val rawJson: String? = response.body()?.string()
            Log.d(
                "JsonApiClient",
                String.format("raw JSON response is: %s", rawJson)
            )

            // Re-create the response before returning it because body can be read only once
            return response.newBuilder()
                .body(ResponseBody.create(response.body()?.contentType(), rawJson)).build()
        }
    }


    @JvmStatic
    val apiClient: Retrofit
        get() {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header(
                        "Content-Type",
                        "application/json",
                    ) //.header("Authorization", "JWT eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImNoYXRodXJhMDcwQGdtYWlsLmNvbSIsImZpcnN0TmFtZSI6ImNoYXRodXJhIiwibGFzdE5hbWUiOiJsaXlhbmFnZSIsIl9pZCI6IjViYzRlMTE1Y2Y0MTkxMzgxZDgwODdjNSIsImlhdCI6MTUzOTYyOTU1Mn0.kB-u4g2tktbZlGB1WcOzaZLuScqXRG876LFTOF_7MRY")
                    //method(original.method(), original.body())
                    //.header("Connection", "close")
                    .build()
                chain.proceed(request)
            }
            val client = OkHttpClient.Builder()


            client.connectTimeout(30, TimeUnit.SECONDS)
            client.writeTimeout(30, TimeUnit.SECONDS)
            client.readTimeout(30, TimeUnit.SECONDS)
            client.interceptors().add(LogJsonInterceptor())


            if (retrofit == null) {
                retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).client(client.build())
                    .build()
            }
            return retrofit!!
        }

    @JvmStatic
    val paymentApiClient: Retrofit
        get() {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Content-Type", "application/json")
//                    .method(
//                        original.method(),
//                        original.body()
//                    ) //.header("Authorization", "JWT eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImNoYXRodXJhMDcwQGdtYWlsLmNvbSIsImZpcnN0TmFtZSI6ImNoYXRodXJhIiwibGFzdE5hbWUiOiJsaXlhbmFnZSIsIl9pZCI6IjViYzRlMTE1Y2Y0MTkxMzgxZDgwODdjNSIsImlhdCI6MTUzOTYyOTU1Mn0.kB-u4g2tktbZlGB1WcOzaZLuScqXRG876LFTOF_7MRY")
                    //method(original.method(), original.body())
                    //.header("Connection", "close")
                    .build()
                chain.proceed(request)
            }
            val client = OkHttpClient.Builder()

            client.connectTimeout(5, TimeUnit.MINUTES)
            client.writeTimeout(5, TimeUnit.MINUTES)
            client.readTimeout(5, TimeUnit.MINUTES)
            client.interceptors().add(LogJsonInterceptor())


            if (retrofitPayment == null) {
                retrofitPayment =
                    Retrofit.Builder().baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create()).client(client.build())
                        .build()
            }
            return retrofitPayment!!
        }
}
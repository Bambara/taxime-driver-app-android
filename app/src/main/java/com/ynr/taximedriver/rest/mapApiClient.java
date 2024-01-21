package com.ynr.taximedriver.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class mapApiClient {
    public static final String BASE_URL = "https://maps.googleapis.com/maps/";
    public static Retrofit retrofit = null;

    public static Retrofit getApiClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        //.header("Content-Type", "application/json")
                        //.header("Authorization", "JWT eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImNoYXRodXJhMDcwQGdtYWlsLmNvbSIsImZpcnN0TmFtZSI6ImNoYXRodXJhIiwibGFzdE5hbWUiOiJsaXlhbmFnZSIsIl9pZCI6IjViYzRlMTE1Y2Y0MTkxMzgxZDgwODdjNSIsImlhdCI6MTUzOTYyOTU1Mn0.kB-u4g2tktbZlGB1WcOzaZLuScqXRG876LFTOF_7MRY")
                        //method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();

        if(retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(client).build();
        }
        return retrofit;
    }
}

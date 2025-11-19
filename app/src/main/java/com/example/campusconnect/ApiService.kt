package com.example.campusconnect

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("campusconnect_server.php") // The name of your PHP file
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>
}

object ApiClient {
    // IF USING EMULATOR: Use "http://10.0.2.2:8080/"
    // IF USING REAL PHONE: Use "http://YOUR_LAPTOP_IP:8080/"
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
package com.example.projectprodia

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/v4/articles/?format=json")
    fun getArticles(): Call<ApiResponse>
}


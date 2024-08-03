package com.example.projectprodia

import com.google.gson.annotations.SerializedName
import java.util.Date

//data class ResponseModel (
//    val id: Int,
//    val title:String,
//    val news_site:String,
//    val image_url: String,
//    val published_at: Date,
//    val summary: String
//)

data class ApiResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<ResponseModel>
)

data class ResponseModel(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("news_site") val newsSite: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("published_at") val publishedAt: String,
)
package com.nepaltech.news.depot.api

import com.google.gson.annotations.SerializedName

data class AllNews(
    @SerializedName("status")
    val status: String,

    @SerializedName("totalResults")
    val totalNewsCount: Int,

    @SerializedName("articles")
    val newsList: List<NewsPost>
) {
}
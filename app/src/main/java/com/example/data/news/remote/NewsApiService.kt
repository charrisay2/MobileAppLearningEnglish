package com.example.data.news.remote

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

@Serializable
data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticleDto>
)

@Serializable
data class NewsArticleDto(
    val source: SourceDto,
    val author: String? = null,
    val title: String,
    val description: String? = null,
    val url: String,
    val urlToImage: String? = null,
    val publishedAt: String,
    val content: String? = null
)

@Serializable
data class SourceDto(
    val id: String? = null,
    val name: String
)

interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String?,
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String
    ): NewsResponse

    companion object {
        const val BASE_URL = "https://newsapi.org/"
    }
}

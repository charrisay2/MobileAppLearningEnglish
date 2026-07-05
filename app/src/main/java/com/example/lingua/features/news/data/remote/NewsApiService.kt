package com.example.lingua.features.news.data.remote

import com.example.lingua.features.news.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    
    @GET("svc/search/v2/articlesearch.json")
    suspend fun searchArticles(
        @Query("q") query: String?,
        @Query("fq") filterQuery: String?, // Dùng để lọc section_name
        @Query("page") page: Int,
        @Query("api-key") apiKey: String
    ): NewsResponseDto

    companion object {
        const val BASE_URL = "https://api.nytimes.com/"
    }
}

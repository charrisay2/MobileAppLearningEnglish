package com.example.data.news.repository

import com.example.domain.news.Article
import com.example.domain.news.NewsRepository
import com.example.data.news.remote.NewsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.BuildConfig

class NewsRepositoryImpl(
    private val apiService: NewsApiService
) : NewsRepository {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    
    override fun getArticles(category: String?): Flow<List<Article>> {
        // In a real app, this would fetch from a database. 
        // For simplicity, we return the current state and trigger a refresh.
        return _articles.asStateFlow()
    }

    override suspend fun refreshArticles(category: String?) {
        try {
            val response = apiService.getTopHeadlines(
                category = category?.lowercase() ?: "general",
                apiKey = BuildConfig.NEWS_API_KEY
            )
            
            val domainArticles = response.articles.map { dto ->
                Article(
                    id = dto.url,
                    title = dto.title,
                    source = dto.source.name,
                    content = dto.description ?: dto.content ?: "",
                    timeToRead = "5 min",
                    difficulty = "B1",
                    tags = listOf(category ?: "General"),
                    articleUrl = dto.url,
                    publishDate = dto.publishedAt
                )
            }
            _articles.value = domainArticles
        } catch (e: Exception) {
            android.util.Log.e("NewsRepo", "Error fetching news: ${e.message}")
        }
    }
}
